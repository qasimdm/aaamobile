package app15.aaamobile.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.DatabaseController;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private final String TABLE_USER = "users";
    // tags used to attach the fragments
    private final String TAG_HOME = "home";
    private final String TAG_REPAIR = "repair";
    private final String TAG_PRICES = "prices";
    private final String TAG_ORDER = "order";
    private final String TAG_MY_ACCOUNT = "my account";
    private final String TAG_CONTACT = "contact";
    private final String TAG_ABOUT = "about";
    private final String TAG_SHOPPING_CART = "shopping cart";
    private String CURRENT_TAG = TAG_HOME;
    private final String TAG = "MainActivity";
    //Navigation bar items indexing
    private final int homeItem = 0;
    private final int repairItem = 1;
    private final int pricesItem = 2;
    private final int accountItem = 3;
    private final int orderItem = 4;
    private final int contactUsItem = 5;
    private final int aboutItem = 6;
    private final int cartItem = 7;

    //Cart item counter
    public static int mNotificationCount = 0;
    private static int navItemIndex = 0;
    private boolean doubleBackToExitPressedOnce = false;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    private Handler mHandler;
    // Firebase member variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mFirebaseAuthListener;
    private FirebaseUser mFirebaseUser;

    private DrawerLayout drawer;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //drawer.setDrawerListener(toggle);   // TODO: 2016-11-30 change to addDrawerListener
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mHandler = new Handler();
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        loadNavigatedFragment(new HomeFragment(), CURRENT_TAG, homeItem);   //Opens home fragment everytime user closes and then resumes the app

        // Firebase Authentication state listener
        mFirebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "in onAuthStateChanged Listener");
                initFirebaseAndCheckIfSignedIn();
            }//onAuthStateChanged
        };
        //Initilize firebase and check if the user is signed in otherwise welcome Guest
        Log.i(TAG, "Before initFirebaseAndCheckIfSignedIn");
        initFirebaseAndCheckIfSignedIn();   // TODO: 2016-12-01 optimization, comment it 
        setupDatabase();
    }
    private void loadNavigatedFragment(final Fragment fragment, String fragmentTag, int currentItemIndex) {
        CURRENT_TAG = fragmentTag;
        navItemIndex = currentItemIndex;
        setToolbarTitle();
        // if user select the current navigation menu again, don't do anything
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }
        // using runnable, the fragment is loaded with cross fade effect
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // updates the contents by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                //fragmentTransaction.setCustomAnimations(android.R.anim.slide_out_right, android.R.anim.slide_out_right);
                //fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
        //Navigation bar items references
        MenuItem menuItemLogin = menu.findItem(R.id.nav_login);
        MenuItem menuItemLogout = menu.findItem(R.id.nav_logout);
        MenuItem menuItemMyAccount = menu.findItem(R.id.nav_my_account);
        MenuItem menuItemOrders = menu.findItem(R.id.nav_orders);
        View headerView = navigationView.getHeaderView(0);
        TextView navHeaderUserEmail = (TextView) headerView.findViewById(R.id.nav_header_email);

        // Not signed in OR when user logs out
        if (mFirebaseUser == null) {
            menuItemLogin.setVisible(true);
            menuItemLogout.setVisible(false);
            menuItemMyAccount.setVisible(false);
            menuItemOrders.setVisible(false);
            navHeaderUserEmail.setText("Guest");
            loadNavigatedFragment(new HomeFragment(), TAG_HOME, homeItem);  //Load default Home fragment
            CartController cartController = new CartController();
            cartController.clearCart();                                     //Remove all items from the cart
            mNotificationCount = cartController.getOrdersCount();
        }
        //Signed in
        else {
            menuItemLogin.setVisible(false);
            menuItemLogout.setVisible(true);
            menuItemMyAccount.setVisible(true);
            if (mFirebaseUser.getEmail().toString() != null) {
                navHeaderUserEmail.setText(mFirebaseUser.getEmail().toString());
            }
            String mUsername = mFirebaseUser.getDisplayName();
            //add profile pic funtionality later on
            if (mFirebaseUser.getPhotoUrl() != null) {
               String mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
        setToolbarTitle();
        invalidateOptionsMenu();    //re draw the action toolbar
    }

    private void setupDatabase(){
        MenuItem menuItemOrder = navigationView.getMenu().findItem(R.id.nav_orders);
        DatabaseController databaseController = new DatabaseController();

        if (mFirebaseUser != null) {
            databaseController.setDatabaseReference(TABLE_USER);
            databaseController.readOnce(mFirebaseUser.getUid(), menuItemOrder); //skickar med order menu ref, att visa den om det är en Admin
            //databaseController.readUserOrder(mFirebaseUser.getUid());
            //databaseController.readOrders();
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {  //Double click to close
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please press BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
            //super.onBackPressed();
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
        Drawable mDrawable = getResources().getDrawable(R.drawable.ic_shopping_cart_24dp);
        if (mNotificationCount>0) {
            mDrawable.setTint(Color.GREEN);     // TODO: 2016-11-24 change cart icon from background to src in xml and try setColorFilter
            //mDrawable.setColorFilter(new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN));
        }
        else{
            mDrawable.setTint(Color.WHITE);
        }
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_cart);
        //MenuItemCompat.setActionView(item, R.layout.actionbar_badge_layout);
        RelativeLayout badgeLayout = (RelativeLayout)item.getActionView();
        ImageButton iconCart = (ImageButton)badgeLayout.findViewById(R.id.badge_icon_button);
        TextView tvBadge = (TextView)badgeLayout.findViewById(R.id.badge_textView);

        mDrawable.mutate();
        if (mNotificationCount>0) {
            tvBadge.setText("" + mNotificationCount);
            tvBadge.setVisibility(View.VISIBLE);
        }
        else {
            tvBadge.setVisibility(View.GONE);
        }

        iconCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CartViewFragment cartViewFragment = new CartViewFragment();
                loadNavigatedFragment(cartViewFragment, TAG_SHOPPING_CART, cartItem);
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
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
            loadNavigatedFragment(homeFragment, TAG_HOME, homeItem);
        }
        else if (id == R.id.nav_repair) {
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            if (mFirebaseUser == null){
                drawer.closeDrawers();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return true;
            }
            else {
                RepairFragment repairFragment = new RepairFragment();
                loadNavigatedFragment(repairFragment, TAG_REPAIR, repairItem);
            }
        }
        else if (id == R.id.nav_prices) {
            PricesFragment pricesFragment = new PricesFragment();
            loadNavigatedFragment(pricesFragment, TAG_PRICES, pricesItem);
        }
        else if (id == R.id.nav_orders){
            OrdersFragment ordersFragment = new OrdersFragment();
            loadNavigatedFragment(ordersFragment, TAG_ORDER, orderItem);
        }
        else if (id == R.id.nav_my_account){
            MyAccountFragment myAccountFragment = new MyAccountFragment();
            loadNavigatedFragment(myAccountFragment, TAG_MY_ACCOUNT, accountItem);
        }
        else if (id == R.id.nav_login) {
            drawer.closeDrawers();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return true;
        } else if (id == R.id.nav_logout) {
            //navItemIndex = 4;
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Toast.makeText(MainActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                        }
                    });

            drawer.closeDrawers();
            return true;
        } else if (id == R.id.nav_contact_us) {
            ContactFragment contactFragment = new ContactFragment();
            loadNavigatedFragment(contactFragment, TAG_CONTACT, contactUsItem);
        } else if (id == R.id.nav_about) {
            AboutFragment aboutFragment = new AboutFragment();
            loadNavigatedFragment(aboutFragment, TAG_ABOUT, aboutItem);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
