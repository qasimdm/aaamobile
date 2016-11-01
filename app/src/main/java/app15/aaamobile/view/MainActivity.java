package app15.aaamobile.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import app15.aaamobile.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_REPAIR = "repair";
    private static final String TAG_PRICES = "prices";
    private static final String TAG_LOGIN = "login";
    private static final String TAG_CONTACT = "contact";
    private static final String TAG_ABOUT = "about";
    public static String CURRENT_TAG = TAG_HOME;
    private static int navItemIndex = 0;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler mHandler;
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Default fragment is HomeFragment when user starts the app
        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        loadNavigatedFragment(new HomeFragment());


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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
            return true;
        } else if (id == R.id.nav_contact_us) {
            ContactFragment contactFragment = new ContactFragment();
            CURRENT_TAG = TAG_CONTACT;
            navItemIndex = 4;
            loadNavigatedFragment(contactFragment);
        } else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            CURRENT_TAG = TAG_ABOUT;
            navItemIndex = 5;
            loadNavigatedFragment(aboutFragment);
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
