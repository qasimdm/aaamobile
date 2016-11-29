package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import app15.aaamobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mUser = mAuth.getCurrentUser();
            Log.i("MyAccount", "in if(mAuth != null) ");
        }
        ImageButton btnChangePassword = (ImageButton)view.findViewById(R.id.user_profile_change_password);
        TextView tvUsername = (TextView)view.findViewById(R.id.user_profile_name);
        TextView tvEmail = (TextView)view.findViewById(R.id.user_profile_email);
        if ( mUser.getDisplayName() != null ) {
            tvUsername.setText(mUser.getDisplayName());
        }
        tvEmail.setText(mUser.getEmail());

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Implement change password diaglog", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

}
