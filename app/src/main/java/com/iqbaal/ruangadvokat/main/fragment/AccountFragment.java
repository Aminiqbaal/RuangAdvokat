package com.iqbaal.ruangadvokat.main.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iqbaal.ruangadvokat.R;
import com.iqbaal.ruangadvokat.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    private ConstraintLayout loginLayout, profileLayout;
    private EditText email, password;
    private TextView user_email;
    private FirebaseAuth mAuth;
    private String TAG = "Account Fragment";

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        loginLayout = view.findViewById(R.id.login_layout);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        Button login = view.findViewById(R.id.login);
        TextView toRegister = view.findViewById(R.id.to_register);
        login.setOnClickListener(this);
        toRegister.setOnClickListener(this);

        profileLayout = view.findViewById(R.id.profile_layout);
        user_email = view.findViewById(R.id.user_email);
        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.isEmailVerified()) updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null && user.isEmailVerified()) {
            email.setText(null);
            password.setText(null);
            user_email.setText(user.getEmail());
            loginLayout.setVisibility(View.GONE);
            profileLayout.setVisibility(View.VISIBLE);
        }else if(user != null && !user.isEmailVerified()){
            Toast.makeText(getContext(), getString(R.string.email_not_verified), Toast.LENGTH_SHORT).show();
        } else{
            loginLayout.setVisibility(View.VISIBLE);
            profileLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                signin();
                break;
            case R.id.to_register:
                startActivity(new Intent(getContext(), RegisterActivity.class));
                break;
            case R.id.logout:
                signout();
                break;
        }
    }

    private void signout() {
        mAuth.signOut();
        updateUI(null);
    }

    private void signin() {
        if (!validate()) return;

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.authenticating));
        progressDialog.show();

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getString(R.string.login_failed),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean validate() {
        boolean validated = true;

        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        if (sEmail.isEmpty()) {
            email.setError(getString(R.string.must_be_filled));
            validated = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError(getString(R.string.invalid_email));
            validated = false;
        } else email.setError(null);

        if (sPassword.isEmpty()) {
            password.setError(getString(R.string.must_be_filled));
            validated = false;
        } else if (sPassword.length() < 8) {
            password.setError(getString(R.string.min_char));
            validated = false;
        } else password.setError(null);

        return validated;
    }
}
