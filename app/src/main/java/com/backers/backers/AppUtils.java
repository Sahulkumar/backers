package com.backers.backers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class AppUtils {
    private final String LOG_TAG = getClass().getSimpleName();
    private static ProgressDialog dialog;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    public static boolean isInternetAccessible(Context context) {
        boolean isDataAccessible = false;
        try {
            isDataAccessible = new isDataAccessible().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return isDataAccessible;
    }


    private static class isDataAccessible extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                HttpURLConnection url = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                url.setRequestProperty("User-Agent", "Test");
                url.setRequestProperty("Connection", "close");
                url.setConnectTimeout(1500);
                url.connect();
                return (url.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("isInternetAccessible", "Couldn't check internet connection", e);
            }
            return false;
        }
    }

    public static void showNoInternetAccessibleAlert(Context context) {
        Toast.makeText(context, " Internet is not Accessible" , Toast.LENGTH_SHORT).show();
    }


    public static void showWiFiSettingsAlert(Context context) {
        Toast.makeText(context , " Please connect to internet" , Toast.LENGTH_SHORT).show();
    }

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
