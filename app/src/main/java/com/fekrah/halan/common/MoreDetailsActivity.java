package com.fekrah.halan.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.fekrah.halan.R;

public class MoreDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details);
    }

    public void goWeb(View view) {
        startActivity(new Intent(this,WebViewActivity.class));
    }
}
