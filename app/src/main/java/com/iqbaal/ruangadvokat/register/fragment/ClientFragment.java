package com.iqbaal.ruangadvokat.register.fragment;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.iqbaal.ruangadvokat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class ClientFragment extends Fragment implements View.OnClickListener {
    private View view;
    private EditText name, birthday, company, phone, email, password, passwordConfirm;
    private TextView termsAndConditions;
    private Spinner gender;
    private CheckBox agreement;
    private Button register;
    private FirebaseAuth mAuth;
    private String TAG = "Client Fragment";

    public ClientFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_client, container, false);
        name = view.findViewById(R.id.client_name);
        gender = view.findViewById(R.id.client_gender);
        birthday = view.findViewById(R.id.client_birthday);
        company = view.findViewById(R.id.client_company);
        phone = view.findViewById(R.id.client_phone);
        email = view.findViewById(R.id.client_email);
        password = view.findViewById(R.id.client_password);
        passwordConfirm = view.findViewById(R.id.client_password_confirm);
        agreement = view.findViewById(R.id.client_agreement);
        termsAndConditions = view.findViewById(R.id.terms_and_conditions);
        register = view.findViewById(R.id.client_register);

        birthday.setOnClickListener(this);
        termsAndConditions.setOnClickListener(this);
        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

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

        String sName = name.getText().toString();
        String sGender = gender.getSelectedItem().toString();
        String sBirthday = birthday.getText().toString();
        String sCompany = company.getText().toString();
        String sPhone = phone.getText().toString();
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Mendaftarkan akun Anda...");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getContext(), "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(), "Silahkan cek email Anda untuk verifikasi", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Pendaftaran gagal",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private boolean validate() {
        boolean validated = true;

        String sName = name.getText().toString();
        String sPhone = phone.getText().toString();
        String sEmail = email.getText().toString();
        String sPassword = password.getText().toString();
        String sPasswordConfirm = passwordConfirm.getText().toString();

        if (sName.isEmpty()) {
            name.setError("harus diisi");
            validated = false;
        } else name.setError(null);

        if (sPhone.isEmpty()) {
            phone.setError("harus diisi");
            validated = false;
        } else if (!Patterns.PHONE.matcher(sPhone).matches()) {
            phone.setError("masukkan nomor telepon yang valid");
            validated = false;
        } else phone.setError(null);

        if (sEmail.isEmpty()) {
            email.setError("harus diisi");
            validated = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("masukkan nomor telepon yang valid");
            validated = false;
        } else email.setError(null);

        if (sPassword.isEmpty()) {
            password.setError("harus diisi");
            validated = false;
        } else if (sPassword.length() < 8) {
            password.setError("minimal 8 karakter");
            validated = false;
        } else password.setError(null);

        if (sPasswordConfirm.isEmpty()) {
            passwordConfirm.setError("harus diisi");
            validated = false;
        } else if (sPasswordConfirm.length() < 8) {
            passwordConfirm.setError("minimal 8 karakter");
            validated = false;
        } else if (!sPasswordConfirm.equals(sPassword)) {
            passwordConfirm.setError("kata sandi tidak cocok");
            validated = false;
        } else passwordConfirm.setError(null);

        if(!agreement.isChecked()){
            agreement.setError("harus diisi");
            validated = false;
        }else agreement.setError(null);

        return validated;
    }

    private void showTermsAndConditions() {
        AlertDialog.Builder popup = new AlertDialog.Builder(getContext());
    }

    private void showDatePicker() {
        String date = birthday.getText().toString();
        Calendar calendar = Calendar.getInstance();
        int mYear = date.isEmpty() ? calendar.get(Calendar.YEAR) : Integer.parseInt(date.substring(6));
        int mMonth = date.isEmpty() ? calendar.get(Calendar.MONTH) : Integer.parseInt(date.substring(3, 5))-1;
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
