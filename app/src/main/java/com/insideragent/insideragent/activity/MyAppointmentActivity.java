package com.insideragent.insideragent.activity;

import android.app.ProgressDialog;
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
import com.insideragent.insideragent.adapter.MyAppointmentAdapter;
import com.insideragent.insideragent.model.MyAppointment;
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
 * Created by actiknow on 9/21/17.
 */

public class MyAppointmentActivity extends AppCompatActivity {
    RecyclerView rvMyAppointmentlist;
    SwipeRefreshLayout swipe_refresh_layout;
    ProgressDialog progress_dialog;
    RelativeLayout rlInternetConnection;
    RelativeLayout rlNoResultFound;
    List<MyAppointment> myAppointmentList = new ArrayList<> ();
    CoordinatorLayout clMain;
    BuyerDetailsPref buyerDetailsPref;
    MyAppointmentAdapter myAppointmentAdapter;
    RelativeLayout rlBack;
    FloatingActionButton fabChat;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_my_appointment);
        initView ();
        initData ();
        initListener ();
        getAppointmentList ();
        
    }
    
    private void initListener () {

        rlBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                finish ();
                overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        swipe_refresh_layout.setOnRefreshListener (new SwipeRefreshLayout.OnRefreshListener () {
            @Override
            public void onRefresh () {
                swipe_refresh_layout.setRefreshing (true);
                getAppointmentList ();
            }
        });
    }
    
    
    private void initData () {
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        progress_dialog = new ProgressDialog (this);
        myAppointmentAdapter = new MyAppointmentAdapter (MyAppointmentActivity.this, myAppointmentList);
        rvMyAppointmentlist.setAdapter (myAppointmentAdapter);
        rvMyAppointmentlist.setHasFixedSize (true);
        rvMyAppointmentlist.setLayoutManager (new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMyAppointmentlist.setItemAnimator (new DefaultItemAnimator());
        Utils.setTypefaceToAllViews (this, clMain);
    }
    
    private void initView () {
        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        swipe_refresh_layout = (SwipeRefreshLayout) findViewById (R.id.swipe_refresh_layout);
        rvMyAppointmentlist = (RecyclerView) findViewById (R.id.rvMyAppointmentlist);
        rlNoResultFound = (RelativeLayout) findViewById (R.id.rlNoResultFound);
        rlInternetConnection = (RelativeLayout) findViewById (R.id.rlInternetConnection);
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        fabChat = (FloatingActionButton) findViewById (R.id.fabChat);
    }
    
    private void getAppointmentList () {
        if (NetworkConnection.isNetworkAvailable (this)) {
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_MY_APPOINTMENT, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_MY_APPOINTMENT,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            myAppointmentList.clear ();
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    rlInternetConnection.setVisibility (View.GONE);
                                    rlNoResultFound.setVisibility (View.GONE);
                                    rvMyAppointmentlist.setVisibility (View.VISIBLE);
                                    JSONObject jsonObject = new JSONObject (response);
                                    boolean error = jsonObject.getBoolean (AppConfigTags.ERROR);
                                    String message = jsonObject.getString (AppConfigTags.MESSAGE);
                                    if (! error) {
                                        JSONArray jsonArrayMyAppointment = jsonObject.getJSONArray (AppConfigTags.APPOINTMENTS);
                                        for (int i = 0; i < jsonArrayMyAppointment.length (); i++) {
                                            JSONObject jsonObjectMyAppointment = jsonArrayMyAppointment.getJSONObject (i);
                                            myAppointmentList.add (new MyAppointment (
                                                    jsonObjectMyAppointment.getInt (AppConfigTags.SCHEDULE_APPOINTMENT_ID), jsonObjectMyAppointment.getInt (AppConfigTags.BUYER_ID),
                                                    jsonObjectMyAppointment.getInt (AppConfigTags.SCHEDULE_ACCESS_TOKEN_ID),
                                                    jsonObjectMyAppointment.getInt (AppConfigTags.PROPERTY_ID),
                                                    jsonObjectMyAppointment.getString (AppConfigTags.STATUS),
                                                    jsonObjectMyAppointment.getString (AppConfigTags.PROPERTY_ADDRESS),
                                                    jsonObjectMyAppointment.getString (AppConfigTags.SCHEDULE_COMMENT),
                                                    jsonObjectMyAppointment.getString (AppConfigTags.SCHEDULE_ADDRESS),
                                                    jsonObjectMyAppointment.getString (AppConfigTags.SCHEDULE_TIME),
                                                    jsonObjectMyAppointment.getString (AppConfigTags.SCHEDULE_DATE)));
                                        }
                                        if (jsonArrayMyAppointment.length () > 0) {
                                            swipe_refresh_layout.setRefreshing (false);
                                        }
                                        myAppointmentAdapter.notifyDataSetChanged ();
                                    } else {
                                        rlInternetConnection.setVisibility (View.GONE);
                                        rlNoResultFound.setVisibility (View.VISIBLE);
                                        rvMyAppointmentlist.setVisibility (View.GONE);
                                    }
                                } catch (Exception e) {
                                    Utils.showSnackBar (MyAppointmentActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                                
                            } else {
                                Utils.showSnackBar (MyAppointmentActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            swipe_refresh_layout.setRefreshing (false);
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            swipe_refresh_layout.setRefreshing (false);
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (MyAppointmentActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.TYPE, AppConfigTags.PROPERTY_MY_APPOINTMENT);
                    params.put (AppConfigTags.BUYER_ID, String.valueOf (buyerDetailsPref.getIntPref (MyAppointmentActivity.this, BuyerDetailsPref.BUYER_ID)));
                    // params.put(AppConfigTags.BUYER_ID, "219");
                    Utils.showLog (Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, false);
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
            swipe_refresh_layout.setRefreshing (false);
            Utils.showSnackBar (this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View view) {
                    Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
            rlInternetConnection.setVisibility (View.VISIBLE);
            rlNoResultFound.setVisibility (View.GONE);
            rvMyAppointmentlist.setVisibility (View.GONE);
        }
    }
    
}
