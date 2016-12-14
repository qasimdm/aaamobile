package app15.aaamobile.view;


import android.content.DialogInterface;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app15.aaamobile.R;
import app15.aaamobile.controller.DatabaseController;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private FragmentManager fm;
    private FirebaseAuth mAuth;
    private DatabaseController dbController;

    private TextView tvUsername, tvEmail;

    private String username;
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
            getUserInfo();
        }

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                //Setting a listener on EditProfile fragment that when dismiss() is called, it shall update the Display name text field in MyAccountFragment
                editProfileFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (dbController != null) {
                            if (dbController.getUser() != null) {
                                username = dbController.getUser().getName();     //After setting username first time, set username also, as it's a tag for dialog fragment
                                tvUsername.setText(username);
                            }
                        }
                    }
                });
                editProfileFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);    //// TODO: 2016-11-15 fix style, title not visible, problem after Api 23
                editProfileFragment.show(fm, username);
            }
        });

        return view;
    }
    //Firebase database controller reference
    private void getUserInfo() {
        dbController = new DatabaseController();
        username = dbController.getUser().getName();
        String userEmail = dbController.getUser().getEmail();
        if ( username != null ) {
            if (!username.equals("")) {
                tvUsername.setText(username);
            }
        }
        if (userEmail != null) {
            tvEmail.setText(userEmail);
        }
    }

}
