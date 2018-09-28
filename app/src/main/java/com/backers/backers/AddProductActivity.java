package com.backers.backers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.backers.backers.http.AppHelper;
import com.backers.backers.http.AppSingleton;
import com.backers.backers.http.VolleyMultipartRequest;
import com.mindorks.paracamera.Camera;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    String TAG = AddProductActivity.class.getSimpleName();
    TextView mAddDateTV, mAddReceiptTv;
    ImageView mAddReceiptIV;
    Button mAddProductB;
    Camera camera;
    Boolean dateAvailable = false;
    Boolean imageAvailable = false;
    App app;
    String date = "";
    Bitmap bitmap;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        app = (App) getApplication();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initViews();
        initCamera();
        initViewListeners();
    }

    private void initViews() {
        mAddDateTV = (TextView) findViewById(R.id.product_calender_tv);
        mAddReceiptIV = (ImageView) findViewById(R.id.product_receipt_iv);
        mAddReceiptTv = (TextView) findViewById(R.id.product_receipt_tv);
        mAddProductB = (Button) findViewById(R.id.product_add_b);
    }

    private void initViewListeners() {
        mAddDateTV.setOnClickListener(this);
        mAddReceiptIV.setOnClickListener(this);
        mAddReceiptTv.setOnClickListener(this);
        mAddProductB.setOnClickListener(this);
        enableAddButton(false);
    }

    private void initCamera() {
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);
    }

    private void enableAddButton(boolean enable) {
        //mAddProductB.setEnabled(enable);
        if (enable) {
            mAddProductB.setAlpha(1);
        } else {
            mAddProductB.setAlpha(.5f);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_calender_tv:
                showDatePicker();
                break;
            case R.id.product_receipt_iv:
                try {
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.product_receipt_tv:
                try {
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.product_add_b:
                if (dateAvailable && imageAvailable) {
                    dialog = ProgressDialog.show(this, "", "Please wait");
                    submitReceipt();
                } else {
                    AppUtils.showShortToast(this, "Date and Receipt Image are required");
                }

                break;

        }
    }

    private void showDatePicker() {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(AddProductActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMaxDate(now);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mAddDateTV.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        date = year + "/" + (monthOfYear + 1) + "/" + dayOfMonth;
        dateAvailable = true;
        if (dateAvailable && imageAvailable) {
            enableAddButton(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            bitmap = camera.getCameraBitmap();
            if (bitmap != null) {
                imageAvailable = true;
                mAddReceiptIV.setImageBitmap(bitmap);
                mAddReceiptTv.setVisibility(View.VISIBLE);
                if (dateAvailable && imageAvailable) {
                    enableAddButton(true);
                }
            } else {
                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.deleteImage();
    }


    public void submitReceipt() {
        //String url = "http://54.212.215.106/backers/product/5111/upload_receipt";
        //String product_tag = "TEST_NULLURL_01";
        String product_tag = app.setting.getString("product_tag", "");
        String url = App.SERVER_URL + "/product/" + product_tag + "/upload_receipt";

        Log.d("AddProductActivity : ", url);
        Log.d("AddProductActivity : ", product_tag);

        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                dialog.dismiss();
                Log.d(TAG, "onResponse : " + response.headers);
                Log.d(TAG, "onResponse : " + response.data);
                Log.d(TAG, "onResponse : " + response.statusCode);
                Log.d(TAG, "onResponse : " + response.networkTimeMs);
                Log.d(TAG, "onResponse : " + response.notModified);
                Log.d(TAG, "onResponse : " + response.toString());

                String resultResponse = new String(response.data);
                Log.d(TAG, "Resp: " + resultResponse);
                AppUtils.showShortToast(AddProductActivity.this, "Submitted Successfully");
                AddProductActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                try {
                    if (volleyError != null) {
                        NetworkResponse networkResponse = volleyError.networkResponse;
                        Log.d(TAG, "Error: " + volleyError.networkResponse);
                        AppUtils.showShortToast(AddProductActivity.this, "Failed");
                        Log.d(TAG, "parseVolleyError : " + parseVolleyError(volleyError));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", app.setting.getString("token", ""));
                params.put("purchase_date", date);
                return params;
            }

            /*@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + app.setting.getString("token", ""));
                return params;
            }*/

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("file", new DataPart(product_tag + ".jpg", AppHelper.getFileDataFromBitmap(AddProductActivity.this, bitmap), "image/jpeg"));

                return params;
            }
        };

        AppSingleton.getInstance(this).addToRequestQueue(multipartRequest, "com.backers.backers");

    }


    public String parseVolleyError(VolleyError error) {
        String message = "";
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            Log.d(TAG, "message : " + data);
            message = data.getString("msg");
            Log.d(TAG, "message : " + message);
        } catch (JSONException e) {
            Log.d(TAG, "Error: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "Error: " + e.getMessage());
        }
        return message;
    }
}

