package com.backers.backers;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 0;
    public boolean permissionForCameraGot = false;
    Handler h;
    Runnable r;

    App app;
    String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);


        getPermissionForCamera();

        app = (App) getApplication();
        token = app.setting.getString("token", "");
        if (!"".equals(token) && token != null) {
            Log.v("0", "onCreate");
            Log.v("0", "onCreate :" + token);
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }

    }


    public void facebook(View v) {

        if (AppUtils.isNetworkAvailable(LoginActivity.this)) {
            if (AppUtils.isInternetAccessible(LoginActivity.this)) {
                startActivity(new Intent(this, FacebookLoginActivity.class));
            } else {
                AppUtils.showNoInternetAccessibleAlert(LoginActivity.this);
            }
        } else {
            AppUtils.showWiFiSettingsAlert(LoginActivity.this);
        }
    }

    public void google(View v) {
        if (AppUtils.isNetworkAvailable(LoginActivity.this)) {
            if (AppUtils.isInternetAccessible(LoginActivity.this)) {
                startActivity(new Intent(this, GoogleLoginActivity.class));
            } else {
                AppUtils.showNoInternetAccessibleAlert(LoginActivity.this);
            }
        } else {
            AppUtils.showWiFiSettingsAlert(LoginActivity.this);
        }

    }


    private void getPermissionForCamera() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            permissionForCameraGot = true;
            return;
        }
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE,
                                Manifest.permission.VIBRATE,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WAKE_LOCK,
                                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.NFC,
                                Manifest.permission.MANAGE_DOCUMENTS
                        },
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            permissionForCameraGot = true;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionForCameraGot = true;

                    if (!"".equals(token) && token != null) {
                        Log.v("0", "onRequestPermissionsResult");
                        Intent i = new Intent(this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                    }
                } else {
                    //finish();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // check version on play store and force update
    public void forceUpdate(){
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo =  packageManager.getPackageInfo(getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        Log.d("currentVersion : ", currentVersion);
        new ForceUpdateAsync(currentVersion,LoginActivity.this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //forceUpdate();
    }
}
