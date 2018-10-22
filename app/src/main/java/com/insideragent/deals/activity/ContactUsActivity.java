package com.insideragent.deals.activity;


import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.insideragent.deals.R;
import com.insideragent.deals.utils.AppConfigTags;
import com.insideragent.deals.utils.AppConfigURL;
import com.insideragent.deals.utils.BuyerDetailsPref;
import com.insideragent.deals.utils.Constants;
import com.insideragent.deals.utils.NetworkConnection;
import com.insideragent.deals.utils.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by l on 22/03/2017.
 */

public class ContactUsActivity extends AppCompatActivity {
    EditText etName;
    EditText etEmail;
    EditText etMobile;
    EditText etMessage;
    TextView tvCall;
    TextView tvEmail;
    RelativeLayout rlBack;
    CoordinatorLayout clMain;
    ProgressDialog progressDialog;
    BuyerDetailsPref buyerDetailsPref;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.etUserName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobile = (EditText) findViewById(R.id.etPhone);
        etMessage = (EditText) findViewById(R.id.etMessage);
        tvCall = (TextView) findViewById(R.id.tvCall);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        clMain = (CoordinatorLayout) findViewById(R.id.clMain);
        rlBack = (RelativeLayout) findViewById(R.id.rlBack);
        Utils.setTypefaceToAllViews(this, tvCall);
    }

    private void initData() {
        buyerDetailsPref = BuyerDetailsPref.getInstance();
        progressDialog = new ProgressDialog(ContactUsActivity.this);
        Log.e("MOBILE", "" + buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.BUYER_MOBILE));
        etName.setText(buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.BUYER_NAME));

        etEmail.setText(buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.INSIDER_EMAIL));
        etMobile.setText(buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.INSIDER_MOBILE));
        etName.setText(buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.INSIDER_NAME));
    }

    private void initListener() {
        tvEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = getIntent();
                address = intent1.getStringExtra(AppConfigTags.PROPERTY_ADDRESS);
                Log.e("adress", address);
                try {

                    String mailto = "mailto:" + buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.INSIDER_EMAIL) +
                            "?cc=" + "" +
                            "&subject=" + Uri.encode(address) +
                            "&body=" + Uri.encode("");

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse(mailto));
                    startActivity(emailIntent);

                } catch (ActivityNotFoundException e) {
                    //TODO smth
                }
            }

        });
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("smsto:"+(buyerDetailsPref.getStringPref(ContactUsActivity.this, BuyerDetailsPref.INSIDER_MOBILE)));
                    Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                    intent.putExtra("sms_body", "The SMS text");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS faild, please try again later!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }



            }
        });


        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etName.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etEmail.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etMobile.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etMessage.setError(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }

    private void contactDetailsSendToServer(final String name, final String email, final String number, final String message) {
        if (NetworkConnection.isNetworkAvailable(ContactUsActivity.this)) {
            Utils.showProgressDialog(progressDialog, getResources().getString(R.string.progress_dialog_text_please_wait), true);
            Utils.showLog(Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_CONTACT_US, true);
            StringRequest strRequest1 = new StringRequest(Request.Method.POST, AppConfigURL.URL_CONTACT_US,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Utils.showLog(Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                    if (!error) {
                                        Utils.showSnackBar(ContactUsActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);
                                    } else {
                                        Utils.showSnackBar(ContactUsActivity.this, clMain, message, Snackbar.LENGTH_LONG, null, null);
                                    }
                                    progressDialog.dismiss();
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    Utils.showSnackBar(ContactUsActivity.this, clMain, getResources().getString(R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace();
                                }
                            } else {
                                Utils.showSnackBar(ContactUsActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                                Utils.showLog(Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressDialog.dismiss();
                            Utils.showLog(Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString(), true);
                            Utils.showSnackBar(ContactUsActivity.this, clMain, getResources().getString(R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String>();
                    params.put(AppConfigTags.NAME, name);
                    params.put(AppConfigTags.EMAIL, email);
                    params.put(AppConfigTags.MOBILE, number);
                    params.put(AppConfigTags.MESSAGE, message);
                    params.put(AppConfigTags.TYPE, "contact_us");
                    Utils.showLog(Log.INFO, AppConfigTags.PARAMETERS_SENT_TO_THE_SERVER, "" + params, true);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put(AppConfigTags.HEADER_API_KEY, Constants.api_key);
                    Utils.showLog(Log.INFO, AppConfigTags.HEADERS_SENT_TO_THE_SERVER, "" + params, false);
                    return params;
                }
            };
            Utils.sendRequest(strRequest1, 60);
        } else {
            Utils.showSnackBar(this, clMain, getResources().getString(R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources().getString(R.string.snackbar_action_go_to_settings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dialogIntent = new Intent(Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(dialogIntent);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}




