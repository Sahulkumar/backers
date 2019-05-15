package com.backers.backers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FacebookLoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    App app;
    String token = "";
    LoginButton loginButton;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_login);
        app = (App) getApplication();


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                dialog = ProgressDialog.show(FacebookLoginActivity.this, "", "Authenticating");
                int i = 0;
                token = loginResult.getAccessToken().getToken();
                Log.v("0000", "onsuccess");
                Log.v("0000", "token:" + token);


                RequestQueue queue = Volley.newRequestQueue(FacebookLoginActivity.this);
                String url = App.SERVER_URL + "/newuser/facebook";

// Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                dialog.dismiss();

                                Log.v("0000", "onsuccess");
                                Log.v("0000", response);
                                try {
                                    app.setting.edit().putString("token", response).commit();
                                    app.setting.edit().putString("login_type", "facebook").commit();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                } catch (com.google.gson.JsonSyntaxException ex) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                AppUtils.showShortToast(FacebookLoginActivity.this, "Login Failed");
                                Log.v("0000", "VolleyError:" + error.getMessage());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("accessToken", token);
                        return map;
                    }
                };
                stringRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {
                        return 50000;
                    }

                    @Override
                    public int getCurrentRetryCount() {
                        return 50000;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {
                        dialog.dismiss();
                        AppUtils.showShortToast(FacebookLoginActivity.this, "Login Failed");
                        Log.v("0000", "VolleyError:" + error.getMessage());
                    }
                });
// Add the request to the RequestQueue.
                queue.add(stringRequest);
            }

            @Override
            public void onError(FacebookException error) {


                Log.v("0", "onError");
                Log.v("0000", "error:" + error.getMessage());
                AppUtils.showShortToast(FacebookLoginActivity.this, "Facebook Login Failed");
            }

            @Override
            public void onCancel() {
                finish();
                Log.v("0", "onCancel");
            }
        });

        loginButton.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
