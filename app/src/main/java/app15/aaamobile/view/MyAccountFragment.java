package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app15.aaamobile.R;
import app15.aaamobile.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private final String TAG = "MyAccountFragment";

    FragmentManager fm;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    User user;

    TextView tvUsername, tvEmail;

    private String uid;
    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        ImageButton btnChangePassword = (ImageButton)view.findViewById(R.id.user_profile_change_password);
        tvUsername = (TextView)view.findViewById(R.id.user_profile_name);
        tvEmail = (TextView)view.findViewById(R.id.user_profile_email);

        fm = getActivity().getSupportFragmentManager();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mUser = mAuth.getCurrentUser();
            uid = mUser.getUid();
            Log.i("MyAccount", "in if(mAuth != null) ");
        }
        setupDatabaseAndEventListener();

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                editProfileFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);    //// TODO: 2016-11-15 fix style, title not visible, problem after Api 23
                editProfileFragment.show(fm, user.getName());
            }
        });

        return view;
    }
    //Firebase database setup and setting an EventListener on it
    private void setupDatabaseAndEventListener() {
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("users").child(uid);  // TODO: 2016-11-29 check for exceptions
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //This method is called once with initial value
                // and when data at this location is updated
                user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    //User is null, error out
                    Log.e(TAG, "User object is null");
                } else {
                    if ( user.getName() != null || !user.getName().equals("")) {
                        tvUsername.setText(user.getName());
                        tvEmail.setText(user.getEmail());
                    }
                    Log.i(TAG, "Username is: " + user.getName());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Failed to read the value
                Log.i(TAG, "Failed to read value. ", databaseError.toException());
            }
        });
    }

}
