package app15.aaamobile.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import app15.aaamobile.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_REPAIR = "repair";
    private static final String TAG_PRICES = "prices";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_LOGOUT = "logout";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_ABOUT = "about";
    public static String CURRENT_TAG = TAG_HOME;
    private static final String TAG = "MainActivity";
    private static int navItemIndex = 0;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler mHandler;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private FirebaseUser mFirebaseUser;

    private DrawerLayout drawer;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Default fragment is HomeFragment when user starts the app
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        loadNavigatedFragment(new HomeFragment());

        // Firebase Authentication state listener
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //get reference to the header view
                View headerView = navigationView.getHeaderView(0);
                TextView navHeaderUserEmail = (TextView) headerView.findViewById(R.id.nav_header_email);
                if (user != null) {
                    // User is signed in
                    Log.i(TAG, "onAuthStateChanged:signed_in:" + user.getEmail().toString());
                    if (user.getEmail().toString() != null) {
                        navHeaderUserEmail.setText(user.getEmail().toString());

                    }//inner if
                }//outer if
                else {
                    // User is signed out
                    navHeaderUserEmail.setText("Guest");
                    Log.i(TAG, "onAuthStateChanged:signed_out" );
                }//else
            }//onAuthStateChanged
        };
        //Initilize firebase and check if the user is signed in otherwise popup sign in view
        initFirebaseAndCheckIfSignedIn();
    }
    private void loadNavigatedFragment(final Fragment fragment) {
        // selecting appropriate nav menu item
        //selectNavMenu();
        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment currentFragment = fragment;
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void initFirebaseAndCheckIfSignedIn(){
        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        Menu menu = navigationView.getMenu();
        MenuItem menuItemLogin = menu.findItem(R.id.nav_login);
        MenuItem menuItemLogout = menu.findItem(R.id.nav_logout);

        if (mFirebaseUser == null) {
            // Not signed in
            menuItemLogin.setVisible(true);
            menuItemLogout.setVisible(false);
            //startActivity(new Intent(this, LoginActivity.class));
            //finish();

            return;
        } else {
            menuItemLogin.setVisible(false);
            menuItemLogout.setVisible(true);
            String mUsername = mFirebaseUser.getDisplayName();
            //add profile pic funtionality later on
            if (mFirebaseUser.getPhotoUrl() != null) {
               String mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mFirebaseAuthListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mFirebaseAuthListener != null){
            mFirebaseAuth.removeAuthStateListener(mFirebaseAuthListener);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:

                /*mFirebaseAuth.signOut();
                mFirebaseAuth.GoogleSignInApi.signOut(mGoogleApiClient);
                mUsername = ANONYMOUS;
                startActivity(new Intent(this, LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);*/
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            CURRENT_TAG = TAG_HOME;
            navItemIndex = 0;
            loadNavigatedFragment(homeFragment);
        } else if (id == R.id.nav_repair) {
            RepairFragment repairFragment = new RepairFragment();
            CURRENT_TAG = TAG_REPAIR;
            navItemIndex = 1;
            loadNavigatedFragment(repairFragment);
        } else if (id == R.id.nav_prices) {
            PricesFragment pricesFragment = new PricesFragment();
            CURRENT_TAG = TAG_PRICES;
            navItemIndex = 2;
            loadNavigatedFragment(pricesFragment);
        } else if (id == R.id.nav_login) {
            CURRENT_TAG = TAG_LOGIN;
            navItemIndex = 3;
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            drawer.closeDrawers();
            finish();
            return true;
        } else if (id == R.id.nav_logout) {
            CURRENT_TAG = TAG_LOGOUT;
            navItemIndex = 4;
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                            finish();
                        }
                    });

            drawer.closeDrawers();
            finish();
            return true;
        } else if (id == R.id.nav_contact_us) {
            ContactFragment contactFragment = new ContactFragment();
            CURRENT_TAG = TAG_CONTACT;
            navItemIndex = 5;
            loadNavigatedFragment(contactFragment);
        } else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            CURRENT_TAG = TAG_ABOUT;
            navItemIndex = 6;
            loadNavigatedFragment(aboutFragment);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
