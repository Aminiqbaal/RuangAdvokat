package com.iqbaal.ruangadvokat.register.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iqbaal.ruangadvokat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdvocatFragment extends Fragment {


    public AdvocatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_advocat, container, false);
    }

}