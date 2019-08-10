package com.iqbaal.ruangadvokat.register;

import android.os.Bundle;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.iqbaal.ruangadvokat.R;
import com.iqbaal.ruangadvokat.register.fragment.AdvocateFragment;
import com.iqbaal.ruangadvokat.register.fragment.ClientFragment;
import com.iqbaal.ruangadvokat.register.fragment.LawFirmFragment;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        MaterialCardView client = findViewById(R.id.client);
        MaterialCardView advocat = findViewById(R.id.advocat);
        MaterialCardView lawFirm = findViewById(R.id.law_firm);

        client.setOnClickListener(this);
        advocat.setOnClickListener(this);
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
        frameLayout = findViewById(R.id.container);
        frameLayout.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        if (frameLayout.getVisibility() == View.VISIBLE) frameLayout.setVisibility(View.GONE);
        else super.onBackPressed();
    }
}
