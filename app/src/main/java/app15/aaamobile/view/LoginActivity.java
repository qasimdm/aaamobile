/**
 * A login screen that offers login via email/password, Gmail and register a new account.
 */
package app15.aaamobile.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import app15.aaamobile.R;
import app15.aaamobile.controller.DatabaseController;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener { //LoaderCallbacks<Cursor>,

    private final String TAG = "LoginActivity";
    private final int RC_SIGN_IN = 9001;

    //Keep track of the login task to ensure we can cancel it if requested.
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mEmailSignInButton, mSignUpButton;

    private GoogleApiClient mGoogleApiClient;
    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    //Database handler
    private DatabaseController databaseController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupActionBar();

        //getting UI references
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        com.google.android.gms.common.SignInButton gmailSignInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.gmail_sign_in_button);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        initDatabaseController();
        setupGoogleApiClient();

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        //Button click listeners
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {    //Sign in button clicked, invokes attemptLogin, checks for errors and authenticates the user
                attemptLogin();
            }
        });
        gmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(mEmailView.getText().toString(), mPasswordView.getText().toString());
            }
        });

    }
    //Setting up Google Api Client
    private void setupGoogleApiClient(){
        //GoogleApiClient settings
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }
    //Initilizing database controller for Firebase database
    private void initDatabaseController() {
        //String key = mFirebaseAuth.getCurrentUser().getUid();
        databaseController = new DatabaseController();
        databaseController.setDatabaseReference("users");
        //databaseController.readData(key);
    }

    //Step 1. Sign in with google, starts an intent for login google view
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //Step 2. Google authentication, if success, then triggers firebaseAuthWithGoogle
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showProgress(true);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign-in was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                showProgress(false);
                // Google Sign In failed
                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Step 3. Google authentication with firebase, if success then saves the credentials in the database
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user.
                        if (!task.isSuccessful()) {
                            showProgress(false);
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        //If sign in succeeds, the auth state listener will be notified
                        else {
                            //save in the database
                            writeToFirebaseDatabase("");    //"" = no password, as we can not access user's google account password
                            //Welcome user, and start main activity
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            Toast.makeText(LoginActivity.this, "Welcome " + user.getEmail(), Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    }
                    //handleFirebaseAuthResult(AuthResult);
                });
    }//END fireBaseAuthWithGoogle

    //Create new account using Email and Password
    private void createAccount(final String email, final String password) {
        if (!validateForm()) {
            return;
        }
        showProgress(true);     //Show a progressbar until a new user account created
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user.
                        if (task.isSuccessful()) {
                            writeToFirebaseDatabase(password);
                            attemptLogin();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }//END createAccount

    //end createUserWithEmailAndPassword
    private void writeToFirebaseDatabase(String password) {
        String uid = mFirebaseAuth.getCurrentUser().getUid();
        String email = mFirebaseAuth.getCurrentUser().getEmail();
        String name = mFirebaseAuth.getCurrentUser().getDisplayName();
        if (name == null) {
            name = "";
        }
        databaseController.writeUserData(uid, email, name, password, false);
    }

    // Set up the {@link android.app.ActionBar}, if the API is available.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    // Validates the whole login form for errors
    private boolean validateForm() {
        boolean valid = true;
        View focusView = null;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        //Validate the entered email, if user entered one
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            valid = false;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            valid = false;
        }

        //Password field is neither empty and valid
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            valid = false;
        }
        if (!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }
    //triggered when user tries to login with email and password
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        if (!validateForm()) {
            return;
        } else {
            // Show a progress spinner
            showProgress(true);
            //kick off a background task to perform the user login attempt.
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
            //}
        }
    }

    // Shows the progress UI and hides the login form.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    // Represents an asynchronous login/registration task used to authenticate
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            //Thread.sleep(1000);
            signIn(mEmail, mPassword);
            return true;
        }

        //sign in with existing account
        private void signIn(String email, String password) {
            if (!validateForm()) {
                return;
            }
            // sign_in_with_email
            mFirebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            // If sign in fails, display a message to the user.
                            if (!task.isSuccessful()) {
                                showProgress(false);
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                            }
                            // If sign in succeeds the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            else {
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                            //showProgress(false);
                        }
                    });
            // END sign_in_with_email
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            //showProgress(false);

            if (success) {
                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
