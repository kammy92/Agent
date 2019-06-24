package com.insideragent.deals.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.insideragent.deals.R;
import com.insideragent.deals.adapter.PropertyAdapter;
import com.insideragent.deals.model.Property;
import com.insideragent.deals.utils.AppConfigTags;
import com.insideragent.deals.utils.AppConfigURL;
import com.insideragent.deals.utils.BuyerDetailsPref;
import com.insideragent.deals.utils.Constants;
import com.insideragent.deals.utils.FilterDetailsPref;
import com.insideragent.deals.utils.GPSTracker;
import com.insideragent.deals.utils.NetworkConnection;
import com.insideragent.deals.utils.SetTypeFace;
import com.insideragent.deals.utils.Utils;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static int PERMISSION_REQUEST_CODE = 11;
    final int CURRENT_LOCATION_REQUEST_CODE = 1;
    
    Double currentLatitude = 0.0;
    Double currentLongitude = 0.0;
    
    Bundle savedInstanceState;
    Toolbar toolbar;
    RecyclerView rvPropertyList;
    SwipeRefreshLayout swipeRefreshLayout;
    PropertyAdapter propertyAdapter;
    List<Property> propertyList = new ArrayList<> ();
    BuyerDetailsPref buyerDetailsPref;
    FilterDetailsPref filterDetailsPref;
    CoordinatorLayout clMain;
    ImageView ivFilter;
    ImageView ivMaps;
    ImageView ivOverflow;
    Menu menu2;
    ImageView ivNavigation;
    RelativeLayout rlList;
    RelativeLayout rlInternetConnection;
    RelativeLayout rlNoResultFound;
    TextView tvRetry;
    TextView tvResetFilter;
    private AccountHeader headerResult = null;
    private Drawer result = null;
    String currentDay;
    ImageView ivImage;
    TextView tvTitle;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        initView ();
        initData ();
        initListener ();
        initDrawer ();
        isLogin ();
//        getAllProperties ();
        //   initApplication();
        checkPermissions ();
        this.savedInstanceState = savedInstanceState;
    }
    
    private void isLogin () {
        if (buyerDetailsPref.getIntPref (MainActivity.this, BuyerDetailsPref.INSIDER_ID) == 0) {
            Intent myIntent = new Intent (this, LoginActivity.class);
            startActivity (myIntent);
        }
        
        
        if (buyerDetailsPref.getIntPref (MainActivity.this, BuyerDetailsPref.INSIDER_ID) == 0)
            finish ();
    }
    
    private void initView () {
        toolbar = (Toolbar) findViewById (R.id.toolbar);
        rvPropertyList = (RecyclerView) findViewById (R.id.rvPropertyList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById (R.id.swipe_refresh_layout);
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        ivNavigation = (ImageView) findViewById (R.id.ivNavigation);
        rlInternetConnection = (RelativeLayout) findViewById (R.id.rlInternetConnection);
        rlNoResultFound = (RelativeLayout) findViewById (R.id.rlNoResultFound);
        rlList = (RelativeLayout) findViewById (R.id.rlList);
        tvRetry = (TextView) findViewById (R.id.tvRetry);
        tvResetFilter = (TextView) findViewById (R.id.tvResetFilter);
        ivImage = (ImageView) findViewById (R.id.ivImage);
        tvTitle = (TextView) findViewById (R.id.tvTitle);
        
    }
    
    private void initData () {
        
        
        currentDay = new SimpleDateFormat ("dd/MM/yyyy", Locale.getDefault ()).format (Calendar.getInstance ().getTime ());
        
        //SHA1: 4F:9C:6B:A9:F0:55:8C:73:6F:7C:32:49:FF:3E:0D:9E:34:8D:FA:30
//        byte[] sha1 = {
//                0x4F, (byte) 0x9C, 0x6B, (byte) 0xA9, (byte) 0xF0, 0x55, (byte) 0x8C, 0x73, 0x6F, 0x7C, 0x32, 0x49, (byte) 0xFF, 0x3E, 0x0D, (byte) 0x9E, 0x34, (byte) 0x8D, (byte) 0xFA, 0x30
//        };
//        Log.e ("keyhash", Base64.encodeToString (sha1, Base64.NO_WRAP));


//        if (LeakCanary.isInAnalyzerProcess (this)) {
        // This process is dedicated to LeakCanary for heap analysis.
        // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install (getApplication ());
        
        
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        filterDetailsPref = FilterDetailsPref.getInstance ();
        swipeRefreshLayout.setRefreshing (true);
        propertyList.clear ();
        propertyAdapter = new PropertyAdapter (MainActivity.this, propertyList);
        rvPropertyList.setAdapter (propertyAdapter);
        rvPropertyList.setHasFixedSize (true);
        rvPropertyList.setLayoutManager (new LinearLayoutManager (this, LinearLayoutManager.VERTICAL, false));
        rvPropertyList.setItemAnimator (new DefaultItemAnimator ());
        Utils.setTypefaceToAllViews (this, clMain);
        
        tvTitle.setText (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_NAME).toUpperCase ());
        
        
        if (! buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_LOGO).equalsIgnoreCase ("")) {
            ivImage.setVisibility (View.VISIBLE);
            String urlStr = buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_LOGO);
            URL url = null;
            try {
                url = new URL (urlStr);
                URI uri = new URI (url.getProtocol (), url.getUserInfo (), url.getHost (), url.getPort (), url.getPath (), url.getQuery (), url.getRef ());
                url = uri.toURL ();
                Log.d ("image url", String.valueOf (url));
                Glide.with (MainActivity.this)
                        .load ("" + url)
                        .into (ivImage);
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (URISyntaxException e) {
                e.printStackTrace ();
            }
        } else {
            ivImage.setVisibility (View.GONE);
        }
        
        
    }
    
    private void initListener () {
        swipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh () {
                getAllProperties ();
            }
        });
        ivNavigation.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                result.openDrawer ();
            }
        });
        
        
        tvRetry.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                rlList.setVisibility (View.VISIBLE);
                rlInternetConnection.setVisibility (View.GONE);
                rlNoResultFound.setVisibility (View.GONE);
                swipeRefreshLayout.setRefreshing (true);
                getAllProperties ();
            }
        });
        
    }
    
    private void initLocation (final int request_code) {
        GoogleApiClient googleApiClient;
        googleApiClient = new GoogleApiClient.Builder (this)
                .addApi (LocationServices.API)
                .addConnectionCallbacks (new GoogleApiClient.ConnectionCallbacks () {
                    @Override
                    public void onConnected (@Nullable Bundle bundle) {
                    }
                    
                    @Override
                    public void onConnectionSuspended (int i) {
                    }
                })
                .addOnConnectionFailedListener (new GoogleApiClient.OnConnectionFailedListener () {
                    @Override
                    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
                    }
                }).build ();
        googleApiClient.connect ();
        
        LocationRequest locationRequest2 = LocationRequest.create ();
        locationRequest2.setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest2.setInterval (30 * 1000);
        locationRequest2.setFastestInterval (5 * 1000);
        LocationSettingsRequest.Builder builder2 = new LocationSettingsRequest.Builder ().addLocationRequest (locationRequest2);
        builder2.setAlwaysShow (true); //this is the key ingredient
        
        PendingResult<LocationSettingsResult> result2 =
                LocationServices.SettingsApi.checkLocationSettings (googleApiClient, builder2.build ());
        result2.setResultCallback (new ResultCallback<LocationSettingsResult> () {
            @Override
            public void onResult (LocationSettingsResult result) {
                final Status status = result.getStatus ();
                final LocationSettingsStates state = result.getLocationSettingsStates ();
                switch (status.getStatusCode ()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location requests here.
                        switch (request_code) {
                            case CURRENT_LOCATION_REQUEST_CODE:
                                GPSTracker gps = new GPSTracker (MainActivity.this);
                                if (gps.canGetLocation ()) {
                                    currentLatitude = gps.getLatitude ();
                                    currentLongitude = gps.getLongitude ();
                                    Log.e ("latitude", String.valueOf (currentLatitude));
                                    Log.e ("currentLongitude", String.valueOf (currentLongitude));
                                }
                                break;
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user a dialog.
                        try {
                            status.startResolutionForResult (MainActivity.this, request_code);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    
    public void checkPermissions () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions (new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MainActivity.PERMISSION_REQUEST_CODE);
            }
        }
    }
    
    @Override
    @TargetApi(23)
    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult (requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    boolean showRationale = shouldShowRequestPermissionRationale (permission);
                    if (! showRationale) {
                        AlertDialog.Builder builder = new AlertDialog.Builder (MainActivity.this);
                        builder.setMessage ("Permission are required please enable them on the App Setting page")
                                .setCancelable (false)
                                .setPositiveButton ("OK", new DialogInterface.OnClickListener () {
                                    public void onClick (DialogInterface dialog, int id) {
                                        dialog.dismiss ();
                                        Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts ("package", getPackageName (), null));
                                        startActivity (intent);
                                    }
                                });
                        AlertDialog alert = builder.create ();
                        alert.show ();
                    } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals (permission)) {
                    } else if (Manifest.permission.ACCESS_COARSE_LOCATION.equals (permission)) {
                    }
                }
            }
        }
        
        
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        }
    }
    
    private void getAllProperties () {
        if (NetworkConnection.isNetworkAvailable (MainActivity.this)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_PROPERTY_LIST, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_PROPERTY_LIST,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            propertyList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    rlInternetConnection.setVisibility (View.GONE);
                                    rlList.setVisibility (View.VISIBLE);
                                    rlNoResultFound.setVisibility (View.GONE);
    
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.ABOUT_US, jsonObj.getString (AppConfigTags.ABOUT_US));
    
                                        JSONArray jsonArrayProperty = jsonObj.getJSONArray (AppConfigTags.PROPERTIES);
                                        for (int i = 0; i < jsonArrayProperty.length (); i++) {
                                            JSONObject jsonObjectProperty = jsonArrayProperty.getJSONObject (i);
                                            Property property = new Property (
                                                    jsonObjectProperty.getInt (AppConfigTags.PROPERTY_ID),
                                                    jsonObjectProperty.getInt (AppConfigTags.PROPERTY_STATUS),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_PRICE),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_BEDROOMS),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_BATHROOMS),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_AREA),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_BUILT_YEAR),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_ADDRESS),
                                                    jsonObjectProperty.getString (AppConfigTags.PROPERTY_CITY),
                                                    jsonObjectProperty.getBoolean (AppConfigTags.PROPERTY_IS_OFFER), false);
    
                                            JSONArray jsonArrayPropertyImages = jsonObjectProperty.getJSONArray (AppConfigTags.PROPERTY_IMAGES);
                                            ArrayList<String> propertyImages = new ArrayList<> ();
    
                                            for (int j = 0; j < jsonArrayPropertyImages.length (); j++) {
                                                JSONObject jsonObjectImages = jsonArrayPropertyImages.getJSONObject (j);
                                                propertyImages.add (jsonObjectImages.getString (AppConfigTags.PROPERTY_IMAGE));
                                                property.setImageList (propertyImages);
                                            }
                                            propertyList.add (i, property);
                                        }
    
                                        propertyAdapter.notifyDataSetChanged ();
    
                                        if (jsonArrayProperty.length () > 0) {
                                            if (filterDetailsPref.getBooleanPref (MainActivity.this, FilterDetailsPref.FILTER_APPLIED))
                                                rlNoResultFound.setVisibility (View.GONE);
                                            swipeRefreshLayout.setRefreshing (false);
                                        }
                                    } else {
    
    
                                        Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        if (message.equalsIgnoreCase ("no property available"))
                                            rlNoResultFound.setVisibility (View.VISIBLE);
                                        else
                                            rlNoResultFound.setVisibility (View.GONE);
                                    }
                                } catch (Exception e) {
                                    Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            swipeRefreshLayout.setRefreshing (false);
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            swipeRefreshLayout.setRefreshing (false);
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (MainActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.TYPE, "property_list_insider");
                    params.put (AppConfigTags.DEVICE_TYPE, "ANDROID");
                    params.put (AppConfigTags.INSIDER_ID, String.valueOf (buyerDetailsPref.getIntPref (MainActivity.this, BuyerDetailsPref.INSIDER_ID)));
                    params.put (AppConfigTags.LOGIN_TYPE, buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.LOGIN_TYPE));
    
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
            Utils.sendRequest (strRequest1, 60);
        } else {
            swipeRefreshLayout.setRefreshing (false);
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
            rlInternetConnection.setVisibility (View.VISIBLE);
            rlList.setVisibility (View.GONE);
            rlNoResultFound.setVisibility (View.GONE);
        }
    }
    
    private void initDrawer () {
        IProfile profile = new IProfile () {
            @Override
            public Object withName (String name) {
                return null;
            }
            
            @Override
            public StringHolder getName () {
                return null;
            }
            
            @Override
            public Object withEmail (String email) {
                return null;
            }
            
            @Override
            public StringHolder getEmail () {
                return null;
            }
            
            @Override
            public Object withIcon (Drawable icon) {
                return null;
            }
            
            @Override
            public Object withIcon (Bitmap bitmap) {
                return null;
            }
            
            @Override
            public Object withIcon (@DrawableRes int iconRes) {
                return null;
            }
            
            @Override
            public Object withIcon (String url) {
                return null;
            }
            
            @Override
            public Object withIcon (Uri uri) {
                return null;
            }
            
            @Override
            public Object withIcon (IIcon icon) {
                return null;
            }
            
            @Override
            public ImageHolder getIcon () {
                return null;
            }
            
            @Override
            public Object withSelectable (boolean selectable) {
                return null;
            }
            
            @Override
            public boolean isSelectable () {
                return false;
            }
            
            @Override
            public Object withIdentifier (long identifier) {
                return null;
            }
            
            @Override
            public long getIdentifier () {
                return 0;
            }
        };
        
        DrawerImageLoader.init (new AbstractDrawerImageLoader () {
            @Override
            public void set (ImageView imageView, Uri uri, Drawable placeholder) {
                if (uri != null) {
                    Glide.with (imageView.getContext ()).load (uri).placeholder (placeholder).into (imageView);
                }
            }
            
            @Override
            public void cancel (ImageView imageView) {
                Glide.clear (imageView);
            }
            
            @Override
            public Drawable placeholder (Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name ().equals (tag)) {
                    return DrawerUIUtils.getPlaceHolder (ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name ().equals (tag)) {
                    return new IconicsDrawable (ctx).iconText (" ").backgroundColorRes (com.mikepenz.materialdrawer.R.color.colorPrimary).sizeDp (56);
                } else if ("customUrlItem".equals (tag)) {
                    return new IconicsDrawable (ctx).iconText (" ").backgroundColorRes (R.color.md_white_1000);
                }
                
                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                
                return super.placeholder (ctx, tag);
            }
        });
        
        
        /*
        Log.e ("image drawer", buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PHOTO));
        if (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PHOTO).length () != 0) {
            
            String urlStr = buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PHOTO);
            URL url = null;
            try {
                url = new URL (urlStr);
                URI uri = new URI (url.getProtocol (), url.getUserInfo (), url.getHost (), url.getPort (), url.getPath (), url.getQuery (), url.getRef ());
                url = uri.toURL ();
                Log.d ("image phote", String.valueOf (url));
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (URISyntaxException e) {
                e.printStackTrace ();
            }
            
            
            headerResult = new AccountHeaderBuilder ()
                    .withActivity (this)
                    .withCompactStyle (false)
                    .withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                    .withTypeface (SetTypeFace.getTypeface (this))
                    .withPaddingBelowHeader (false)
                    .withSelectionListEnabled (false)
                    .withSelectionListEnabledForSingleProfile (false)
                    .withProfileImagesVisible (false)
                    .withOnlyMainProfileImageVisible (true)
                    .withDividerBelowHeader (true)
                    .withHeaderBackground (R.drawable.drawer_bg)
                    .withSavedInstance (savedInstanceState)
                    .build ();
            headerResult.addProfiles (new ProfileDrawerItem ()
                    .withIcon (url.toString ())
                    .withName (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.BUYER_NAME))
                    .withEmail (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.BUYER_EMAIL)));
            
        } else {
            headerResult = new AccountHeaderBuilder ()
                    .withActivity (this)
                    .withCompactStyle (false)
                    .withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                    .withTypeface (SetTypeFace.getTypeface (this))
                    .withPaddingBelowHeader (false)
                    .withSelectionListEnabled (false)
                    .withSelectionListEnabledForSingleProfile (false)
                    .withProfileImagesVisible (false)
                    .withOnlyMainProfileImageVisible (false)
                    .withDividerBelowHeader (true)
                    .withHeaderBackground (R.drawable.drawer_bg)
                    .withSavedInstance (savedInstanceState)
                    .build ();
            headerResult.addProfiles (new ProfileDrawerItem ()
                    .withName (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.BUYER_NAME))
                    .withEmail (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.BUYER_EMAIL)));
        }
        */
        
        if (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PHOTO).length () != 0) {
            String urlStr = buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PHOTO);
            URL url = null;
            try {
                url = new URL (urlStr);
                URI uri = new URI (url.getProtocol (), url.getUserInfo (), url.getHost (), url.getPort (), url.getPath (), url.getQuery (), url.getRef ());
                url = uri.toURL ();
                Log.d ("image phote", String.valueOf (url));
            } catch (MalformedURLException e) {
                e.printStackTrace ();
            } catch (URISyntaxException e) {
                e.printStackTrace ();
            }
            
            headerResult = new AccountHeaderBuilder ()
                    .withActivity (this)
                    .withCompactStyle (false)
                    .withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                    .withTypeface (SetTypeFace.getTypeface (this))
                    .withPaddingBelowHeader (false)
                    .withSelectionListEnabled (false)
                    .withSelectionListEnabledForSingleProfile (false)
                    .withProfileImagesVisible (false)
                    .withOnlyMainProfileImageVisible (true)
                    .withDividerBelowHeader (true)
                    .withHeaderBackground (R.drawable.drawer_bg)
                    .withSavedInstance (savedInstanceState)
                    .withOnAccountHeaderListener (new AccountHeader.OnAccountHeaderListener () {
                        @Override
                        public boolean onProfileChanged (View view, IProfile profile, boolean currentProfile) {
                            // Intent intent = new Intent (MainActivity.this, MyProfileActivity.class);
                            //startActivity (intent);
                            return false;
                        }
                    })
                    .build ();
            headerResult.addProfiles (new ProfileDrawerItem ()
                    .withIcon ("" + url)
                    .withName (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_NAME))
                    .withEmail (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_EMAIL)));
        } else {
            headerResult = new AccountHeaderBuilder ()
                    .withActivity (this)
                    .withCompactStyle (false)
                    .withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                    .withTypeface (SetTypeFace.getTypeface (this))
                    .withPaddingBelowHeader (false)
                    .withSelectionListEnabled (false)
                    .withSelectionListEnabledForSingleProfile (false)
                    .withProfileImagesVisible (true)
                    .withOnlyMainProfileImageVisible (true)
                    .withDividerBelowHeader (true)
                    .withHeaderBackground (R.drawable.drawer_bg)
                    .withSavedInstance (savedInstanceState)
                    .withOnAccountHeaderListener (new AccountHeader.OnAccountHeaderListener () {
                        @Override
                        public boolean onProfileChanged (View view, IProfile profile, boolean currentProfile) {
                            
                            return false;
                        }
                    })
                    .build ();
            headerResult.addProfiles (new ProfileDrawerItem ()
                    .withName (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_NAME))
                    .withEmail (buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_EMAIL)));
        }
        
        
        DrawerBuilder drawerBuilder = new DrawerBuilder ()
                .withActivity (this)
                .withAccountHeader (headerResult)
                .withSavedInstance (savedInstanceState)
                .withOnDrawerItemClickListener (new Drawer.OnDrawerItemClickListener () {
                    @Override
                    public boolean onItemClick (View view, int position, IDrawerItem drawerItem) {
                        switch ((int) drawerItem.getIdentifier ()) {
                            case 2:
                                Intent shareApp = new Intent ();
                                shareApp.setAction (Intent.ACTION_SEND);
                                shareApp.setType ("text/plain");
                                shareApp.putExtra (Intent.EXTRA_TEXT, "Download my Insider Agent Deals app & never miss a deal again.  View all of my off market properties as they become available. Link: https://www.insideragentdeals.com/8cg7il");
                                startActivity (Intent.createChooser (shareApp, "Share"));
                                break;
                            case 3:
                                Intent intent2 = new Intent (MainActivity.this, HowItWorksActivity.class);
                                startActivity (intent2);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 4:
                                Intent intent3 = new Intent (MainActivity.this, AboutUsActivity.class);
                                startActivity (intent3);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 5:
                               /* Intent intent4 = new Intent (MainActivity.this, TestimonialActivity.class);
                                startActivity (intent4);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                break;
                            case 6:
                                Intent intent5 = new Intent (MainActivity.this, ContactUsActivity.class);
                                startActivity (intent5);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 7:
                                Intent intent6 = new Intent (MainActivity.this, FAQActivity.class);
                                startActivity (intent6);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 8:
                               /* Intent intent7 = new Intent (MainActivity.this, MyProfileActivity.class);
                                startActivity (intent7);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                break;
                            case 9:
                                /*Intent intent8 = new Intent (MainActivity.this, ChangePasswordActivity.class);
                                startActivity (intent8);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);*/
                                break;
                            case 10:
                                showLogOutDialog ();
                                break;
                            case 11:
                                Intent intent9 = new Intent (MainActivity.this, MyAppointmentActivity.class);
                                startActivity (intent9);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                            case 12:
                                /*    *//* Intent intent12 = new Intent (MainActivity.this, com.livechatinc.inappchat.ChatWindowActivity.class);
                                intent12.putExtra (com.livechatinc.inappchat.ChatWindowActivity.KEY_GROUP_ID, "0");
                                intent12.putExtra (com.livechatinc.inappchat.ChatWindowActivity.KEY_LICENCE_NUMBER, "9704635");
                                intent12.putExtra (com.livechatinc.inappchat.ChatWindowActivity.KEY_VISITOR_NAME, buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.BUYER_NAME));
                                intent12.putExtra (com.livechatinc.inappchat.ChatWindowActivity.KEY_VISITOR_EMAIL, buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.BUYER_EMAIL));
*//*
                                startActivity (intent12);
*/
                                break;
                            case 13:
    
                                break;
    
                            case 14:
                                try {
                                    Intent intent = new Intent (Intent.ACTION_VIEW, Uri.parse ("mailto:"));
                                    intent.putExtra (Intent.EXTRA_SUBJECT, "Off Market Properties");
                                    intent.putExtra (Intent.EXTRA_TEXT, "Hello,\n\n" +
                                            "I've found some properties I think you'll be interested in. Contact me to discuss these great, off market deals." + "\n\n" +
                                            "Thank you, " + "\n" +
                                            buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_NAME) + "\n" +
                                            buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_MOBILE) + "\n\n" +
                                            "Download my Insider Agent Deals app & never miss a deal again!" + "\n" +
                                            "View all of my off market properties as they become available." + "\n\n" +
                                            "Download now https://www.insideragentdeals.com/8cg7il" + "\n" +
                                            "Your Username: " + buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_USERNAME) + "\n" +
                                            "Your Password: " + buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PASSWORD));
                                    startActivity (intent);
                                } catch (ActivityNotFoundException e) {
                                    //TODO smth
                                }
        
        
                                break;
    
                            case 15:
                               /* Intent shareApp = new Intent ();
                                shareApp.setAction (Intent.ACTION_SEND);
                                shareApp.setType ("text/plain");
                                shareApp.putExtra (Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=" + getPackageName ());
                                startActivity (Intent.createChooser (shareApp, "Share"));*/
                                break;
                            case 16:
                                Intent dialIntent = new Intent (Intent.ACTION_DIAL);
                                dialIntent.setData (Uri.parse ("tel:" + buyerDetailsPref.getStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_MOBILE)));
                                startActivity (dialIntent);
                                break;
    
                            case 17:
                                Intent intent17 = new Intent (MainActivity.this, ContactUsAdminActivity.class);
                                startActivity (intent17);
                                overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
                                break;
                        }
                        return false;
                    }
                });
        
        switch (buyerDetailsPref.getStringPref (MainActivity.this, buyerDetailsPref.LOGIN_TYPE))
        
        {
    
            case "INSIDER":
                drawerBuilder.addDrawerItems (
                        new PrimaryDrawerItem ().withName ("Home").withIcon (FontAwesome.Icon.faw_home).withIdentifier (1).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("My Scheduled Access").withIcon (FontAwesome.Icon.faw_calendar).withIdentifier (11).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("How It Works").withIcon (FontAwesome.Icon.faw_handshake_o).withIdentifier (3).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Share This App").withIcon (FontAwesome.Icon.faw_share).withIdentifier (2).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Contact Us (Home Trust)").withIcon (FontAwesome.Icon.faw_phone).withIdentifier (17).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Contact Your Buyer").withIcon (FontAwesome.Icon.faw_key).withIdentifier (14).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Sign Out").withIcon (FontAwesome.Icon.faw_sign_out).withIdentifier (10).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                );
                break;
    
            case "BUYER":
                drawerBuilder.addDrawerItems (
                        new PrimaryDrawerItem ().withName ("Home").withIcon (FontAwesome.Icon.faw_home).withIdentifier (1).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("About Us").withIcon (FontAwesome.Icon.faw_info).withIdentifier (4).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Contact Me").withIcon (FontAwesome.Icon.faw_phone).withIdentifier (6).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Call Me").withIcon (FontAwesome.Icon.faw_phone).withIdentifier (16).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("FAQ").withIcon (FontAwesome.Icon.faw_question).withIdentifier (7).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this)),
                        new PrimaryDrawerItem ().withName ("Sign Out").withIcon (FontAwesome.Icon.faw_sign_out).withIdentifier (10).withSelectable (false).withTypeface (SetTypeFace.getTypeface (MainActivity.this))
                );
        
                break;
    
    
        }
        result = drawerBuilder.build ();
    }
    
    private void showLogOutDialog () {
        MaterialDialog dialog = new MaterialDialog.Builder (this)
                .limitIconToDefaultSize ()
                .content ("Do you wish to Sign Out?")
                .positiveText ("Yes")
                .negativeText ("No")
                .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                .onPositive (new MaterialDialog.SingleButtonCallback () {
                    @Override
                    public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
    
                        buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_NAME, "");
                        buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_EMAIL, "");
                        buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_MOBILE, "");
                        buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_PHOTO, "");
                        buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.INSIDER_LOGO, "");
                        buyerDetailsPref.putIntPref (MainActivity.this, BuyerDetailsPref.INSIDER_ID, 0);
                        Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                        intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity (intent);
                        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                }).build ();
        dialog.show ();
    }
    
    @Override
    public void onBackPressed () {
        if (result != null && result.isDrawerOpen ()) {
            result.closeDrawer ();
        } else {
            super.onBackPressed ();
            finish ();
            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
    
    @Override
    public void onDestroy () {
        super.onDestroy ();
//        filterDetailsPref.putBooleanPref (MainActivity.this, FilterDetailsPref.FILTER_APPLIED, false);
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_BATHROOMS, "");
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_BEDROOMS, "");
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_CITIES, "");
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_LOCATION, "");
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_PRICE_MIN, "");
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_PRICE_MAX, "");
//        filterDetailsPref.putStringPref (MainActivity.this, FilterDetailsPref.FILTER_STATUS, "");
    }
    
    @Override
    public void onResume () {
        swipeRefreshLayout.setRefreshing (true);
        super.onResume ();
        // put your code here...
        getAllProperties ();
    }
    
    
    private void initApplication () {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager ().getPackageInfo (getPackageName (), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace ();
        }
        
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, AppConfigTags.URL, AppConfigURL.URL_INIT, true);
            final PackageInfo finalPInfo = pInfo;
            StringRequest strRequest = new StringRequest (Request.Method.POST, AppConfigURL.URL_INIT,
                    new Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    int status = jsonObj.getInt (AppConfigTags.STATUS);
    
                                    if (! error) {
                                     /*   if (jsonObj.getInt (AppConfigTags.VERSION_UPDATE) > 0) {
                                            if (jsonObj.getInt (AppConfigTags.VERSION_CRITICAL) == 1) {
                                                MaterialDialog dialog = new MaterialDialog.Builder (MainActivity.this)
                                                        .title ("New Update Available")
                                                        .content (jsonObj.getString (AppConfigTags.UPDATE_MESSAGE))
                                                        .titleColor (getResources ().getColor (R.color.text_color_black))
                                                        .positiveColor (getResources ().getColor (R.color.text_color_black))
                                                        .contentColor (getResources ().getColor (R.color.text_color_black))
                                                        .negativeColor (getResources ().getColor (R.color.text_color_black))
                                                        .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                        .canceledOnTouchOutside (false)
                                                        .cancelable (false)
                                                        .positiveText ("UPDATE")
                                                        .negativeText ("EXIT")
                                                        .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                final String appPackageName = getPackageName ();
                                                                try {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        })
                                                        .onNegative (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                finish ();
                                                                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                                            }
                                                        }).build ();

                                                dialog.getActionButton (DialogAction.POSITIVE).setOnClickListener (new CustomListener (MainActivity.this, dialog, DialogAction.POSITIVE));
                                                dialog.getActionButton (DialogAction.NEGATIVE).setOnClickListener (new CustomListener (MainActivity.this, dialog, DialogAction.NEGATIVE));
                                                dialog.show ();
                                            } else {

                                                Log.d ("Update", jsonObj.getString (AppConfigTags.UPDATE_MESSAGE));
                                                MaterialDialog dialog = new MaterialDialog.Builder (MainActivity.this)
                                                        .title ("New Update Available")
                                                        .content (jsonObj.getString (AppConfigTags.UPDATE_MESSAGE))
                                                        .titleColor (getResources ().getColor (R.color.text_color_black))
                                                        .positiveColor (getResources ().getColor (R.color.text_color_black))
                                                        .contentColor (getResources ().getColor (R.color.text_color_black))
                                                        .negativeColor (getResources ().getColor (R.color.text_color_black))

                                                        .typeface (SetTypeFace.getTypeface (MainActivity.this), SetTypeFace.getTypeface (MainActivity.this))
                                                        .canceledOnTouchOutside (true)
                                                        .cancelable (true)
                                                        .positiveText ("UPDATE")
                                                        .negativeText ("IGNORE")
                                                        .onPositive (new MaterialDialog.SingleButtonCallback () {
                                                            @Override
                                                            public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                                final String appPackageName = getPackageName ();
                                                                try {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("market://details?id=" + appPackageName)));
                                                                } catch (android.content.ActivityNotFoundException anfe) {
                                                                    startActivity (new Intent (Intent.ACTION_VIEW, Uri.parse ("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                                                }
                                                            }
                                                        }).build ();
                                                dialog.show ();
                                            }
                                        }*/
        
                                    } else {
                                        if (status == 2) {
    
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.BUYER_NAME, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.BUYER_EMAIL, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.BUYER_MOBILE, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.BUYER_LOGIN_KEY, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.BUYER_IMAGE, "");
                                            buyerDetailsPref.putIntPref (MainActivity.this, BuyerDetailsPref.BUYER_ID, 0);
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.BUYER_FACEBOOK_ID, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.PROFILE_HOME_TYPE, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.PROFILE_STATE, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.PROFILE_PRICE_RANGE, "");
                                            buyerDetailsPref.putStringPref (MainActivity.this, BuyerDetailsPref.ACTIVE_SESSION, "");
    
                                            Intent intent = new Intent (MainActivity.this, LoginActivity.class);
                                            intent.setFlags (Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity (intent);
                                            overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
                                        } else {
                                            Utils.showSnackBar (MainActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        }
        
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace ();
                                }
                            } else {
                                //   initDefaultBanner ();
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                        }
                    },
                    new Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                        }
                    }) {
                
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<> ();
                    params.put ("app_version", String.valueOf (finalPInfo.versionCode));
                    params.put (AppConfigTags.TYPE, "init");
                    params.put (AppConfigTags.BUYER_DEVICE, "ANDROID");
                    params.put (AppConfigTags.BUYER_ID, String.valueOf (buyerDetailsPref.getIntPref (MainActivity.this, BuyerDetailsPref.BUYER_ID)));
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }
                
                @Override
                public Map<String, String> getHeaders () throws AuthFailureError {
                    Map<String, String> params = new HashMap<> ();
                    Utils.showLog (Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            strRequest.setRetryPolicy (new DefaultRetryPolicy (DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Utils.sendRequest (strRequest, 30);
        } else {
        }
        
    }
}

