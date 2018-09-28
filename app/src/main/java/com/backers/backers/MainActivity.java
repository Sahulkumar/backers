package com.backers.backers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.backers.backers.json.MainList;
import com.backers.backers.json.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.Gson;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    App app;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    RequestQueue queue;
    GoogleApiClient mGoogleApiClient;

    boolean showTicket = true;
    boolean showProduct = true;
    private String Log_tag = getClass().getSimpleName();

    ArrayList<Card> data = new ArrayList<Card>();
    ;

    MainList ml;

    Drawer drawer;
    View settingButton;
    AccountHeader headerResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();
        settingButton = findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer();
            }
        });

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        queue = Volley.newRequestQueue(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);


        makeDrawer();


        if (AppUtils.isNetworkAvailable(MainActivity.this)) {
            if (AppUtils.isInternetAccessible(MainActivity.this)) {
                String url = App.SERVER_URL + "/user/me/" + app.setting.getString("token", "");
                Log.d("Url : " , url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.v("0000", response);

                                Gson gson = new Gson();
                                User user = gson.fromJson(response, User.class);

                                headerResult.updateProfile(new ProfileDrawerItem().withIdentifier(11111).withName(user.name).withIcon(user.img));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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
                        });
                queue.add(stringRequest);
            } else {
                AppUtils.showNoInternetAccessibleAlert(MainActivity.this);
            }
        } else {
            AppUtils.showWiFiSettingsAlert(MainActivity.this);
        }

        Log.d(Log_tag, "Main Activity");
    }


    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void makeDrawer() {


        //initialize and create the image loader logic
        DrawerImageLoader loader = DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.get().cancelRequest(imageView);
            }


            @Override
            public Drawable placeholder(Context ctx) {
                return super.placeholder(ctx);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return super.placeholder(ctx, tag);
            }

            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                Picasso.get().load(uri).placeholder(placeholder).into(imageView);
            }
        });

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/tt0009m.ttf");
//        Uri uuu=Uri.parse();

        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withIdentifier(11111).withName("name").withIcon("http://icons.iconarchive.com/icons/graphicloads/100-flat/256/home-icon.png")
                )
                .withTypeface(tf)
                .withSelectionListEnabledForSingleProfile(false)
                .withTextColorRes(R.color.colorPrimary)
                .build();


//if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withIdentifier(1).withName("ALL").withTypeface(tf);
        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withIdentifier(2).withName("TICKET").withTypeface(tf);
        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withIdentifier(3).withName("PRODUCT").withTypeface(tf);
        PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(4).withName("LOGOUT").withTypeface(tf);
        SecondaryDrawerItem item4 = new SecondaryDrawerItem().withIdentifier(1).withName("ALL");
        SecondaryDrawerItem item5 = new SecondaryDrawerItem().withIdentifier(2).withName("TICKET");
        SecondaryDrawerItem item6 = new SecondaryDrawerItem().withIdentifier(3).withName("PRODUCT");
        SecondaryDrawerItem item8 = new SecondaryDrawerItem().withIdentifier(4).withName("LOGOUT");

//create the drawer and remember the `Drawer` result object
        drawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        item1,
                        item2,
                        item3,
                        item7
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {
                            case 1:
                                showTicket = true;
                                showProduct = true;
                                drawer.closeDrawer();
                                break;
                            case 2:
                                showTicket = true;
                                showProduct = false;
                                drawer.closeDrawer();
                                break;
                            case 3:
                                showTicket = false;
                                showProduct = true;
                                drawer.closeDrawer();
                                break;
                            case 4:
                                drawer.closeDrawer();
                                showLogoutDialog();
                                break;
                        }

                        data.clear();
                        if (showTicket) {
                            for (Card c : ml.ticket) {
                                data.add(c);
                            }
                        }
                        if (showProduct) {
                            for (Card c : ml.product) {
                                data.add(c);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        return true;
                    }
                })

                .build();


    }

    private void goToLogin() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Confirm Logout");
        alertDialog.setMessage("Are you sure you want Logout?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                logoutFromApp();
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void logoutFromApp() {
        app = (App) getApplication();
        String type = app.setting.getString("login_type", "");
        if (type.equals("google")) {
            Log.v("0", "login_type : " + type);
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            goToLogin();
                        }
                    });
            Log.v("0", "logout : " + type);
        }

        if (type.equals("facebook")) {
            Log.v("0", "login_type : " + type);
            LoginManager.getInstance().logOut();
            goToLogin();
            Log.v("0", "logout : " + type);
        }

        SharedPreferences settings = getApplicationContext().getSharedPreferences("backers", Context.MODE_PRIVATE);
        settings.edit().clear().apply();
        String token = app.setting.getString("token", "");
        String login_type = app.setting.getString("login_type", "");
        String product_tag = app.setting.getString("product_tag", "");
        Log.v("0", "token : " + token);
        Log.v("0", "login_type : " + login_type);
        Log.v("0", "product_tag : " + product_tag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
//            Intent intent=new Intent(getApplicationContext(),RegisterActivity.class);
//            startActivity(intent);
            Log.d(Log_tag, "Clicked + icon " + "options menu");
            SelectDialog dia = new SelectDialog();
            dia.show(getSupportFragmentManager(), "abc");
            //goToAddProduct();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToAddProduct() {
        Intent intent = new Intent(getApplicationContext(), AddProductActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppUtils.isNetworkAvailable(MainActivity.this)) {
            if (AppUtils.isInternetAccessible(MainActivity.this)) {
                String url = App.SERVER_URL + "/ticket-and-product/" + app.setting.getString("token", "");
                Log.d("Url : " , url);
                Log.v("0000", url);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.v("0000", response);
                                Gson gson = new Gson();
                                ml = gson.fromJson(response, MainList.class);

                                data = new ArrayList<>();
                                if (showTicket) {
                                    for (Card c : ml.ticket) {
                                        data.add(c);
                                    }
                                }
                                if (showProduct) {
                                    for (Card c : ml.product) {
                                        data.add(c);
                                    }
                                }
                                mAdapter = new MyAdapter(data);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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
                        });

                queue.add(stringRequest);
            } else {
                AppUtils.showNoInternetAccessibleAlert(MainActivity.this);
            }
        } else {
            AppUtils.showWiFiSettingsAlert(MainActivity.this);
        }

    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private ArrayList<Card> mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View v;

            public CustomFontTextView ticketTitle;
            public CustomFontTextView ticketStatus;


            public CustomFontTextView productName;
            public CustomFontTextView productWarranty;
            public ImageView productImage;
            public LikeButton like;


            public ViewHolder(View v) {
                super(v);
                this.v = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(ArrayList<Card> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v;

            if (viewType == Card.TYPE_TICKET) {

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_ticket, parent, false);
            } else if (viewType == Card.TYPE_PRODUCT) {

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_product, parent, false);
            } else {
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_add, parent, false);
            }
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);


            if (viewType == Card.TYPE_TICKET) {

                vh.ticketTitle = (CustomFontTextView) v.findViewById(R.id.title);
                vh.ticketStatus = (CustomFontTextView) v.findViewById(R.id.status);
            } else if (viewType == Card.TYPE_PRODUCT) {

                vh.productName = (CustomFontTextView) v.findViewById(R.id.name);
                vh.productWarranty = (CustomFontTextView) v.findViewById(R.id.warranty);
                vh.productImage = (ImageView) v.findViewById(R.id.body);
                vh.like = (LikeButton) v.findViewById(R.id.like);

            } else {
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(Log_tag, "Clicked + icon " + "Home view");
                        SelectDialog dia = new SelectDialog();
                        dia.show(getSupportFragmentManager(), "abc");
                        //goToAddProduct();
                    }
                });
            }

            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element

            if (position < mDataset.size()) {

                Card card = mDataset.get(position);
                int viewType = card.getType();

                if (viewType == Card.TYPE_TICKET) {
                    TicketCard tc = (TicketCard) card;
                    holder.ticketTitle.setText(tc.title);
                    holder.ticketStatus.setText(tc.status.substring(2));
                    holder.v.setTag(card);
                    holder.v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            TicketCard card = (TicketCard) v.getTag();
                            Intent i = new Intent(getApplication(), TicketActivity.class);
                            i.putExtra("ticketId", card.id);
                            startActivity(i);
                        }
                    });
                } else if (viewType == Card.TYPE_PRODUCT) {
                    ProductCard pc = (ProductCard) card;


                    if (pc.name != null) {
                        holder.productName.setText(pc.name);
                    }

                    holder.productWarranty.setText(pc.warranty + "days");

                    if (pc.image.isEmpty()) {
                        holder.productImage.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Picasso.get().load(pc.image).into(holder.productImage);
                    }


                    holder.like.setTag(card);
                    holder.v.setTag(card);
                    holder.like.setLiked(pc.liked);
                    holder.like.setOnLikeListener(new OnLikeListener() {
                        @Override
                        public void liked(LikeButton likeButton) {
                            ProductCard card = (ProductCard) likeButton.getTag();

                            if (AppUtils.isNetworkAvailable(MainActivity.this)) {
                                if (AppUtils.isInternetAccessible(MainActivity.this)) {
                                    String url = App.SERVER_URL + "/product/" + card.product_id + "/like";
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    Log.v("0", response);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
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
                                            map.put("token", app.setting.getString("token", ""));
                                            return map;
                                        }

                                    };

                                    queue.add(stringRequest);
                                } else {
                                    AppUtils.showNoInternetAccessibleAlert(MainActivity.this);
                                }
                            } else {
                                AppUtils.showWiFiSettingsAlert(MainActivity.this);
                            }
                        }

                        @Override
                        public void unLiked(LikeButton likeButton) {
                            ProductCard card = (ProductCard) likeButton.getTag();

                            if (AppUtils.isNetworkAvailable(MainActivity.this)) {
                                if (AppUtils.isInternetAccessible(MainActivity.this)) {
                                    String url = App.SERVER_URL + "/product/" + card.product_id + "/unlike";
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {

                                                    Log.v("0", response);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
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
                                            map.put("token", app.setting.getString("token", ""));
                                            return map;
                                        }

                                    };
                                    queue.add(stringRequest);
                                } else {
                                    AppUtils.showNoInternetAccessibleAlert(MainActivity.this);
                                }
                            } else {
                                AppUtils.showWiFiSettingsAlert(MainActivity.this);
                            }

                        }
                    });
                    holder.v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ProductCard card = (ProductCard) v.getTag();
                            Intent i = new Intent(getApplication(), ProductActivity.class);
                            i.putExtra("pid", card.product_id);
                            startActivity(i);
                        }
                    });
                } else {

                }
            }

        }

        @Override
        public int getItemViewType(int position) {
            if (position > mDataset.size() - 1) {
                return Card.NOT_CARD;
            }
            return mDataset.get(position).getType();
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size() + 1;
        }
    }

    public void useNfc(View v) {
        Log.v("0000", "nfc");
    }

    public void useQrCode(View v) {

    }
}
