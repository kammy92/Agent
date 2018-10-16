package com.insideragent.insideragent.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.insideragent.insideragent.R;
import com.insideragent.insideragent.adapter.ScheduleTourAdapter;
import com.insideragent.insideragent.model.ScheduleTour;
import com.insideragent.insideragent.utils.AppConfigTags;
import com.insideragent.insideragent.utils.AppConfigURL;
import com.insideragent.insideragent.utils.BuyerDetailsPref;
import com.insideragent.insideragent.utils.Constants;
import com.insideragent.insideragent.utils.NetworkConnection;
import com.insideragent.insideragent.utils.Utils;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by actiknow on 6/21/17.
 */

public class ScheduleTourActivity extends AppCompatActivity {
    RecyclerView rvScheduleTour;
    CoordinatorLayout clMain;
    SwipeRefreshLayout swipeRefreshLayout;
    List<ScheduleTour> scheduleTourList = new ArrayList<> ();
    ScheduleTourAdapter scheduleTourAdapter;
    int property_id = 0;
    String property_address = "";
    String property_address_full = "";
    String property_city = "";
    RelativeLayout rlBack;
    FloatingActionButton fabChat;
    BuyerDetailsPref buyerDetailsPref;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_schedule_tour_list);
        initView ();
        getExtras ();
        initData ();
        initListener ();
        getScheduleList ();
    }
    
    private void getExtras () {
        Intent intent = getIntent ();
        property_address = intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS);
        property_address_full = intent.getStringExtra (AppConfigTags.PROPERTY_ADDRESS_FULL);
        property_city = intent.getStringExtra (AppConfigTags.PROPERTY_CITY);
        property_id = intent.getIntExtra (AppConfigTags.PROPERTY_ID, 0);
    }
    
    private void initData () {
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        swipeRefreshLayout.setRefreshing (true);
        scheduleTourAdapter = new ScheduleTourAdapter (this, scheduleTourList);
        rvScheduleTour.setAdapter (scheduleTourAdapter);
        rvScheduleTour.setHasFixedSize (true);
        rvScheduleTour.setLayoutManager (new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvScheduleTour.setItemAnimator (new DefaultItemAnimator());
        Utils.setTypefaceToAllViews (this, rlBack);
    }
    
    private void initView () {
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        rvScheduleTour = (RecyclerView) findViewById (R.id.rvScheduleTour);
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        fabChat = (FloatingActionButton) findViewById (R.id.fabChat);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById (R.id.swipe_refresh_layout);
    }
    
    private void initListener () {
    

        rlBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        swipeRefreshLayout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh () {
                swipeRefreshLayout.setRefreshing (true);
                getScheduleList ();
            }
        });
        
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
    private void getScheduleList () {
        if (NetworkConnection.isNetworkAvailable (ScheduleTourActivity.this)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_SCHEDULE_TOUR, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_SCHEDULE_TOUR,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            scheduleTourList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject (response);
                                    boolean error = jsonObj.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObj.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
    
                                        JSONArray jsonArraySchedule = jsonObj.getJSONArray (AppConfigTags.SCHEDULE_ACCESS_TOKEN);
                                        for (int i = 0; i < jsonArraySchedule.length (); i++) {
                                            JSONObject jsonObjectSchedule = jsonArraySchedule.getJSONObject (i);
                                            ScheduleTour scheduleTour = new ScheduleTour (
                                                    jsonObjectSchedule.getInt (AppConfigTags.SCHEDULE_ACCESS_TOKEN_ID),
                                                    property_address_full + ", " + property_city,
                                                    jsonObjectSchedule.getString (AppConfigTags.SCHEDULE_DATE),
                                                    jsonObjectSchedule.getString (AppConfigTags.SCHEDULE_TIME),
                                                    jsonObjectSchedule.getString (AppConfigTags.BUYER_ADDRESS));
                                            scheduleTourList.add (scheduleTour);
                                        }
                                        
                                        scheduleTourAdapter.notifyDataSetChanged ();
                                        if (jsonArraySchedule.length () > 0) {
                                            swipeRefreshLayout.setRefreshing (false);
                                        }
                                    } else {
                                        Utils.showSnackBar (ScheduleTourActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                } catch (Exception e) {
                                    Utils.showSnackBar (ScheduleTourActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (ScheduleTourActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
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
                            Utils.showSnackBar (ScheduleTourActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    BuyerDetailsPref buyerDetailsPref = BuyerDetailsPref.getInstance ();
                    params.put (AppConfigTags.TYPE, "property_access_token");
                    params.put (AppConfigTags.PROPERTY_ID, String.valueOf (property_id));
                    params.put (AppConfigTags.BUYER_ID, String.valueOf (buyerDetailsPref.getIntPref (ScheduleTourActivity.this, BuyerDetailsPref.INSIDER_ID)));
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
        }
    }
}
