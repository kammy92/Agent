package com.insideragent.deals.activity;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.insideragent.deals.R;
import com.insideragent.deals.fragment.CompsFragment;
import com.insideragent.deals.fragment.OverviewFragment;
import com.insideragent.deals.utils.AppConfigTags;
import com.insideragent.deals.utils.AppConfigURL;
import com.insideragent.deals.utils.BuyerDetailsPref;
import com.insideragent.deals.utils.Constants;
import com.insideragent.deals.utils.CustomImageSlider;
import com.insideragent.deals.utils.NetworkConnection;
import com.insideragent.deals.utils.PropertyDetailsPref;
import com.insideragent.deals.utils.SetTypeFace;
import com.insideragent.deals.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by l on 22/03/2017.
 */

public class PropertyDetailActivity extends AppCompatActivity {
    List<String> bannerList = new ArrayList<> ();
    List<String> videoList = new ArrayList<> ();
    Toolbar toolbar;
    CoordinatorLayout clMain;
    
    TextView tv4;
    int property_id;
    ProgressDialog progressDialog;
    TextView tvSliderPosition;
    PropertyDetailsPref propertyDetailsPref;
    BuyerDetailsPref buyerDetailsPref;
    RelativeLayout rlBack;
    FloatingActionButton fabMaps;
    RelativeLayout rlMain;
    boolean isFavourite;
    ImageView ivFavourite;
    ViewPagerAdapter viewPagerAdapter;
    //  ImageView ivVideo;
    SharedPreferences.Editor editor;
    SliderLayout slider;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    TextView tvTitle;
    
    AppBarLayout appBar;
    
    setPropertyDetails setPropertyDetail;
    TextView tv1;
    TextView tvViaSMS;
    TextView tvViaEmail;
    String address;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_property_detail);
        initView ();
        initData ();
        initListener ();
        getExtras ();
        getPropertyDetails ();
    }
    
    private void getExtras () {
        Intent intent = getIntent ();
        property_id = intent.getIntExtra (AppConfigTags.PROPERTY_ID, 0);
        
        address = intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS) + " " + intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS2);
        tvTitle.setText (intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS) + "\n" + intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS2));
        
        editor.putInt (PropertyDetailsPref.PROPERTY_ID, intent.getIntExtra (AppConfigTags.PROPERTY_ID, 0));
        editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS1, intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS));
        editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS2, intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS2));
        editor.putString (PropertyDetailsPref.PROPERTY_BEDROOM, intent.getStringExtra (AppConfigTags.PROPERTY_BEDROOMS));
        editor.putString (PropertyDetailsPref.PROPERTY_BATHROOM, intent.getStringExtra (AppConfigTags.PROPERTY_BATHROOMS));
        editor.putString (PropertyDetailsPref.PROPERTY_AREA, intent.getStringExtra (AppConfigTags.PROPERTY_AREA));
        editor.putString (PropertyDetailsPref.PROPERTY_PRICE, intent.getStringExtra (AppConfigTags.PROPERTY_PRICE));
        editor.putString (PropertyDetailsPref.PROPERTY_YEAR_BUILD, intent.getStringExtra (AppConfigTags.PROPERTY_BUILT_YEAR));
        
        editor.putString (PropertyDetailsPref.PROPERTY_STATE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_LATITUDE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_LONGITUDE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_DESCRIPTION, "");
        editor.putString (PropertyDetailsPref.PROPERTY_OVERVIEW, "");
        editor.putString (PropertyDetailsPref.PROPERTY_REALTOR, "");
        editor.putString (PropertyDetailsPref.PROPERTY_COMPS, "");
        editor.putString (PropertyDetailsPref.PROPERTY_KEY_DETAILS, "");
        editor.putString (PropertyDetailsPref.PROPERTY_COMPS_ADDRESSES, "");
        editor.putString (PropertyDetailsPref.PROPERTY_IMAGES, "");
        
        editor.putString (PropertyDetailsPref.PROPERTY_WORK_SCOPE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_FINISHED_PRODUCT, "");
        editor.putString (PropertyDetailsPref.PROPERTY_CLOSING_DETAILS, "");
        
        editor.putInt (PropertyDetailsPref.PROPERTY_AUCTION_ID, 0);
        editor.putInt (PropertyDetailsPref.PROPERTY_AUCTION_STATUS, intent.getIntExtra (AppConfigTags.PROPERTY_STATUS, 0));
        editor.putInt (PropertyDetailsPref.PROPERTY_TOUR_STATUS, 0);
        editor.putInt (PropertyDetailsPref.PROPERTY_IMAGE_COUNT, 0);
        
        
        editor.apply ();
        
        bannerList = intent.getStringArrayListExtra (AppConfigTags.PROPERTY_IMAGES);
        
        try {
            propertyDetailsPref.putIntPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_IMAGE_COUNT, intent.getStringArrayListExtra (AppConfigTags.PROPERTY_IMAGES).size ());
        } catch (Exception e) {
            propertyDetailsPref.putIntPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_IMAGE_COUNT, 0);
        }
        
        initSlider ();
        
        if (intent.getBooleanExtra (AppConfigTags.PROPERTY_IS_FAVOURITE, false)) {
            ivFavourite.setImageResource (R.drawable.ic_heart_filled);
        } else {
            ivFavourite.setImageResource (R.drawable.ic_heart);
        }
        setupViewPager (viewPager);
    }
    
    private void initView () {
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        slider = (SliderLayout) findViewById (R.id.slider);
        ivFavourite = (ImageView) findViewById (R.id.ivFavourite);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById (R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById (R.id.toolbar);
        tabLayout = (TabLayout) findViewById (R.id.tabs);
        viewPager = (ViewPager) findViewById (R.id.viewpager);
        tvSliderPosition = (TextView) findViewById (R.id.tvSliderPosition);
        fabMaps = (FloatingActionButton) findViewById (R.id.fabMap);
        //  ivVideo = (ImageView) findViewById (R.id.ivVideo);
        appBar = (AppBarLayout) findViewById (R.id.appBar);
        tvTitle = (TextView) findViewById (R.id.tvTitle);
        
        tvViaEmail = (TextView) findViewById (R.id.tvViaEmail);
        tvViaSMS = (TextView) findViewById (R.id.tvViaSMS);
        tv1 = (TextView) findViewById (R.id.tv1);
    }
    
    private void initData () {
        setPropertyDetail = new setPropertyDetails ();
        
        propertyDetailsPref = PropertyDetailsPref.getInstance ();
        editor = getSharedPreferences (PropertyDetailsPref.PROPERTY_DETAILS, Context.MODE_PRIVATE).edit ();
        
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        progressDialog = new ProgressDialog (this);
        tabLayout.setupWithViewPager (viewPager);
        tabLayout.setTabGravity (TabLayout.GRAVITY_FILL);
        collapsingToolbarLayout.setTitleEnabled (false);
        
        Utils.setTypefaceToAllViews (this, rlBack);
        
        
        switch (buyerDetailsPref.getStringPref (PropertyDetailActivity.this, buyerDetailsPref.LOGIN_TYPE)) {
            case "BUYER":
                tv1.setText ("CONTACT YOUR AGENT");
                break;
            case "INSIDER":
                tv1.setText ("SHARE WITH YOUR BUYERS");
                break;
        }
        
    }
    
    private void initListener () {
        appBar.addOnOffsetChangedListener (new AppBarLayout.OnOffsetChangedListener () {
            @Override
            public void onOffsetChanged (AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs (verticalOffset) == appBarLayout.getTotalScrollRange ()) {
                    // Collapsed
                    tvTitle.setVisibility (View.VISIBLE);
                    collapsingToolbarLayout.setScrimAnimationDuration ((long) 400);
                    collapsingToolbarLayout.setScrimsShown (true);
//                    collapsingToolbarLayout.setScrimVisibleHeightTrigger ((int) Utils.dpFromPx (PropertyDetailActivity.this, 60));
                    getWindow ().clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    collapsingToolbarLayout.setContentScrimColor (getResources ().getColor (R.color.primary));
                } else if (verticalOffset == 0) {
                    // Expanded
                    tvTitle.setVisibility (View.GONE);
                    collapsingToolbarLayout.setScrimsShown (false);
                    
                    if (Build.VERSION.SDK_INT >= 21) {
                        Window window = getWindow ();
                        getWindow ().getDecorView ().setSystemUiVisibility (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor (Color.TRANSPARENT);
                    } else {
                        getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    }
    
                    collapsingToolbarLayout.setContentScrim (null);
                    collapsingToolbarLayout.setStatusBarScrim (null);
                } else {
                    // transparent statusbar for marshmallow and above
                    tvTitle.setVisibility (View.GONE);
                    collapsingToolbarLayout.setScrimsShown (false);
                    if (Build.VERSION.SDK_INT >= 21) {
                        Window window = getWindow ();
                        getWindow ().getDecorView ().setSystemUiVisibility (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor (Color.TRANSPARENT);
                    } else {
                        getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    }
                    collapsingToolbarLayout.setContentScrim (null);
                    collapsingToolbarLayout.setStatusBarScrim (null);
                }
    
            }
        });
        
        rlBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        
        fabMaps.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                try {
                    Intent intent = new Intent (PropertyDetailActivity.this, PropertyLocationActivity.class);
                    startActivity (intent);
                } catch (Exception e) {
                    e.printStackTrace ();
                    Utils.showToast (PropertyDetailActivity.this, "No Map Details", false);
                }
            }
        });
        
        tvViaEmail.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                switch (buyerDetailsPref.getStringPref (PropertyDetailActivity.this, buyerDetailsPref.LOGIN_TYPE)) {
                    case "BUYER":
                        try {
                            final Intent shareIntent = new Intent (Intent.ACTION_SENDTO, Uri.parse ("mailto:" + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_EMAIL)));
                            shareIntent.putExtra (Intent.EXTRA_SUBJECT, propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS1) + ", " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS2));// property address
                            shareIntent.putExtra (Intent.EXTRA_TEXT, "Hi " + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_NAME) + ",\n\n" +
                                    "I am interested in " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS1) + ", " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS2) + ". Please contact me so that we may discuss this property");

//                            Uri imageUri = Uri.parse ("android.resource://" + getPackageName ()+ "/drawable/" + "no_image_placeholder.png");
//                            shareIntent.putExtra (Intent.EXTRA_STREAM, imageUri);
//                            shareIntent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            shareIntent.setDataAndType (imageUri, "image/png");
                            startActivity (shareIntent);
    
                        } catch (ActivityNotFoundException e) {
                            //TODO smth
                        }
                        break;
                    case "INSIDER":
                        try {
                            final Intent shareIntent = new Intent (Intent.ACTION_SENDTO, Uri.parse ("mailto:"));
                            shareIntent.putExtra (Intent.EXTRA_SUBJECT, propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS1) + ", " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS2));// property address
                            shareIntent.putExtra (Intent.EXTRA_TEXT, "Hello,\n\n" +
                                    "Here's a property I think you'll be interested in. Contact me to view this off market deal.\n\n" +
                                    "Property Address: " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS1) + ", " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS2) + "\n\n" +
                                    "Your Buy Price: " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_PRICE) + "\n" +
                                    "Beds: " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_BEDROOM) + "\n" +
                                    "Baths: " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_BATHROOM) + "\n" +
                                    "Sqft: " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_AREA) + "\n\n" +
                                    "View property details " + "https://www.insideragentdeals.com/property" + "/" + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_SLUG) + "/" + propertyDetailsPref.getIntPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ID) + "\n\n" +
                                    "Thank you, " + "\n" +
                                    buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_NAME) + "\n" +
                                    buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_MOBILE) + "\n\n" +
                                    "Download my Insider Agent Deals app & never miss a deal again!" + "\n" +
                                    "View all of my off market properties as they become available" + "\n\n" +
                                    "Download now https://www.insideragentdeals.com/8cg7il" + "\n" +
                                    "Your Username: " + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_USERNAME) + "\n" +
                                    "Your Password: " + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_PASSWORD));

//                            Uri imageUri = Uri.parse ("android.resource://" + getPackageName ()+ "/drawable/" + "no_image_placeholder.png");
//                            shareIntent.putExtra (Intent.EXTRA_STREAM, imageUri);
//                            shareIntent.addFlags (Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                            shareIntent.setDataAndType (imageUri, "image/png");
                            startActivity (shareIntent);
    
                        } catch (ActivityNotFoundException e) {
                            //TODO smth
                        }
                        break;
                }
            }
        });
        
        tvViaSMS.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                switch (buyerDetailsPref.getStringPref (PropertyDetailActivity.this, buyerDetailsPref.LOGIN_TYPE)) {
                    case "BUYER":
                        try {
                            Uri uri = Uri.parse ("smsto:" + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_MOBILE));
                            Intent intent = new Intent (Intent.ACTION_SENDTO, uri);
                            intent.putExtra ("sms_body", "Hello," + "\n" +
                                    "I am interested in " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS1) + ", " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS2) + ". Please contact me so that we may discuss this property");
                            startActivity (intent);
                        } catch (Exception e) {
                            Toast.makeText (getApplicationContext (), "SMS failed, please try again later!", Toast.LENGTH_LONG).show ();
                            e.printStackTrace ();
                        }
                        break;
                    case "INSIDER":
                        try {
                            Uri uri = Uri.parse ("smsto:");
                            Intent intent = new Intent (Intent.ACTION_SENDTO, uri);
                            intent.putExtra ("sms_body", "Here's a property I think you'll be interested in. Contact me to view this off market deal.\n" +
                                    propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS1) + ", " + propertyDetailsPref.getStringPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ADDRESS2) + "\n" +
                                    "View property details: https://www.insideragentdeals.com/property" + "/" + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_SLUG) + "/" + propertyDetailsPref.getIntPref (PropertyDetailActivity.this, PropertyDetailsPref.PROPERTY_ID) + "\n\n" +
                                    "- " + buyerDetailsPref.getStringPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_NAME) + "");
                            startActivity (intent);
                        } catch (Exception e) {
                            Toast.makeText (getApplicationContext (), "SMS failed, please try again later!", Toast.LENGTH_LONG).show ();
                            e.printStackTrace ();
                        }
                        break;
                }
            }
        });

    
     /*   ivVideo.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse (videoList.get (0))));
            }
        });*/
    }
    
    private void initSlider () {
        slider.removeAllSliders ();
        try {
            for (int i = 0; i < bannerList.size (); i++) {
                final int k = i;
                slider.addSlider (
                        new CustomImageSlider (PropertyDetailActivity.this)
                                .image (bannerList.get (i))
                                .setScaleType (BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener (new BaseSliderView.OnSliderClickListener () {
                                    @Override
                                    public void onSliderClick (BaseSliderView slider) {
                                        Intent intent = new Intent (PropertyDetailActivity.this, PropertyImageActivity.class);
                                        intent.putExtra ("position", k);
                                        startActivity (intent);
                                    }
                                }));
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }
        
        slider.setIndicatorVisibility (PagerIndicator.IndicatorVisibility.Invisible);
        slider.getPagerIndicator ().setVisibility (View.INVISIBLE);
        slider.setPresetTransformer (SliderLayout.Transformer.Default);
        slider.setCustomAnimation (new DescriptionAnimation ());
        slider.addOnPageChangeListener (new ViewPagerEx.OnPageChangeListener () {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
//                tvSliderPosition.setText (position  + bannerList.size ());
            }
            
            @Override
            public void onPageSelected (int position) {
                if (position == 0) {
                    tvSliderPosition.setText (0 + " of " + 0);
                } else {
                    tvSliderPosition.setText ((position + 1) + " of " + bannerList.size ());
                    tvSliderPosition.setVisibility (View.VISIBLE);
                }
            }
            
            @Override
            public void onPageScrollStateChanged (int state) {
//                final Handler handler = new Handler ();
//                Runnable finalizer = null;
//                switch (state) {
//                    case 0:
//                        finalizer = new Runnable () {
//                            public void run () {
//                                rlSliderIndicator.setVisibility (View.GONE);
//                            }
//                        };
//                        handler.postDelayed (finalizer, 1500);
//                        break;
//                    case 1:
//                        handler.removeCallbacks (finalizer);
//                        rlSliderIndicator.setVisibility (View.VISIBLE);
//                        break;
//                    case 2:
//                        break;
//                }
            }
        });
    }
    
    private void getPropertyDetails () {
        if (NetworkConnection.isNetworkAvailable (PropertyDetailActivity.this)) {
            OverviewFragment.hideProgressBar (false);
//            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_PROPERTY_DETAIL, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_PROPERTY_DETAIL,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        setPropertyDetail.execute (jsonObj.toString ());
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    OverviewFragment.hideProgressBar (true);
                                    clMain.setVisibility (View.VISIBLE);
                                    Utils.showSnackBar (PropertyDetailActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_INDEFINITE, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                        @Override
                                        public void onClick (View v) {
                                            getPropertyDetails ();
                                        }
                                    });
                                    e.printStackTrace ();
                                }
                            } else {
                                OverviewFragment.hideProgressBar (true);
                                clMain.setVisibility (View.VISIBLE);
                                Utils.showSnackBar (PropertyDetailActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_INDEFINITE, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                    @Override
                                    public void onClick (View v) {
                                        getPropertyDetails ();
                                    }
                                });
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            clMain.setVisibility (View.VISIBLE);
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            clMain.setVisibility (View.VISIBLE);
                            OverviewFragment.hideProgressBar (true);
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (PropertyDetailActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_INDEFINITE, getResources ().getString (R.string.snackbar_action_retry), new View.OnClickListener () {
                                @Override
                                public void onClick (View v) {
                                    getPropertyDetails ();
                                }
                            });
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.PROPERTY_ID, String.valueOf (property_id));
                    params.put (AppConfigTags.BUYER_ID, String.valueOf (buyerDetailsPref.getIntPref (PropertyDetailActivity.this, BuyerDetailsPref.INSIDER_ID)));
                    params.put (AppConfigTags.TYPE, "property_detail_insider");
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    params.put (AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest (strRequest1, 10);
        } else {
//            clMain.setVisibility (View.VISIBLE);
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }
    }
    
    private void setupViewPager (ViewPager viewPager) {
        viewPagerAdapter = new ViewPagerAdapter (getSupportFragmentManager ());
        viewPagerAdapter.addFragment (new OverviewFragment (), "OVERVIEW");
        viewPagerAdapter.addFragment (new CompsFragment (), "COMPS");
        viewPager.setAdapter (viewPagerAdapter);
        
        for (int i = 0; i < tabLayout.getTabCount (); i++) {
            TextView tv = new TextView (this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams (params);
            tv.setGravity (Gravity.CENTER);
            switch (i) {
                case 0:
                    tv.setText ("OVERVIEW");
                    break;
                case 1:
                    tv.setText ("COMPS");
                    break;
            }
            tv.setTypeface (SetTypeFace.getTypeface (this), Typeface.BOLD);
            tabLayout.getTabAt (i).setCustomView (tv);
        }
    }
    
    @Override
    public void onDestroy () {
        super.onDestroy ();
        setPropertyDetail.cancel (true);
        editor.putInt (PropertyDetailsPref.PROPERTY_ID, 0);
        editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS1, "");
        editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS2, "");
        editor.putString (PropertyDetailsPref.PROPERTY_BEDROOM, "");
        editor.putString (PropertyDetailsPref.PROPERTY_BATHROOM, "");
        editor.putString (PropertyDetailsPref.PROPERTY_AREA, "");
        editor.putString (PropertyDetailsPref.PROPERTY_PRICE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_YEAR_BUILD, "");
        
        editor.putString (PropertyDetailsPref.PROPERTY_STATE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_LATITUDE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_LONGITUDE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_DESCRIPTION, "");
        editor.putString (PropertyDetailsPref.PROPERTY_OVERVIEW, "");
        editor.putString (PropertyDetailsPref.PROPERTY_REALTOR, "");
        editor.putString (PropertyDetailsPref.PROPERTY_COMPS, "");
        editor.putString (PropertyDetailsPref.PROPERTY_KEY_DETAILS, "");
        editor.putString (PropertyDetailsPref.PROPERTY_COMPS_ADDRESSES, "");
        editor.putString (PropertyDetailsPref.PROPERTY_IMAGES, "");
        
        editor.putString (PropertyDetailsPref.PROPERTY_WORK_SCOPE, "");
        editor.putString (PropertyDetailsPref.PROPERTY_FINISHED_PRODUCT, "");
        editor.putString (PropertyDetailsPref.PROPERTY_CLOSING_DETAILS, "");
        
        editor.putInt (PropertyDetailsPref.PROPERTY_AUCTION_ID, 0);
        editor.putInt (PropertyDetailsPref.PROPERTY_AUCTION_STATUS, 0);
        editor.putInt (PropertyDetailsPref.PROPERTY_TOUR_STATUS, 0);
        editor.putInt (PropertyDetailsPref.PROPERTY_IMAGE_COUNT, 0);
        
        editor.apply ();
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<> ();
        private final List<String> mFragmentTitleList = new ArrayList<> ();
        
        public ViewPagerAdapter (FragmentManager manager) {
            super (manager);
        }
        
        @Override
        public Fragment getItem (int position) {
            return mFragmentList.get (position);
        }
        
        @Override
        public int getCount () {
            return mFragmentList.size ();
        }
        
        public void addFragment (Fragment fragment, String title) {
            mFragmentList.add (fragment);
            mFragmentTitleList.add (title);
        }
        
        @Override
        public CharSequence getPageTitle (int position) {
            return mFragmentTitleList.get (position);
        }
    }
    
    private class setPropertyDetails extends AsyncTask<String, Void, String> {
        JSONArray jsonArrayPropertyImages;
        
        @Override
        protected String doInBackground (String... params) {
            try {
                JSONObject jsonObj = new JSONObject (params[0]);
                jsonArrayPropertyImages = jsonObj.getJSONArray (AppConfigTags.PROPERTY_IMAGES);
                editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS1, jsonObj.getString (AppConfigTags.PROPERTY_ADDRESS));
                editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS2, jsonObj.getString (AppConfigTags.PROPERTY_ADDRESS2));
                editor.putString (PropertyDetailsPref.PROPERTY_ADDRESS_FULL, jsonObj.getString (AppConfigTags.PROPERTY_ADDRESS_FULL));
                editor.putString (PropertyDetailsPref.PROPERTY_BEDROOM, jsonObj.getString (AppConfigTags.PROPERTY_BEDROOMS));
                editor.putString (PropertyDetailsPref.PROPERTY_BATHROOM, jsonObj.getString (AppConfigTags.PROPERTY_BATHROOMS));
                editor.putString (PropertyDetailsPref.PROPERTY_AREA, jsonObj.getString (AppConfigTags.PROPERTY_AREA));
                editor.putString (PropertyDetailsPref.PROPERTY_PRICE, jsonObj.getString (AppConfigTags.PROPERTY_PRICE));
                editor.putString (PropertyDetailsPref.PROPERTY_YEAR_BUILD, jsonObj.getString (AppConfigTags.PROPERTY_BUILT_YEAR));
    
                editor.putString (PropertyDetailsPref.PROPERTY_STATE, jsonObj.getString (AppConfigTags.PROPERTY_STATE));
                editor.putString (PropertyDetailsPref.PROPERTY_LATITUDE, jsonObj.getString (AppConfigTags.LATITUDE));
                editor.putString (PropertyDetailsPref.PROPERTY_LONGITUDE, jsonObj.getString (AppConfigTags.LONGITUDE));
                editor.putString (PropertyDetailsPref.PROPERTY_DESCRIPTION, jsonObj.getString (AppConfigTags.PROPERTY_ARV));
                editor.putString (PropertyDetailsPref.PROPERTY_OVERVIEW, jsonObj.getString (AppConfigTags.PROPERTY_OVERVIEW));
                editor.putString (PropertyDetailsPref.PROPERTY_REALTOR, jsonObj.getString (AppConfigTags.PROPERTY_REALTOR));
                editor.putString (PropertyDetailsPref.PROPERTY_COMPS, jsonObj.getString (AppConfigTags.PROPERTY_COMPS));
                editor.putString (PropertyDetailsPref.PROPERTY_KEY_DETAILS, jsonObj.getString (AppConfigTags.PROPERTY_KEY_DETAILS));
                editor.putString (PropertyDetailsPref.PROPERTY_COMPS_ADDRESSES, jsonObj.getJSONArray (AppConfigTags.PROPERTY_COMPS_ADDRESSES).toString ());
                editor.putString (PropertyDetailsPref.PROPERTY_IMAGES, jsonArrayPropertyImages.toString ());
    
                editor.putString (PropertyDetailsPref.PROPERTY_WORK_SCOPE, jsonObj.getString (AppConfigTags.PROPERTY_WORK_SCOPE));
                editor.putString (PropertyDetailsPref.PROPERTY_FINISHED_PRODUCT, jsonObj.getString (AppConfigTags.PROPERTY_FINISHED_PRODUCT));
                editor.putString (PropertyDetailsPref.PROPERTY_CLOSING_DETAILS, jsonObj.getString (AppConfigTags.PROPERTY_CLOSING_DETAILS));
    
                editor.putInt (PropertyDetailsPref.PROPERTY_AUCTION_ID, jsonObj.getInt (AppConfigTags.PROPERTY_BID_AUCTION_ID));
                editor.putInt (PropertyDetailsPref.PROPERTY_AUCTION_STATUS, jsonObj.getInt (AppConfigTags.PROPERTY_BID_AUCTION_STATUS));
                editor.putInt (PropertyDetailsPref.PROPERTY_TOUR_STATUS, jsonObj.getInt (AppConfigTags.PROPERTY_TOUR_STATUS));
                editor.putInt (PropertyDetailsPref.PROPERTY_IMAGE_COUNT, jsonArrayPropertyImages.length ());
    
                editor.apply ();
    
                if (jsonObj.getBoolean (AppConfigTags.PROPERTY_IS_FAVOURITE)) {
                    runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            ivFavourite.setImageResource (R.drawable.ic_heart_filled);
                        }
                    });
                } else {
                    runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            ivFavourite.setImageResource (R.drawable.ic_heart);
                        }
                    });
                }
    
               /* for (int i = 0; i < jsonObj.getJSONArray (AppConfigTags.PROPERTY_VIDEOS).length (); i++) {
                    videoList.add (jsonObj.getJSONArray (AppConfigTags.PROPERTY_VIDEOS).getJSONObject (i).getString (AppConfigTags.VIDEO));
                    runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            ivVideo.setVisibility (View.VISIBLE);
                        }
                    });
                }*/
    
            } catch (Exception e) {
                e.printStackTrace ();
            }
            return "executed";
        }
        
        @Override
        protected void onPostExecute (String result) {
            clMain.setVisibility (View.VISIBLE);
            progressDialog.dismiss ();
            setupViewPager (viewPager);
            final Handler handler = new Handler ();
            handler.postDelayed (new Runnable () {
                @Override
                public void run () {
                    new setPropertyImages ().execute (jsonArrayPropertyImages.toString ());
                }
            }, 1000);
        }
        
        @Override
        protected void onPreExecute () {
        }
        
        @Override
        protected void onProgressUpdate (Void... values) {
        }
    }
    
    private class setPropertyImages extends AsyncTask<String, Void, String> {
        int length;
        
        @Override
        protected String doInBackground (String... params) {
            try {
                JSONArray jsonArray = new JSONArray (params[0]);
                length = jsonArray.length ();
                runOnUiThread (new Runnable () {
                    @Override
                    public void run () {
                        slider.removeAllSliders ();
                        tvSliderPosition.setVisibility (View.GONE);
//                        tvSliderPosition.setText ( + " of " + length);
                    }
                });
//                try {
//                    for (int i = 0; i < bannerList.size (); i++) {
//                        final int k = i;
//                        slider.addSlider (
//                                new CustomImageSlider (PropertyDetailActivity.this)
//                                        .image (bannerList.get (k))
//                                        .setScaleType (BaseSliderView.ScaleType.CenterCrop)
//                                        .setOnSliderClickListener (new BaseSliderView.OnSliderClickListener () {
//                                            @Override
//                                            public void onSliderClick (BaseSliderView slider) {
//                                                Intent intent = new Intent (PropertyDetailActivity.this, PropertyImageActivity.class);
//                                                intent.putExtra ("position", k);
//                                                startActivity (intent);
//                                            }
//                                        }));
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace ();
//                }
//
//                bannerList.clear ();
                for (int j = 0; j < length; j++) {
                    final int k = j;
                    final CustomImageSlider customImageSlider = new CustomImageSlider (PropertyDetailActivity.this);
                    customImageSlider
                            .image (jsonArray.getJSONObject (j).getString (AppConfigTags.PROPERTY_IMAGE))
                            .setScaleType (BaseSliderView.ScaleType.CenterCrop)
                            .setOnSliderClickListener (new BaseSliderView.OnSliderClickListener () {
                                @Override
                                public void onSliderClick (BaseSliderView slider) {
                                    Intent intent = new Intent (PropertyDetailActivity.this, PropertyImageActivity.class);
                                    intent.putExtra ("position", k);
                                    startActivity (intent);
                                }
                            });
    
                    runOnUiThread (new Runnable () {
                        @Override
                        public void run () {
                            slider.addSlider (customImageSlider);
                            tvSliderPosition.setVisibility (View.GONE);
                        }
                    });
//                    bannerList.add (jsonArray.getJSONObject (j).getString (AppConfigTags.PROPERTY_IMAGE));
                }
            } catch (Exception e) {
                e.printStackTrace ();
            }
            return "Executed";
        }
        
        @Override
        protected void onPostExecute (String result) {
            slider.addOnPageChangeListener (new ViewPagerEx.OnPageChangeListener () {
                @Override
                public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
                }
                
                @Override
                public void onPageSelected (int position) {
                    tvSliderPosition.setText ((position + 1) + " of " + length);
                    tvSliderPosition.setVisibility (View.VISIBLE);
                }
                
                @Override
                public void onPageScrollStateChanged (int state) {
                }
            });
        }
        
        @Override
        protected void onPreExecute () {
        }
        
        @Override
        protected void onProgressUpdate (Void... values) {
        }
    }
}