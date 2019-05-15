package com.backers.backers;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.backers.backers.json.Ticket;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketActivity extends AppCompatActivity {


    App app;
    int ticketId=0;
    Ticket ticket;
    FitHeightListView listView;


    Adapter adapter;


    @BindView(R.id.title)TextView title;
    @BindView(R.id.product) TextView product;
    @BindView(R.id.date) TextView date;
    @BindView(R.id.status) TextView status;
    @BindView(R.id.description) TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        ButterKnife.bind(this);
        app=(App)getApplication();


        ticketId=getIntent().getIntExtra("ticketId",0);
        if(ticketId==0){
            finish();
            return;
        }
        listView=(FitHeightListView)findViewById(R.id.dialog_list);
        adapter = new Adapter();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        ActionBar ab=getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();


        RequestQueue queue = Volley.newRequestQueue(this);

        if (AppUtils.isNetworkAvailable(TicketActivity.this)) {
            if (AppUtils.isInternetAccessible(TicketActivity.this)) {
                String url =App.SERVER_URL+"/ticket/"+ticketId+"/"+app.setting.getString("token","");
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.v("0000",response);
                                Gson gson=new Gson();
                                ticket=gson.fromJson(response, Ticket.class);
                                title.setText(ticket.title);
                                product.setText(ticket.product);
                                date.setText(ticket.date);
                                status.setText(ticket.status.substring(2));
                                description.setText(ticket.description);


                                if(ticket.dialog==null){
                                    ticket.dialog=new ArrayList<>();
                                }
                                listView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
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
                AppUtils.showNoInternetAccessibleAlert(TicketActivity.this);
            }
        } else {
            AppUtils.showWiFiSettingsAlert(TicketActivity.this);
        }

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {

//            EditText reviewEditext=(EditText)findViewById(R.id.review);
//            review=reviewEditext.getText().toString();
//
//            token=app.setting.getString("token","");
//
//
//            RequestQueue queue = Volley.newRequestQueue(this);
//            String url =App.SERVER_URL+"/product/"+pid+"/review";
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        @Override
//                        public void onResponse(String response) {
//
//                            Log.v("0000",response);
//                            if("0".equals(response)){
//                                finish();
//                                return;
//                            }
//                            finish();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.v("0000","VolleyError:"+error.getMessage());
//                            String json= null;
//                            try {
//                                json = new String(error.networkResponse.data,
//                                        HttpHeaderParser.parseCharset(error.networkResponse.headers));
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                            Log.v("0000","VolleyError:"+json);
//                        }
//                    }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> map = new HashMap<String, String>();
//                    map.put("review", review);
//                    map.put("token", token);
//                    return map;
//                }
//            };
//
//            queue.add(stringRequest);
//
//
//            return true;
        }
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void reply(View v){
        Intent i=new Intent(this,ReplyTicketActivity.class);
        i.putExtra("ticketId",ticketId);
        startActivity(i);
    }




    class Adapter extends BaseAdapter
    {
        LayoutInflater mInflater;

        public Adapter()
        {
            mInflater = (LayoutInflater) TicketActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return ticket.dialog.size();
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
                convertView=mInflater.inflate(R.layout.ticket_dialog_list_item, parent,false);
                //inflate custom layour
                vh.img= (ImageView) convertView.findViewById(R.id.img);
                vh.name= (CustomFontTextView)convertView.findViewById(R.id.name);
                vh.message= (CustomFontTextView)convertView.findViewById(R.id.message);
                vh.date= (CustomFontTextView)convertView.findViewById(R.id.date);
                convertView.setTag(vh);
            }
            else
            {
                vh = (ViewHolder) convertView.getTag();
            }

            Ticket.Dialog oneDialog=ticket.dialog.get(position);

            if (oneDialog.userImg.isEmpty()) {
                vh.img.setImageResource(R.mipmap.ic_launcher);
            } else{
                Picasso.get().load(oneDialog.userImg).into(vh.img);
            }

            vh.name.setText(oneDialog.userName);
            vh.message.setText(oneDialog.reply);
            vh.date.setText(oneDialog.date);

            return convertView;
        }

        class ViewHolder
        {
            ImageView img;
            CustomFontTextView name,message,date;
        }

    }
}
