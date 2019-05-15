package com.backers.backers;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.backers.backers.json.Product;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductActivity extends AppCompatActivity {

    FitHeightListView reviewListView;
    FitHeightListView faqListView;
    ReviewAdapter adapter;
    FaqAdapter faqadapter;


    boolean fullReview=false;
    boolean fullDesc=false;
    int pid=0;
    App app;

    RequestQueue queue;

    Product p;


    @BindView(R.id.product_image) ImageView product_image;
    @BindView(R.id.product_name) TextView product_name;
    @BindView(R.id.warranty_left) TextView warranty_left;
    @BindView(R.id.product_detail) TextView product_detail;
    @BindView(R.id.like) LikeButton like;
    @BindView(R.id.scroll) ScrollView scroll;
    TextView review_read_all;
    TextView product_read_more;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        ButterKnife.bind(this);
        app=(App)getApplication();


        pid=getIntent().getIntExtra("pid",0);
        if(pid==0){
            finish();
            return;
        }

        reviewListView=(FitHeightListView)findViewById(R.id.review_list);
        faqListView=(FitHeightListView)findViewById(R.id.faq_list);


        View header =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.product_review_list_header, null, false);
        reviewListView.addHeaderView(header);
        View footerView =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.product_review_list_footer, null, false);
        reviewListView.addFooterView(footerView);
        View faqHeader =  ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.product_faq_list_header, null, false);
        faqListView.addHeaderView(faqHeader);



        adapter = new ReviewAdapter();
        faqadapter = new FaqAdapter();





        like.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

                if (AppUtils.isNetworkAvailable(ProductActivity.this)) {
                    if (AppUtils.isInternetAccessible(ProductActivity.this)) {
                        String url =App.SERVER_URL+"/product/"+pid+"/like";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.v("0",response);
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
                                }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("token", app.setting.getString("token",""));
                                return map;
                            }

                        };

                        queue.add(stringRequest);
                    } else {
                        AppUtils.showNoInternetAccessibleAlert(ProductActivity.this);
                    }
                } else {
                    AppUtils.showWiFiSettingsAlert(ProductActivity.this);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                ProductCard card=(ProductCard)likeButton.getTag();
                if (AppUtils.isNetworkAvailable(ProductActivity.this)) {
                    if (AppUtils.isInternetAccessible(ProductActivity.this)) {
                        String url =App.SERVER_URL+"/product/"+pid+"/unlike";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                        Log.v("0",response);
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
                                }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<String, String>();
                                map.put("token", app.setting.getString("token",""));
                                return map;
                            }

                        };
                        queue.add(stringRequest);
                    } else {
                        AppUtils.showNoInternetAccessibleAlert(ProductActivity.this);
                    }
                } else {
                    AppUtils.showWiFiSettingsAlert(ProductActivity.this);
                }

            }
        });



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
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = p.share;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, p.name);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();



        queue = Volley.newRequestQueue(this);

        if (AppUtils.isNetworkAvailable(ProductActivity.this)) {
            if (AppUtils.isInternetAccessible(ProductActivity.this)) {
                String url =App.SERVER_URL+"/user-product/"+pid+"/"+app.setting.getString("token","");
                Log.v("0000",url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.v("0000",response);
                                Gson gson=new Gson();
                                p=gson.fromJson(response,Product.class);

                                if (p.img.isEmpty()) {
                                    product_image.setImageResource(R.mipmap.ic_launcher);
                                } else{
                                    Picasso.get().load(p.img).into(product_image);
                                }

                                product_name.setText(p.name);
                                warranty_left.setText(p.warranty+" days");
                                int maxLength=60;
                                if(p.detail.length()>maxLength){
                                    product_detail.setText(p.detail.substring(0,maxLength)+"...");
                                }else{
                                    product_detail.setText(p.detail);
                                }
                                like.setLiked(p.liked);




                                if(p.review==null){
                                    p.review=new ArrayList<>();
                                }
                                reviewListView.setAdapter(adapter);

                                if(p.faq==null){
                                    p.faq=new ArrayList<>();
                                }
                                faqListView.setAdapter(faqadapter);


                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {

                                        scroll.fullScroll(ScrollView.FOCUS_UP);
                                        scroll.scrollTo(0, 0);
                                    }
                                });

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
                        });

                queue.add(stringRequest);
            } else {
                AppUtils.showNoInternetAccessibleAlert(ProductActivity.this);
            }
        } else {
            AppUtils.showWiFiSettingsAlert(ProductActivity.this);
        }

    }

    public void readMore(View v){
        if(product_read_more==null) {
            product_read_more = (TextView) findViewById(R.id.more);
        }

//        fullDesc=!fullDesc;
        fullDesc=true;
        if(fullDesc){
//            product_read_more.setText("Read Less");
            product_read_more.setText("");
            product_detail.setText(p.detail);
        }else{
            product_read_more.setText("Read More");
            int maxLength=60;
            if(p.detail.length()>maxLength){
                product_detail.setText(p.detail.substring(0,maxLength)+"...");
            }
        }

    }
    public void viewAllReview(View v){
        if(review_read_all==null) {
            review_read_all = (TextView) findViewById(R.id.review_read_all);
        }

//        fullReview=!fullReview;
        fullReview=true;
        if(fullReview){
            review_read_all.setText("");
        }else{
            review_read_all.setText("Read All");
        }

    }
    public void writeReview(View v){

        Intent i=new Intent(this,WriteReviewActivity.class);
        i.putExtra("pid",pid);
        startActivity(i);
    }
    public void askSupport(View v){

        Intent i=new Intent(this,NewTicketActivity.class);
        i.putExtra("pid",pid);
        startActivity(i);
    }



    class ReviewAdapter extends BaseAdapter
    {
        LayoutInflater mInflater;

        public ReviewAdapter()
        {
            mInflater = (LayoutInflater) ProductActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {

//            Log.v("0000","getcount");
            if(p.review.size() > 2 && !fullReview){
                return 2;
            }
            return p.review.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.v("0000","getView");
            ViewHolder vh;


            if(convertView==null )
            {
                vh= new ViewHolder();
                convertView=mInflater.inflate(R.layout.product_review_list_item, parent,false);
                //inflate custom layour
                vh.img= (ImageView)convertView.findViewById(R.id.img);
                vh.name= (CustomFontTextView)convertView.findViewById(R.id.name);
                vh.review= (CustomFontTextView)convertView.findViewById(R.id.review);
                vh.date= (CustomFontTextView)convertView.findViewById(R.id.date);
                convertView.setTag(vh);
            }
            else
            {
                vh = (ViewHolder) convertView.getTag();
            }
            //vh.tv2.setText("Position = "+position);
//            vh.tv2.setText(data_array[position]);
            //set text of second textview based on position
            Product.Review oneReview=p.review.get(position);
            vh.name.setText(oneReview.name);
            vh.review.setText(oneReview.review);
            vh.date.setText(oneReview.date);


            if(oneReview.img == null){
                vh.img.setImageResource(R.mipmap.ic_launcher);
            } else if (oneReview.img.isEmpty()) {
                vh.img.setImageResource(R.mipmap.ic_launcher);
            } else{
                Picasso.get().load(oneReview.img).into(vh.img);
            }
            return convertView;
        }

        class ViewHolder
        {
            ImageView img;
            CustomFontTextView name,review,date;
        }

    }

    class FaqAdapter extends BaseAdapter
    {
        LayoutInflater mInflater;

        public FaqAdapter()
        {
            mInflater = (LayoutInflater) ProductActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return p.faq.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder vh;

            if(convertView==null )
            {
                vh= new ViewHolder();
                convertView=mInflater.inflate(R.layout.product_faq_list_item, parent,false);
                //inflate custom layour
                vh.question= (CustomFontTextView)convertView.findViewById(R.id.question);
                vh.answer= (CustomFontTextView)convertView.findViewById(R.id.answer);
                convertView.setTag(vh);
            }
            else
            {
                vh = (ViewHolder) convertView.getTag();
            }

            Product.Faq oneFaq=p.faq.get(position);
            vh.question.setText(oneFaq.question);
            vh.answer.setText(oneFaq.answer);

            return convertView;
        }

        class ViewHolder
        {
            CustomFontTextView question, answer;
        }

    }
}
