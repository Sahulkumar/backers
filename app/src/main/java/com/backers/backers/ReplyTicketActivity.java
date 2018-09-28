package com.backers.backers;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ReplyTicketActivity extends AppCompatActivity {

    App app;
    int ticketId=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_ticket);
        app=(App)getApplication();


        ticketId=getIntent().getIntExtra("ticketId",0);
        if(ticketId==0){
            finish();
            return;
        }

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


    EditText reply;
    String token;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {

            reply =(EditText)findViewById(R.id.reply);

            token=app.setting.getString("token","");

            if (AppUtils.isNetworkAvailable(ReplyTicketActivity.this)) {
                if (AppUtils.isInternetAccessible(ReplyTicketActivity.this)) {
                    RequestQueue queue = Volley.newRequestQueue(ReplyTicketActivity.this);
                    String url =App.SERVER_URL+"/ticket/reply";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    if("error".equals(response)){
                                        Toast.makeText(getApplicationContext(),"error",Toast.LENGTH_SHORT).show();
                                    }else{
                                        finish();
                                    }
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
                            map.put("reply", reply.getText().toString());
                            map.put("ticketId", ticketId+"");
                            map.put("token", token);
                            return map;
                        }
                    };

                    queue.add(stringRequest);
                } else {
                    AppUtils.showNoInternetAccessibleAlert(ReplyTicketActivity.this);
                }
            } else {
                AppUtils.showWiFiSettingsAlert(ReplyTicketActivity.this);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
