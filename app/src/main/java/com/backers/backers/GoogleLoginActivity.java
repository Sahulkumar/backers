package com.backers.backers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GoogleLoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 0;
    String idToken = "";
    App app;
    SignInButton signInButton;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        app = (App) getApplication();


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestIdToken("217457847200-j07epdbg9t1e9t882jnhvp6uo3or7vb7.apps.googleusercontent.com")
                .requestServerAuthCode("217457847200-j07epdbg9t1e9t882jnhvp6uo3or7vb7.apps.googleusercontent.com")
                /*.requestIdToken("299583366901-22jhqj3j90hj6bb6ssblqu21hsm8667j.apps.googleusercontent.com")
                .requestServerAuthCode("299583366901-22jhqj3j90hj6bb6ssblqu21hsm8667j.apps.googleusercontent.com")*/
                /*.requestIdToken("299583366901-q2f1psskd4crb51jgdc8m3947aoqlbim.apps.googleusercontent.com")
                .requestServerAuthCode("299583366901-q2f1psskd4crb51jgdc8m3947aoqlbim.apps.googleusercontent.com")*/
                .requestEmail()
                .build();

        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestId()
                .requestIdToken("217457847200-a8fbkelv9cb6jp7s4toi85t6o36g5akg.apps.googleusercontent.com")
                .requestServerAuthCode("217457847200-a8fbkelv9cb6jp7s4toi85t6o36g5akg.apps.googleusercontent.com")
                .requestEmail()
                .build();*/

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        //Log.v("0000", "  onActivityResult: requestCode :" + requestCode + " ->  resultCode : " + resultCode + "  -> data :" + data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Log.v("0000", "isSuccess: " + result.getStatus());
            Log.v("0000", "isSuccess: " + result.getSignInAccount());

            dialog = ProgressDialog.show(GoogleLoginActivity.this, "", "Authenticating");
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String accName = acct.getEmail();
            idToken = acct.getIdToken();
            String auth = acct.getServerAuthCode();


            RequestQueue queue = Volley.newRequestQueue(GoogleLoginActivity.this);
            String url = App.SERVER_URL + "/newuser/google";

// Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if ("".equals(response)) {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            try {
                                dialog.dismiss();
                                app.setting.edit().putString("token", response).commit();
                                app.setting.edit().putString("login_type", "google").commit();
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
                            AppUtils.showShortToast(GoogleLoginActivity.this, "Login Failed");
                            Log.v("0000", "VolleyError:" + error.getMessage());
                            String json = null;
                            try {
                                json = new String(error.networkResponse.data,
                                        HttpHeaderParser.parseCharset(error.networkResponse.headers));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            Log.v("0000", "VolleyError:" + json);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("accessToken", idToken);
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
                    AppUtils.showShortToast(GoogleLoginActivity.this, "Login Failed");
                    Log.v("0000", "VolleyError:" + error.getMessage());
                }
            });
            queue.add(stringRequest);

        } else {

            Log.v("0000", "else:" + result.getStatus());
            Log.v("0000", "else:" + result.getSignInAccount());

            // Signed out, show unauthenticated UI.
            AppUtils.showShortToast(GoogleLoginActivity.this, "Login Failed");
            finish();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.v("0000", "onConnectionFailed:" + connectionResult.getErrorMessage());
    }
}
