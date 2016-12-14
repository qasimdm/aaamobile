package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import app15.aaamobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgotPasswordFragment extends DialogFragment {
    private FirebaseAuth mAuth;
    //UI ref
    private EditText etEmail;
    private Button btnSendResetEmail;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        getDialog().setTitle("Reset Password");
        mAuth = FirebaseAuth.getInstance();

        etEmail = (EditText)view.findViewById(R.id.email_in_forgot_password);
        btnSendResetEmail = (Button)view.findViewById(R.id.btn_send_password_reset_email);

        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                if (!TextUtils.isEmpty(email)){
                    Toast.makeText(getContext(), "Password Email sent If provided email exist in our system", Toast.LENGTH_LONG).show();
                    mAuth.sendPasswordResetEmail(email);
                    dismiss();
                }
            }
        });

        return view;
    }

}
