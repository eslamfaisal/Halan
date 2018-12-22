package com.fekrah.halan.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;

import com.fekrah.halan.R;
import com.fekrah.halan.customer.CustomerMainActivity;
import com.fekrah.halan.driver.activities.MainActivityDriver;
import com.fekrah.halan.helper.SharedHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends BaseActivity {

    private Handler handleCheckStatus;
    LinearLayout errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handleCheckStatus = new Handler();

        handleCheckStatus.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null && SharedHelper.getKey(getApplicationContext(), "uses_type").equals("driver")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivityDriver.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, CustomerMainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

        }, 2000);
    }
}
