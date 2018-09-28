package com.backers.backers;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class RegisterSuccessActivity extends AppCompatActivity {
    Boolean activityStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_success);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                if(!activityStarted) {
                    goToAddProduct();
                }
            }
        },2000);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        if(!activityStarted) {
            goToAddProduct();
        }
        return true;
    }

    private void goToAddProduct() {
        activityStarted = true;
        Log.d("RegisterSuccess : " , "goToAddProduct");
        Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        if(!activityStarted) {
            goToAddProduct();
        }
    }
}
