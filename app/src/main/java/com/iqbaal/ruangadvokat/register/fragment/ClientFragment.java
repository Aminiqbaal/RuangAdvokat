package com.iqbaal.ruangadvokat.register.fragment;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iqbaal.ruangadvokat.R;
import com.iqbaal.ruangadvokat.model.Client;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClientFragment extends Fragment implements View.OnClickListener {
    private EditText name, birthday, company, phone, email, password, passwordConfirm;
    private String sName, sBirthday, sPhone, sEmail, sPassword;
    private Spinner gender;
    private CheckBox agreement;
    private String TAG = "Client Fragment";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public ClientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_client, container, false);
        name = view.findViewById(R.id.client_name);
        gender = view.findViewById(R.id.client_gender);
        birthday = view.findViewById(R.id.client_birthday);
        company = view.findViewById(R.id.client_company);
        phone = view.findViewById(R.id.client_phone);
        email = view.findViewById(R.id.client_email);
        password = view.findViewById(R.id.client_password);
        passwordConfirm = view.findViewById(R.id.client_password_confirm);
        agreement = view.findViewById(R.id.client_agreement);

        TextView termsAndConditions = view.findViewById(R.id.terms_and_conditions);
        Button register = view.findViewById(R.id.client_register);

        birthday.setOnClickListener(this);
        termsAndConditions.setOnClickListener(this);
        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.client_birthday:
                showDatePicker();
                break;
            case R.id.terms_and_conditions:
                showTermsAndConditions();
                break;
            case R.id.client_register:
                signup();
                break;
        }
    }

    private void signup() {
        if (!validate()) return;

        String sGender = gender.getSelectedItem().toString();
        String sCompany = company.getText().toString();
        final Client client = new Client(sName, sGender, sBirthday, sCompany, sPhone, sEmail);

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.signing_up));
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            mDatabase.child("client").push().setValue(client);
                            sendVerificationEmail(mAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getString(R.string.register_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(getView(), getString(R.string.check_email), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(getString(R.string.okay), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    snackbar.show();
                }else
                    Toast.makeText(getContext(), getString(R.string.verif_email_failed_to_send), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validate() {
        boolean validated = true;

        sName = name.getText().toString();
        sBirthday = birthday.getText().toString();
        sPhone = phone.getText().toString();
        sEmail = email.getText().toString();
        sPassword = password.getText().toString();
        String sPasswordConfirm = passwordConfirm.getText().toString();

        if (sName.isEmpty()) {
            name.setError(getString(R.string.must_be_filled));
            validated = false;
        } else name.setError(null);

        if (gender.getSelectedItemPosition() == 0) {
            gender.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else gender.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sBirthday.isEmpty()) {
            birthday.setError(getString(R.string.must_be_filled));
            validated = false;
        } else birthday.setError(null);

        if (sPhone.isEmpty()) {
            phone.setError(getString(R.string.must_be_filled));
            validated = false;
        } else if (!Patterns.PHONE.matcher(sPhone).matches()) {
            phone.setError(getString(R.string.invalid_phone));
            validated = false;
        } else phone.setError(null);

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

        if (sPasswordConfirm.isEmpty()) {
            passwordConfirm.setError(getString(R.string.must_be_filled));
            validated = false;
        } else if (sPasswordConfirm.length() < 8) {
            passwordConfirm.setError(getString(R.string.min_char));
            validated = false;
        } else if (!sPasswordConfirm.equals(sPassword)) {
            passwordConfirm.setError(getString(R.string.unmatch_password));
            validated = false;
        } else passwordConfirm.setError(null);

        if (!agreement.isChecked()) {
            agreement.setError(getString(R.string.must_be_filled));
            validated = false;
        } else agreement.setError(null);

        return validated;
    }

    private void showTermsAndConditions() {
        AlertDialog.Builder popup = new AlertDialog.Builder(getContext());
        popup.setTitle(getString(R.string.terms_and_conditions)).setMessage(".....").setCancelable(true);
        popup.show();
    }

    private void showDatePicker() {
        String date = birthday.getText().toString();
        Calendar calendar = Calendar.getInstance();
        int mYear = date.isEmpty() ? calendar.get(Calendar.YEAR) : Integer.parseInt(date.substring(6));
        int mMonth = date.isEmpty() ? calendar.get(Calendar.MONTH) : Integer.parseInt(date.substring(3, 5)) - 1;
        int mDay = date.isEmpty() ? calendar.get(Calendar.DAY_OF_MONTH) : Integer.parseInt(date.substring(0, 2));
        DatePickerDialog dialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);
                birthday.setText(dateFormat.format(newDate.getTime()));
            }
        }, mYear, mMonth, mDay);
        dialog.show();
    }
}
