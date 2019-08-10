package com.iqbaal.ruangadvokat.register.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iqbaal.ruangadvokat.R;
import com.iqbaal.ruangadvokat.model.Advocate;

import java.io.File;
import java.util.UUID;

import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.ADVOCATE_CARD;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.CERTIFICATE;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.IJAZAH;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_ADVOCATE_CARD;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_ID_CARD;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_PKPA;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_UPA;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvocateFragment extends Fragment implements View.OnClickListener {
    private String TAG = "Advocate Fragment";
    private String sName, sAddress, sBirthplace, sBirthday, sCertificateNumber, sAdvocateCard,
            sPhone, sEmail, sPassword;
    private ArrayMap<String, Uri> fileToUpload = new ArrayMap<>();
    private EditText name, address, birthplace, birthday, certificationNumber, advocateCard,
            phone, email, password, passwordConfirm;
    private Spinner gender, status, experience, expertise;
    private CheckBox agreement;
    private TextView ijazahFilename, certificateFilename, advocateCardFilename, selfieIDCardFilename,
            selfiePKPAFilename, selfieUPAFilename, selfieAdvocateCardFilename;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    public AdvocateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_advocate, container, false);
        name = view.findViewById(R.id.advocate_name);
        address = view.findViewById(R.id.advocate_address);
        birthplace = view.findViewById(R.id.advocate_birthplace);
        birthday = view.findViewById(R.id.advocate_birthday);
        status = view.findViewById(R.id.status);
        certificationNumber = view.findViewById(R.id.advocate_certification);
        advocateCard = view.findViewById(R.id.advocate_card);
        phone = view.findViewById(R.id.advocate_phone);
        email = view.findViewById(R.id.advocate_email);
        password = view.findViewById(R.id.advocate_password);
        passwordConfirm = view.findViewById(R.id.advocate_password_confirm);

        gender = view.findViewById(R.id.advocate_gender);
        experience = view.findViewById(R.id.advocate_experience);
        expertise = view.findViewById(R.id.advocate_expertise);

        ijazahFilename = view.findViewById(R.id.ijazah_filename);
        certificateFilename = view.findViewById(R.id.certification_filename);
        advocateCardFilename = view.findViewById(R.id.card_filename);
        selfieIDCardFilename = view.findViewById(R.id.selfie_id_card_filename);
        selfiePKPAFilename = view.findViewById(R.id.selfie_pkpa_filename);
        selfieUPAFilename = view.findViewById(R.id.selfie_upa_filename);
        selfieAdvocateCardFilename = view.findViewById(R.id.selfie_advocate_card_filename);
        agreement = view.findViewById(R.id.advocate_agreement);
        TextView termsAndConditions = view.findViewById(R.id.terms_and_conditions);

        Button chooseIjazah = view.findViewById(R.id.btn_ijazah);
        Button chooseCertificate = view.findViewById(R.id.btn_certification);
        Button chooseAdvocateCard = view.findViewById(R.id.btn_card);
        Button selfieIDCard = view.findViewById(R.id.btn_selfie_id_card);
        Button selfiePKPA = view.findViewById(R.id.btn_selfie_pkpa);
        Button selfieUPA = view.findViewById(R.id.btn_selfie_upa);
        Button selfieAdvocateCard = view.findViewById(R.id.btn_selfie_advocate_card);
        Button register = view.findViewById(R.id.advocate_register);

        chooseIjazah.setOnClickListener(this);
        chooseCertificate.setOnClickListener(this);
        chooseAdvocateCard.setOnClickListener(this);
        selfieIDCard.setOnClickListener(this);
        selfiePKPA.setOnClickListener(this);
        selfieUPA.setOnClickListener(this);
        selfieAdvocateCard.setOnClickListener(this);
        register.setOnClickListener(this);

        selfieIDCard.setOnClickListener(new View.OnClickListener() {
            File file;
            Uri fileUri;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = new File(getActivity().getExternalCacheDir(),
                        String.valueOf(System.currentTimeMillis()) + ".jpg");
                fileUri = Uri.fromFile(file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, 4);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        return view;
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file)), requestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == IJAZAH) {
                fileToUpload.put("ijazah", data.getData());
                ijazahFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == CERTIFICATE) {
                fileToUpload.put("certificate", data.getData());
                certificateFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == ADVOCATE_CARD) {
                fileToUpload.put("advocate card", data.getData());
                advocateCardFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == SELFIE_ID_CARD) {
                fileToUpload.put("selfie id card", data.getData());
                selfieIDCardFilename.setText(data.getData().getLastPathSegment());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadFile(Uri filePath) {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = mStorage.child("images/" + UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ijazah:
                openGallery(IJAZAH);
                break;
            case R.id.btn_certification:
                openGallery(CERTIFICATE);
                break;
            case R.id.btn_card:
                openGallery(ADVOCATE_CARD);
                break;
            case R.id.btn_selfie_id_card:
                openCamera(SELFIE_ID_CARD);break;
            case R.id.btn_selfie_pkpa:
                openCamera(SELFIE_PKPA);break;
            case R.id.btn_selfie_upa:
                openCamera(SELFIE_UPA);break;
            case R.id.btn_selfie_advocate_card:
                openCamera(SELFIE_ADVOCATE_CARD);break;
            case R.id.advocate_register:
                signup();
                break;
        }
    }

    private void openCamera(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivityForResult(intent, requestCode);
    }

    private void signup() {
        if (!validate()) return;


        String sGender = gender.getSelectedItem().toString();
        String sStatus = status.getSelectedItem().toString();
        String sExperience = experience.getSelectedItem().toString();
        String sExpertise = expertise.getSelectedItem().toString();
        Advocate advocate = new Advocate(sName, sAddress, sGender, sBirthplace, sBirthday,
                sStatus, sCertificateNumber, sAdvocateCard, sExperience, sExpertise, sPhone, sEmail);

        createAccount(advocate);
    }

    private void createAccount(final Advocate advocate) {
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
                            mDatabase.child("advocate").push().setValue(advocate);
                            sendEmailVerification(mAuth.getCurrentUser());
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getString(R.string.register_failed),
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar snackbar = Snackbar.make(getView(),
                            getString(R.string.check_email), Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(getString(R.string.okay), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getActivity().finish();
                        }
                    });
                    snackbar.show();
                }
            }
        });
    }

    private boolean validate() {
        boolean validated = true;

        sName = name.getText().toString();
        sAddress = address.getText().toString();
        sBirthplace = birthplace.getText().toString();
        sBirthday = birthday.getText().toString();
        String sIjazahFilename = ijazahFilename.getText().toString();
        sCertificateNumber = certificationNumber.getText().toString();
        String sCertificateFilename = certificateFilename.getText().toString();
        sAdvocateCard = advocateCard.getText().toString();
        String sAdvocateCardFilename = advocateCardFilename.getText().toString();
        sPhone = phone.getText().toString();
        sEmail = email.getText().toString();
        sPassword = password.getText().toString();
        String sPasswordConfirm = passwordConfirm.getText().toString();
        String sSelfieIDCardFilename = selfieIDCardFilename.getText().toString();
        String sSelfiePKPAFilename = selfiePKPAFilename.getText().toString();
        String sSelfieUPAFilename = selfieUPAFilename.getText().toString();
        String sSelfieAdvocateCardFilename = selfieAdvocateCardFilename.getText().toString();

        if (sName.isEmpty()) {
            name.setError(getString(R.string.must_be_filled));
            validated = false;
        } else name.setError(null);

        if (sAddress.isEmpty()) {
            address.setError(getString(R.string.must_be_filled));
            validated = false;
        } else address.setError(null);

        if (gender.getSelectedItemPosition() == 0) {
            gender.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else gender.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sBirthplace.isEmpty()) {
            birthplace.setError(getString(R.string.must_be_filled));
            validated = false;
        } else birthplace.setError(null);

        if (sBirthday.isEmpty()) {
            birthday.setError(getString(R.string.must_be_filled));
            validated = false;
        } else birthday.setError(null);

        if (status.getSelectedItemPosition() == 0) {
            status.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else status.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sIjazahFilename.isEmpty()) {
            ijazahFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            ijazahFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sCertificateNumber.isEmpty()) {
            certificationNumber.setError(getString(R.string.must_be_filled));
            validated = false;
        } else certificationNumber.setError(null);

        if (sCertificateFilename.isEmpty()) {
            certificateFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            certificateFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sAdvocateCard.isEmpty()) {
            advocateCard.setError(getString(R.string.must_be_filled));
            validated = false;
        } else advocateCard.setError(null);

        if (sAdvocateCardFilename.isEmpty()) {
            advocateCardFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            advocateCardFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (experience.getSelectedItemPosition() == 0) {
            experience.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else experience.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (expertise.getSelectedItemPosition() == 0) {
            expertise.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else expertise.setBackgroundColor(getResources().getColor(android.R.color.transparent));

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

        if (sSelfieIDCardFilename.isEmpty()) {
            selfieIDCardFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfieIDCardFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sSelfiePKPAFilename.isEmpty()) {
            selfiePKPAFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfiePKPAFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sSelfieUPAFilename.isEmpty()) {
            selfieUPAFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfieUPAFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sSelfieAdvocateCardFilename.isEmpty()) {
            selfieAdvocateCardFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfieAdvocateCardFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (!agreement.isChecked()) {
            agreement.setError(getString(R.string.must_be_filled));
            validated = false;
        } else agreement.setError(null);

        return validated;
    }
}
