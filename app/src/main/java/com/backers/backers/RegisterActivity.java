package com.backers.backers;

import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity  implements QRCodeReaderView.OnQRCodeReadListener {

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;

    private boolean shouldScan=true;

    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        app=(App)getApplication();



        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
//        qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();



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
        return true;
    }

    String tag;
    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        if(shouldScan){
            shouldScan=false;
            qrCodeReaderView.stopCamera();

            tag=text;
            app.setting.edit().putString("product_tag", tag).commit();
            if (AppUtils.isNetworkAvailable(RegisterActivity.this)) {
                if (AppUtils.isInternetAccessible(RegisterActivity.this)) {
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url =App.SERVER_URL+"/product/register";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Intent i=null;
                                    if("0".equals(response)){
                                        Log.d("RegisterActivity", " "+ response);
                                        Log.d("RegisterActivity : ", " "+ app.setting.getString("product_tag", ""));
                                        i= new Intent(getApplicationContext(), RegisterSuccessActivity.class);
                                    }else{
                                        i= new Intent(getApplicationContext(), RegisterFailActivity.class);
                                    }
                                    startActivity(i);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("0000","VolleyError:"+error.getMessage());
                                    String json= null;
                                    try {
                                        json = new String(error.networkResponse.data,
                                                HttpHeaderParser.parseCharset(error.networkResponse.headers));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    Log.v("0000","VolleyError:"+json);


                                    Intent i=null;
                                    i= new Intent(getApplicationContext(), RegisterFailActivity.class);
                                    startActivity(i);
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("token", app.setting.getString("token",""));
                            map.put("tag", tag);
                            return map;
                        }
                    };

                    queue.add(stringRequest);
                } else {
                    AppUtils.showNoInternetAccessibleAlert(RegisterActivity.this);
                }
            } else {
                AppUtils.showWiFiSettingsAlert(RegisterActivity.this);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }
}
