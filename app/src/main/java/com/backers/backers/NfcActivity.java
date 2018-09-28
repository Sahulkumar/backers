package com.backers.backers;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.backers.backers.lib.NfcAppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class NfcActivity extends NfcAppCompatActivity {

    App app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);
        app=(App)getApplication();


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

    String tagString;
    @Override
    protected void onNewIntent(Intent intent) {




        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            String type = intent.getType();

//            if ("camera/blue".equals(type)) {
//                askToCameraBlue();
//                return;
//            }
//
//            if (!"air/button".equals(type)) {
//                askRecover();
//                return;
//            }

            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] tagId = tag.getId();
            tagString = bytesToHex(tagId);
            app.setting.edit().putString("product_tag", tagString).commit();
            if (AppUtils.isNetworkAvailable(NfcActivity.this)) {
                if (AppUtils.isInternetAccessible(NfcActivity.this)) {
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url =App.SERVER_URL+"/product/register";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.v("0000",response);
                                    Intent i=null;
                                    if("0".equals(response)){
                                        Log.d("NfcActivity", " "+ response);
                                        Log.d("NfcActivity : ", " "+ app.setting.getString("product_tag", ""));
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
                            Log.v("0000",tagString);
                            map.put("tag", tagString);
                            return map;
                        }
                    };

                    queue.add(stringRequest);
                } else {
                    AppUtils.showNoInternetAccessibleAlert(NfcActivity.this);
                }
            } else {
                AppUtils.showWiFiSettingsAlert(NfcActivity.this);
            }

        }



    }


    //very fast
    final protected static char[] hexArray = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
