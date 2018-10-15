
package com.insideragent.insideragent.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.insideragent.insideragent.R;
import com.insideragent.insideragent.utils.AppConfigTags;
import com.insideragent.insideragent.utils.AppConfigURL;
import com.insideragent.insideragent.utils.BuyerDetailsPref;
import com.insideragent.insideragent.utils.Constants;
import com.insideragent.insideragent.utils.NetworkConnection;
import com.insideragent.insideragent.utils.SetTypeFace;
import com.insideragent.insideragent.utils.TypefaceSpan;
import com.insideragent.insideragent.utils.Utils;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText etEmail;
    EditText etPassword;
    TextView tvForgotPassword;
    TextView tvSignIn;
    CoordinatorLayout clMain;
    ProgressDialog progressDialog;
    BuyerDetailsPref buyerDetailsPref;
    PackageInfo info;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_login);
        initView ();
        initData ();
        initListener ();
    }
    
    private void initData () {
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        progressDialog = new ProgressDialog (LoginActivity.this);
    }
    
    private void initView () {
        etEmail = (EditText) findViewById (R.id.etEmail);
        etPassword = (EditText) findViewById (R.id.etPassword);
        tvSignIn = (TextView) findViewById (R.id.tvSignIn);

        clMain = (CoordinatorLayout) findViewById (R.id.clMain);
        Utils.setTypefaceToAllViews (LoginActivity.this, tvSignIn);
    }
    
    private void initListener () {
        etEmail.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etEmail.setError (null);
                }
            }

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        etPassword.addTextChangedListener (new TextWatcher () {
            @Override
            public void onTextChanged (CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    etPassword.setError (null);
                }
            }

            @Override
            public void beforeTextChanged (CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged (Editable s) {
            }
        });
        tvSignIn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                Utils.hideSoftKeyboard (LoginActivity.this);
                SpannableString s1 = new SpannableString (getResources ().getString (R.string.please_enter_email));
                s1.setSpan (new TypefaceSpan (LoginActivity.this, Constants.font_name), 0, s1.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s2 = new SpannableString (getResources ().getString (R.string.please_enter_valid_email));
                s2.setSpan (new TypefaceSpan (LoginActivity.this, Constants.font_name), 0, s2.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                SpannableString s3 = new SpannableString (getResources ().getString (R.string.please_enter_password));
                s3.setSpan (new TypefaceSpan(LoginActivity.this, Constants.font_name), 0, s3.length (), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (etEmail.getText ().toString ().trim ().length () == 0 && etPassword.getText ().toString ().length () == 0) {
                    etEmail.setError (s1);
                    etPassword.setError (s3);
                } else if (etEmail.getText ().toString().trim()=="") {
                    etEmail.setError (s2);

                } else {
                    sendLoginCredentialsToServer (etEmail.getText ().toString ().trim (), etPassword.getText ().toString ().trim ());
                }
            }
        });
    }

    private void sendLoginCredentialsToServer (final String email, final String password) {
        if (NetworkConnection.isNetworkAvailable (LoginActivity.this)) {
            Utils.showProgressDialog (progressDialog, getResources ().getString (R.string.progress_dialog_text_please_wait), true);
            Utils.showLog (Log.INFO, "" + AppConfigTags.URL, AppConfigURL.URL_SIGN_IN, true);
            StringRequest strRequest1 = new StringRequest (Request.Method.POST, AppConfigURL.URL_SIGN_IN,
                    new com.android.volley.Response.Listener<String> () {
                        @Override
                        public void onResponse (String response) {
                            Utils.showLog (Log.INFO, AppConfigTags.SERVER_RESPONSE, response, true);
                            if (response != null) {
                                try {
                                    JSONObject jsonObj = new JSONObject(response);
                                    boolean error = jsonObj.getBoolean(AppConfigTags.ERROR);
                                    String message = jsonObj.getString(AppConfigTags.MESSAGE);
                                  //  String type = jsonObj.getString(AppConfigTags.TYPE);
                                    if(!error) {
                                        buyerDetailsPref.putIntPref(LoginActivity.this, BuyerDetailsPref.INSIDER_ID, jsonObj.getInt(AppConfigTags.INSIDER_ID));
                                        buyerDetailsPref.putIntPref(LoginActivity.this, BuyerDetailsPref.BUYER_ID, jsonObj.getInt(AppConfigTags.INSIDER_ID));
                                        buyerDetailsPref.putStringPref(LoginActivity.this, BuyerDetailsPref.INSIDER_NAME, jsonObj.getString(AppConfigTags.INSIDER_NAME));
                                        buyerDetailsPref.putStringPref(LoginActivity.this, BuyerDetailsPref.INSIDER_EMAIL, jsonObj.getString(AppConfigTags.INSIDER_EMAIL));
                                        buyerDetailsPref.putStringPref(LoginActivity.this, BuyerDetailsPref.INSIDER_MOBILE, jsonObj.getString(AppConfigTags.INSIDER_MOBILE));
                                        buyerDetailsPref.putStringPref(LoginActivity.this, BuyerDetailsPref.LOGIN_TYPE, jsonObj.getString(AppConfigTags.LOGIN_TYPE));


                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        LoginActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);


                                        progressDialog.dismiss();
                                    }else{
                                        progressDialog.dismiss();
                                        Utils.showSnackBar (LoginActivity.this, clMain, message, Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss ();
                                    Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_exception_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                    e.printStackTrace ();
                                }
                            } else {
                                Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                                Utils.showLog (Log.WARN, AppConfigTags.SERVER_RESPONSE, AppConfigTags.DIDNT_RECEIVE_ANY_DATA_FROM_SERVER, true);
                            }
                            progressDialog.dismiss ();
                        }
                    },
                    new com.android.volley.Response.ErrorListener () {
                        @Override
                        public void onErrorResponse (VolleyError error) {
                            progressDialog.dismiss ();
                            Utils.showLog (Log.ERROR, AppConfigTags.VOLLEY_ERROR, error.toString (), true);
                            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_error_occurred), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_dismiss), null);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams () throws AuthFailureError {
                    Map<String, String> params = new Hashtable<String, String> ();
                    params.put (AppConfigTags.BUYER_DEVICE, "ANDROID");
                    params.put (AppConfigTags.BUYER_USERNAME, email);
                    params.put (AppConfigTags.BUYER_PASSWORD, password);
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
            Utils.showSnackBar (LoginActivity.this, clMain, getResources ().getString (R.string.snackbar_text_no_internet_connection_available), Snackbar.LENGTH_LONG, getResources ().getString (R.string.snackbar_action_go_to_settings), new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    Intent dialogIntent = new Intent (Settings.ACTION_SETTINGS);
                    dialogIntent.addFlags (Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity (dialogIntent);
                }
            });
        }


    }
    
    @Override
    public void onBackPressed () {
        super.onBackPressed ();
        finish ();
//        overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
    }
}