package com.iqbaal.ruangadvokat.main.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iqbaal.ruangadvokat.LoginActivity;
import com.iqbaal.ruangadvokat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {
    private ConstraintLayout client, advocate;
    private TextView userName, userGender, userBirthday, userPhone, userEmail, clientCompany,
            advocateBirthplace, advocateAddress, advocateStatus, advocateExperience, advocateExpertise,
            advocateCertificateNumber, advocateCard;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private String TAG = "Account Fragment";

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        client = view.findViewById(R.id.client_profile);
        advocate = view.findViewById(R.id.advocate_profile);

        Button logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signout();
            }
        });
        userName = view.findViewById(R.id.user_name);
        userGender = view.findViewById(R.id.user_gender);
        userBirthday = view.findViewById(R.id.user_birthday);
        userPhone = view.findViewById(R.id.user_phone);
        userEmail = view.findViewById(R.id.user_email);

        clientCompany = view.findViewById(R.id.user_company);
        advocateBirthplace = view.findViewById(R.id.user_birthplace);
        advocateAddress = view.findViewById(R.id.user_address);
        advocateStatus = view.findViewById(R.id.user_status);
        advocateExperience = view.findViewById(R.id.user_experience);
        advocateExpertise = view.findViewById(R.id.user_expertise);
        advocateCertificateNumber = view.findViewById(R.id.user_certificate);
        advocateCard = view.findViewById(R.id.user_advocate_card);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) getUserData(mAuth.getCurrentUser().getEmail());

        return view;
    }

    private void signout() {
        mAuth.signOut();
        if (getActivity() != null) {
            getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

    private void getUserData(final String email) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot root : dataSnapshot.getChildren()) {
                    for (DataSnapshot snapshot : root.getChildren()) {
                        if (email.equals(snapshot.child("email").getValue(String.class))) {
                            if ("client".equals(root.getKey())) {
                                client.setVisibility(View.VISIBLE);
                                clientCompany.setText(snapshot.child("company").getValue(String.class));
                            } else if ("advocate".equals(root.getKey())) {
                                advocate.setVisibility(View.VISIBLE);
                                advocateAddress.setText(snapshot.child("address").getValue(String.class));
                                advocateBirthplace.setText(snapshot.child("birthplace").getValue(String.class));
                                advocateStatus.setText(snapshot.child("status").getValue(String.class));
                                advocateExperience.setText(snapshot.child("experience").getValue(String.class));
                                advocateExpertise.setText(snapshot.child("expertise").getValue(String.class));
                                advocateCertificateNumber.setText(snapshot.child("certificateNumber").getValue(String.class));
                                advocateCard.setText(snapshot.child("advocateCard").getValue(String.class));
                            }
                            userName.setText(snapshot.child("name").getValue(String.class));
                            userGender.setText(snapshot.child("gender").getValue(String.class));
                            userBirthday.setText(snapshot.child("birthday").getValue(String.class));
                            userPhone.setText(snapshot.child("phone").getValue(String.class));
                            userEmail.setText(snapshot.child("email").getValue(String.class));
                        }
                        progressDialog.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
