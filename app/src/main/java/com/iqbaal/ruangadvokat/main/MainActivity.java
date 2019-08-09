package com.iqbaal.ruangadvokat.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.iqbaal.ruangadvokat.R;
import com.iqbaal.ruangadvokat.main.fragment.AccountFragment;
import com.iqbaal.ruangadvokat.main.fragment.HomeFragment;
import com.iqbaal.ruangadvokat.main.fragment.NotificationFragment;
import com.iqbaal.ruangadvokat.main.fragment.ScheduleFragment;
import com.iqbaal.ruangadvokat.main.fragment.StatusFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigation = findViewById(R.id.bottom_nav);

        moveFragment(new HomeFragment());
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.home:
                        moveFragment(new HomeFragment());break;
                    case R.id.schedule:
                        moveFragment(new ScheduleFragment());break;
                    case R.id.status:
                        moveFragment(new StatusFragment());break;
                    case R.id.notification:
                        moveFragment(new NotificationFragment());break;
                    case R.id.account:
                        moveFragment(new AccountFragment());break;
                }
                return true;
            }
        });
    }

    private void moveFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}
