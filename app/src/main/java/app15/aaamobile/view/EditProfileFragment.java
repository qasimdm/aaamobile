package app15.aaamobile.view;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app15.aaamobile.R;
import app15.aaamobile.controller.DatabaseController;
import app15.aaamobile.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends DialogFragment {

    private DialogInterface.OnDismissListener onDismissListener;
    private DatabaseController databaseController;
    Context context;
    //Firebase
    FirebaseUser mCurrentUser;


    private AutoCompleteTextView tvUsername;
    private EditText etCurrentPassword, etNewPassword;
    private boolean ifUsernameNull = true;
    private String username, newPassword;

    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        context = getContext();
        databaseController = new DatabaseController();  //get a reference to the database
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
                    String key = mCurrentUser.getUid();
                    databaseController.setDatabaseReference("users", key);

                    if (!username.equals("")) {
                        databaseController.writeSingleItem("name", username);
                        databaseController.user.setName(username);
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username).build();
                        mCurrentUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //Toast.makeText(context, "Display name changed successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                    if (!TextUtils.isEmpty(newPassword) && !TextUtils.isEmpty(etCurrentPassword.getText().toString())) {    //if user don't want to update password but just Display name
                        changePassword(etCurrentPassword.getText().toString());
                    }
                    dismiss();
                }
            }
        });
        return view;
    }

    private boolean validateFields() {
        boolean valid = true;
        View focusView = null;

        String displayName = tvUsername.getText().toString();
        String currentPassword = etCurrentPassword.getText().toString();
        newPassword = etNewPassword.getText().toString();

        // Reset errors.
        tvUsername.setError(null);
        etCurrentPassword.setError(null);
        etNewPassword.setError(null);

        if (ifUsernameNull) { //if user haven't set a display name already
            if (TextUtils.isEmpty(displayName)) {
                tvUsername.setError(getString(R.string.error_field_required));
                focusView = tvUsername;
                valid = false;
            }
            else{   //if the User name field is not empty
                username = tvUsername.getText().toString();
                if (TextUtils.isEmpty(currentPassword) && TextUtils.isEmpty(newPassword)){
                    return valid;
                }
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

    //authenticates the current password first and then changes the password.
    private void changePassword(String currentPassword){
        AuthCredential credential = EmailAuthProvider.getCredential(mCurrentUser.getEmail(), currentPassword);
        mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mCurrentUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.i("TAG", "Password changed successfully");
                                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show();
                                databaseController.writeSingleItem("password", etNewPassword.getText().toString());
                            }
                            else{
                                Log.i("TAG", "Failed to changed the password");
                                Toast.makeText(context, "Failed to changed the password", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
                else{
                    Log.i("TAG", "Authentication failed, Please try again");
                    Toast.makeText(context, "Authentication failed, Please try again", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    //Listens for onDismiss, to update the Display name in MyAccountFragment,
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener){
        this.onDismissListener = onDismissListener;
    }
    @Override
    public void onDismiss(DialogInterface dialogInterface){
        super.onDismiss(dialogInterface);
        if (onDismissListener != null){
            onDismissListener.onDismiss(dialogInterface);
        }
    }

}
