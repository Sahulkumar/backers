package com.backers.backers;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.backers.backers.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class WriteReviewActivity extends AppCompatActivity {

    App app;
    int pid=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        pid=getIntent().getIntExtra("pid",0);
        if(pid==0){
            finish();
            return;
        }

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_ticket, menu);
        return true;
    }



    String review;
    String token;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {

            EditText reviewEditext=(EditText)findViewById(R.id.review);
            review=reviewEditext.getText().toString();

            token=app.setting.getString("token","");


            RequestQueue queue = Volley.newRequestQueue(this);

            if (AppUtils.isNetworkAvailable(WriteReviewActivity.this)) {
                if (AppUtils.isInternetAccessible(WriteReviewActivity.this)) {
                    String url =App.SERVER_URL+"/product/"+pid+"/review";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.v("0000",response);
                                    if("0".equals(response)){
                                        finish();
                                        return;
                                    }
                                    finish();
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
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("review", review);
                            map.put("token", token);
                            return map;
                        }
                    };

                    queue.add(stringRequest);
                } else {
                    AppUtils.showNoInternetAccessibleAlert(WriteReviewActivity.this);
                }
            } else {
                AppUtils.showWiFiSettingsAlert(WriteReviewActivity.this);
            }



            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
