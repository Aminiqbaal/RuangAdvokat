package com.iqbaal.ruangadvokat.main.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.iqbaal.ruangadvokat.CallActivity;
import com.iqbaal.ruangadvokat.konsultasi.KonsultasiActivity;
import com.iqbaal.ruangadvokat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {
    LinearLayout consultation, read, law, document;
    FirebaseAuth mAuth;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mAuth = FirebaseAuth.getInstance();
        consultation = view.findViewById(R.id.consultation);
        read = view.findViewById(R.id.read);
        law = view.findViewById(R.id.law);
        document = view.findViewById(R.id.document);

        consultation.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case
                    R.id.consultation:
                startActivity(new Intent(getContext(), CallActivity.class));
        }
    }
}
