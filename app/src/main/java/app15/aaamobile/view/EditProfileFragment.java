package app15.aaamobile.view;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app15.aaamobile.R;
import app15.aaamobile.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends DialogFragment {

    DatabaseReference databaseReference;
    AutoCompleteTextView tvUsername;
    EditText etCurrentPassword, etNewPassword;
    boolean ifUsernameNull = true;
    String username, newPassword;
    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        getDialog().setTitle("Edit Profile");
        username = getTag();
        tvUsername = (AutoCompleteTextView)view.findViewById(R.id.edit_profile_username);
        etCurrentPassword = (EditText)view.findViewById(R.id.edit_profile_current_password);
        etNewPassword = (EditText)view.findViewById(R.id.edit_profile_new_password);
        if (username != null && !username.equals("")){
            tvUsername.setVisibility(View.GONE);
            ifUsernameNull = false;
        }
        Button btnCancel = (Button)view.findViewById(R.id.btn_edit_profile_cancel);
        Button btnSaveChanges = (Button)view.findViewById(R.id.btn_edit_profile_save_changes);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dismiss();
            }
        });
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()){
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
                    FirebaseUser mUser = auth.getCurrentUser();
                    mUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                if (!username.equals("")) {
                                    databaseReference.child("name").setValue(username);
                                }
                                databaseReference.child("password").setValue(etNewPassword.getText().toString());
                            }
                            else{

                            }
                        }
                    });

                    dismiss();
                }
            }
        });
        return view;
    }

    private boolean validateFields() {
        boolean valid = true;
        View focusView = null;

        String currentPassword = etCurrentPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();

        // Reset errors.
        tvUsername.setError(null);
        etCurrentPassword.setError(null);
        etNewPassword.setError(null);

        if (ifUsernameNull) { //if user haven't set a display name already
            if (TextUtils.isEmpty(tvUsername.getText().toString())) {
                tvUsername.setError(getString(R.string.error_field_required));
                focusView = tvUsername;
                valid = false;
            }
            else{   //if the User name field is not empty
                username = tvUsername.getText().toString();
            }

        }
        //Password field is neither empty and valid
        if (TextUtils.isEmpty(currentPassword) || !isPasswordValid(currentPassword)) {
            etCurrentPassword.setError(getString(R.string.error_field_required));
            focusView = etCurrentPassword;
            valid = false;
        }
        if (TextUtils.isEmpty(newPassword) || !isPasswordValid(newPassword)) {
            etNewPassword.setError(getString(R.string.error_field_required));
            focusView = etNewPassword;
            valid = false;
        }
        if (!(TextUtils.isEmpty(currentPassword) && TextUtils.isEmpty(newPassword)) ){
            if(currentPassword.equals(newPassword)){
                Toast.makeText(getContext(), "New Password can't be same as current password!", Toast.LENGTH_SHORT).show();
                focusView = etCurrentPassword;
                valid = false;
            }
        }
        if (!valid) {
            focusView.requestFocus();
        }
        return valid;
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }


}
