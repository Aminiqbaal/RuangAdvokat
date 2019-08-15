package com.iqbaal.ruangadvokat.register;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.iqbaal.ruangadvokat.R;
import com.iqbaal.ruangadvokat.api.ApiService;
import com.iqbaal.ruangadvokat.model.CaptchaRequest;
import com.iqbaal.ruangadvokat.model.CaptchaResponse;
import com.iqbaal.ruangadvokat.register.fragment.AdvocateFragment;
import com.iqbaal.ruangadvokat.register.fragment.ClientFragment;
import com.iqbaal.ruangadvokat.register.fragment.LawFirmFragment;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.iqbaal.ruangadvokat.helper.Global.SITE_KEY;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout frameLayout;
    private String TAG = "Register Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        frameLayout = findViewById(R.id.container);
        MaterialCardView client = findViewById(R.id.client);
        MaterialCardView advocate = findViewById(R.id.advocat);
        MaterialCardView lawFirm = findViewById(R.id.law_firm);

        client.setOnClickListener(this);
        advocate.setOnClickListener(this);
        lawFirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.client:
                moveFragment(new ClientFragment());
                break;
            case R.id.advocat:
                moveFragment(new AdvocateFragment());
                break;
            case R.id.law_firm:
                moveFragment(new LawFirmFragment());
                break;
        }
    }

    private void moveFragment(Fragment fragment) {
        frameLayout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (frameLayout.getVisibility() == View.VISIBLE) frameLayout.setVisibility(View.GONE);
        else super.onBackPressed();
    }
}
