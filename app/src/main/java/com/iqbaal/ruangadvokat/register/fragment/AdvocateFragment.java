package com.iqbaal.ruangadvokat.register.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
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
import com.iqbaal.ruangadvokat.api.ApiService;
import com.iqbaal.ruangadvokat.model.Advocate;
import com.iqbaal.ruangadvokat.model.CaptchaResponse;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.ADVOCATE_CARD;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.CERTIFICATE;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.IJAZAH;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.PHOTO;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_ADVOCATE_CARD;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_ID_CARD;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_PKPA;
import static com.iqbaal.ruangadvokat.helper.Global.RequestCode.SELFIE_UPA;
import static com.iqbaal.ruangadvokat.helper.Global.SECRET_KEY;
import static com.iqbaal.ruangadvokat.helper.Global.SITE_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvocateFragment extends Fragment implements View.OnClickListener {
    private String TAG = "Advocate Fragment";
    private ConstraintLayout certificationLayout, advoCardLayout, selfiePKPALayout, selfieUPALayout,
            selfieAdvoCardLayout;
    private String sName, sAddress, sBirthplace, sBirthday, sCertificateNumber, sAdvocateCard, sFee,
            sPhone, sEmail, sPassword;
    private EditText name, address, birthplace, birthday, certificationNumber, advocateCard, fee,
            phone, email, password, passwordConfirm;
    private Spinner gender, status, experience;
    private CheckBox family, business, debt, it, employment, intellectual, property, criminal, civil, agreement;
    private TextView photoFilename, ijazahFilename, certificateFilename, advocateCardFilename, selfieIDCardFilename,
            selfiePKPAFilename, selfieUPAFilename, selfieAdvocateCardFilename, expertise;
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
        status = view.findViewById(R.id.advocate_status);
        certificationNumber = view.findViewById(R.id.advocate_certification);
        advocateCard = view.findViewById(R.id.advocate_card);
        fee = view.findViewById(R.id.advocate_fee);
        phone = view.findViewById(R.id.advocate_phone);
        email = view.findViewById(R.id.advocate_email);
        password = view.findViewById(R.id.advocate_password);
        passwordConfirm = view.findViewById(R.id.advocate_password_confirm);

        gender = view.findViewById(R.id.advocate_gender);
        experience = view.findViewById(R.id.advocate_experience);

        ijazahFilename = view.findViewById(R.id.ijazah_filename);
        photoFilename = view.findViewById(R.id.photo_filename);
        certificateFilename = view.findViewById(R.id.certification_filename);
        advocateCardFilename = view.findViewById(R.id.advocate_card_filename);
        selfieIDCardFilename = view.findViewById(R.id.selfie_id_card_filename);
        selfiePKPAFilename = view.findViewById(R.id.selfie_pkpa_filename);
        selfieUPAFilename = view.findViewById(R.id.selfie_upa_filename);
        selfieAdvocateCardFilename = view.findViewById(R.id.selfie_advocate_card_filename);

        expertise = view.findViewById(R.id.advocate_expertise_title);
        family = view.findViewById(R.id.family_cb);
        business = view.findViewById(R.id.business_cb);
        debt = view.findViewById(R.id.debt_cb);
        it = view.findViewById(R.id.it_cb);
        employment = view.findViewById(R.id.employment_cb);
        property = view.findViewById(R.id.property_cb);
        intellectual = view.findViewById(R.id.intellectual_cb);
        criminal = view.findViewById(R.id.criminal_cb);
        civil = view.findViewById(R.id.civil_cb);
        agreement = view.findViewById(R.id.advocate_agreement);
        TextView termsAndConditions = view.findViewById(R.id.terms_and_conditions);

        Button choosePhoto = view.findViewById(R.id.btn_photo);
        Button chooseIjazah = view.findViewById(R.id.btn_ijazah);
        Button chooseCertificate = view.findViewById(R.id.btn_certification);
        Button chooseAdvocateCard = view.findViewById(R.id.btn_advocate_card);
        Button selfieIDCard = view.findViewById(R.id.btn_selfie_id_card);
        Button selfiePKPA = view.findViewById(R.id.btn_selfie_pkpa);
        Button selfieUPA = view.findViewById(R.id.btn_selfie_upa);
        Button selfieAdvocateCard = view.findViewById(R.id.btn_selfie_advocate_card);
        Button register = view.findViewById(R.id.advocate_register);

        birthday.setOnClickListener(this);
        choosePhoto.setOnClickListener(this);
        chooseIjazah.setOnClickListener(this);
        chooseCertificate.setOnClickListener(this);
        chooseAdvocateCard.setOnClickListener(this);
        selfieIDCard.setOnClickListener(this);
        selfiePKPA.setOnClickListener(this);
        selfieUPA.setOnClickListener(this);
        selfieAdvocateCard.setOnClickListener(this);
        termsAndConditions.setOnClickListener(this);
        register.setOnClickListener(this);

        certificationLayout = view.findViewById(R.id.advocate_certification_layout);
        advoCardLayout = view.findViewById(R.id.advocate_card_layout);
        selfiePKPALayout = view.findViewById(R.id.advocate_selfie_pkpa_layout);
        selfieUPALayout = view.findViewById(R.id.advocate_selfie_upa_layout);
        selfieAdvoCardLayout = view.findViewById(R.id.advocate_selfie_advocate_card_layout);
        status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {
                    fee.setVisibility(View.GONE);
                    certificationLayout.setVisibility(View.GONE);
                    advoCardLayout.setVisibility(View.GONE);
                    selfiePKPALayout.setVisibility(View.GONE);
                    selfieUPALayout.setVisibility(View.GONE);
                    selfieAdvoCardLayout.setVisibility(View.GONE);
                } else if (position == 2) {
                    fee.setVisibility(View.VISIBLE);
                    certificationLayout.setVisibility(View.VISIBLE);
                    advoCardLayout.setVisibility(View.VISIBLE);
                    selfiePKPALayout.setVisibility(View.VISIBLE);
                    selfieUPALayout.setVisibility(View.VISIBLE);
                    selfieAdvoCardLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PHOTO) {
                uploadFile(getString(R.string.photo), data.getData());
                photoFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == IJAZAH) {
                uploadFile(getString(R.string.ijazah), data.getData());
                ijazahFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == CERTIFICATE) {
                uploadFile(getString(R.string.certificate), data.getData());
                certificateFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == ADVOCATE_CARD) {
                uploadFile(getString(R.string.advocate_card), data.getData());
                advocateCardFilename.setText(data.getData().getLastPathSegment());
            } else if (requestCode == SELFIE_ID_CARD) {
                byte[] byteData = bitmapConvert((Bitmap) data.getExtras().get("data"));
                uploadFile(getString(R.string.selfie_with_id_card), byteData);
                selfieIDCardFilename.setText(data.getExtras().get("data").toString().substring(24));
            } else if (requestCode == SELFIE_PKPA) {
                byte[] byteData = bitmapConvert((Bitmap) data.getExtras().get("data"));
                uploadFile(getString(R.string.selfie_with_pkpa), byteData);
                selfiePKPAFilename.setText(data.getExtras().get("data").toString().substring(24));
            } else if (requestCode == SELFIE_UPA) {
                byte[] byteData = bitmapConvert((Bitmap) data.getExtras().get("data"));
                uploadFile(getString(R.string.selfie_with_upa), byteData);
                selfieUPAFilename.setText(data.getExtras().get("data").toString().substring(24));
            } else if (requestCode == SELFIE_ADVOCATE_CARD) {
                byte[] byteData = bitmapConvert((Bitmap) data.getExtras().get("data"));
                uploadFile(getString(R.string.selfie_with_advocate_card), byteData);
                selfieAdvocateCardFilename.setText(data.getExtras().get("data").toString().substring(24));
            }
        } else if (resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "null data", Toast.LENGTH_SHORT).show();
    }

    private byte[] bitmapConvert(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    private void uploadFile(final String name, Uri uri) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(String.format("%s %s...", getString(R.string.uploading), name));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference ref = mStorage.child("images").child(email.getText().toString()).child(name);
        ref.putFile(uri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                Log.d(TAG, name + " uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                Log.d(TAG, name + " failed to upload");
            }
        });
    }

    private void uploadFile(final String name, byte[] bytes) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle(String.format("%s %s...", getString(R.string.uploading), name));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference ref = mStorage.child("images").child(email.getText().toString()).child(name);
        ref.putBytes(bytes)
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setProgress((int) progress);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
                Log.d(TAG, name + " uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.upload_failed), Toast.LENGTH_SHORT).show();
                Log.d(TAG, name + " failed to upload");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.advocate_birthday:
                showDatePicker();
                break;
            case R.id.terms_and_conditions:
                showTermsAndConditions();
                break;
            case R.id.btn_photo:
                openGallery(PHOTO);
                break;
            case R.id.btn_ijazah:
                openGallery(IJAZAH);
                break;
            case R.id.btn_certification:
                openGallery(CERTIFICATE);
                break;
            case R.id.btn_advocate_card:
                openGallery(ADVOCATE_CARD);
                break;
            case R.id.btn_selfie_id_card:
                openCamera(SELFIE_ID_CARD);
                break;
            case R.id.btn_selfie_pkpa:
                openCamera(SELFIE_PKPA);
                break;
            case R.id.btn_selfie_upa:
                openCamera(SELFIE_UPA);
                break;
            case R.id.btn_selfie_advocate_card:
                openCamera(SELFIE_ADVOCATE_CARD);
                break;
            case R.id.advocate_register:
                if (!validate()) return;
                else reCaptcha();
                break;
        }
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

    private void showTermsAndConditions() {
        AlertDialog.Builder popup = new AlertDialog.Builder(getContext());
        popup.setTitle(getString(R.string.terms_and_conditions)).setMessage(".....").setCancelable(true);
        popup.show();
    }

    private void openGallery(int requestCode) {
        sEmail = email.getText().toString();
        if (sEmail.isEmpty()) {
            email.setError(getString(R.string.must_be_filled));
            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_file)), requestCode);
    }

    private void openCamera(int requestCode) {
        sEmail = email.getText().toString();
        if (sEmail.isEmpty()) {
            email.setError(getString(R.string.must_be_filled));
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }

    private void signup() {
        String sGender = gender.getSelectedItem().toString();
        String sStatus = status.getSelectedItem().toString();
        String sExperience = experience.getSelectedItem().toString();
        String sExpertise = "";
        if (family.isChecked()) sExpertise += ", " + getString(R.string.family);
        if (business.isChecked()) sExpertise += ", " + getString(R.string.business);
        if (debt.isChecked()) sExpertise += ", " + getString(R.string.debt);
        if (it.isChecked()) sExpertise += ", " + getString(R.string.it);
        if (employment.isChecked()) sExpertise += ", " + getString(R.string.employment);
        if (property.isChecked()) sExpertise += ", " + getString(R.string.property);
        if (intellectual.isChecked()) sExpertise += ", " + getString(R.string.intellectual);
        if (criminal.isChecked()) sExpertise += ", " + getString(R.string.criminal);
        if (civil.isChecked()) sExpertise += ", " + getString(R.string.civil);
        final Advocate advocate = new Advocate(sName, sAddress, sGender, sBirthplace, sBirthday,
                sStatus, sCertificateNumber, sAdvocateCard, sExperience, sExpertise.substring(2),
                sFee, sPhone, sEmail);

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
                            if (mAuth.getCurrentUser() != null)
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
        sFee = fee.getText().toString();
        sPhone = phone.getText().toString();
        sEmail = email.getText().toString();
        sPassword = password.getText().toString();
        String sPasswordConfirm = passwordConfirm.getText().toString();
        String sPhotoFilename = photoFilename.getText().toString();
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

        if (certificationLayout.getVisibility() == View.VISIBLE && sCertificateNumber.isEmpty()) {
            certificationNumber.setError(getString(R.string.must_be_filled));
            validated = false;
        } else certificationNumber.setError(null);

        if (certificationLayout.getVisibility() == View.VISIBLE && sCertificateFilename.isEmpty()) {
            certificateFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            certificateFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (advoCardLayout.getVisibility() == View.VISIBLE && sAdvocateCard.isEmpty()) {
            advocateCard.setError(getString(R.string.must_be_filled));
            validated = false;
        } else advocateCard.setError(null);

        if (advoCardLayout.getVisibility() == View.VISIBLE && sAdvocateCardFilename.isEmpty()) {
            advocateCardFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            advocateCardFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (experience.getSelectedItemPosition() == 0) {
            experience.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else experience.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (!family.isChecked() && !business.isChecked() && !debt.isChecked() &&
                !it.isChecked() && !employment.isChecked() && !intellectual.isChecked() &&
                !property.isChecked() && !criminal.isChecked() && !civil.isChecked()) {
            expertise.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else expertise.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (fee.getVisibility() == View.VISIBLE) {
            if (sFee.isEmpty()) {
                fee.setError(getString(R.string.must_be_filled));
                validated = false;
            }else if (Integer.parseInt(sFee) > 1000000) {
                fee.setError(getString(R.string.max_fee));
                validated = false;
            }
        } else fee.setError(null);

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

        if (sPhotoFilename.isEmpty()) {
            photoFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            photoFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (sSelfieIDCardFilename.isEmpty()) {
            selfieIDCardFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfieIDCardFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (selfiePKPALayout.getVisibility() == View.VISIBLE && sSelfiePKPAFilename.isEmpty()) {
            selfiePKPAFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfiePKPAFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (selfieUPALayout.getVisibility() == View.VISIBLE && sSelfieUPAFilename.isEmpty()) {
            selfieUPAFilename.setBackgroundColor(getResources().getColor(R.color.transparentRed));
            validated = false;
        } else
            selfieUPAFilename.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        if (selfieAdvoCardLayout.getVisibility() == View.VISIBLE && sSelfieAdvocateCardFilename.isEmpty()) {
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

    private void reCaptcha() {
        SafetyNet.getClient(getActivity()).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        String userResponseToken = response.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            // Validate the user response token using the
                            // reCAPTCHA siteverify API.
                            Call<CaptchaResponse> call = ApiService.getService()
                                    .captcha(SECRET_KEY, userResponseToken);
                            call.enqueue(new Callback<CaptchaResponse>() {
                                @Override
                                public void onResponse(Call<CaptchaResponse> call, Response<CaptchaResponse> response) {
                                    boolean isSuccess = response.body().getSuccess();
                                    if (isSuccess) signup();
                                    else
                                        Toast.makeText(getContext(), String.valueOf(isSuccess), Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, String.valueOf(response.body().getSuccess()));
                                }

                                @Override
                                public void onFailure(Call<CaptchaResponse> call, Throwable t) {
                                    t.printStackTrace();
                                    Log.d(TAG, t.getMessage());
                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    Log.d(TAG, "Error: " + CommonStatusCodes
                            .getStatusCodeString(statusCode));
                } else
                    Log.d(TAG, "Error: " + e.getMessage());
            }
        });
    }
}
