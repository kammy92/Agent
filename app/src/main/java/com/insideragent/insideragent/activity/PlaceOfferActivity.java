package com.insideragent.insideragent.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.insideragent.insideragent.R;
import com.insideragent.insideragent.utils.AppConfigTags;
import com.insideragent.insideragent.utils.AppConfigURL;
import com.insideragent.insideragent.utils.SetTypeFace;
import com.insideragent.insideragent.utils.Utils;


public class PlaceOfferActivity extends AppCompatActivity {
    WebView webviewPlaceAnOffer;
    Intent intent;
    RelativeLayout rlBack;
    ProgressBar progressBar;
    FrameLayout fl1;
    
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_place_an_offer);
        initView ();
        getIntentValue ();
        initData ();
        initListener ();
    }
    
    private void getIntentValue () {
        intent = getIntent ();
    }
    
    private void initData () {
        webviewPlaceAnOffer.loadUrl (AppConfigURL.PLACE_OFFER_URL + intent.getIntExtra (AppConfigTags.PROPERTY_ID, 0) + "&buyer_id=" + intent.getIntExtra (AppConfigTags.INSIDER_ID, 0) + "&auction_id=" + intent.getIntExtra (AppConfigTags.PROPERTY_BID_AUCTION_ID, 0) + "");
        webviewPlaceAnOffer.getSettings ().setJavaScriptEnabled (true);
        webviewPlaceAnOffer.getSettings ().setGeolocationEnabled (true);
        webviewPlaceAnOffer.getSettings ().setJavaScriptCanOpenWindowsAutomatically (true);
        webviewPlaceAnOffer.getSettings ().setSupportMultipleWindows (true);
        webviewPlaceAnOffer.getSettings ().setSaveFormData (false);
        webviewPlaceAnOffer.setHorizontalScrollBarEnabled (true);
        webviewPlaceAnOffer.setVerticalScrollBarEnabled (true);
        webviewPlaceAnOffer.getSettings ().setCacheMode (webviewPlaceAnOffer.getSettings ().LOAD_NO_CACHE);
        webviewPlaceAnOffer.setWebChromeClient (new MyWebChromeClient ());
        Log.e ("URL", AppConfigURL.PLACE_OFFER_URL + intent.getIntExtra (AppConfigTags.PROPERTY_ID, 0) + "&buyer_id=" + intent.getIntExtra (AppConfigTags.INSIDER_ID, 0) + "&auction_id=" + intent.getIntExtra (AppConfigTags.PROPERTY_BID_AUCTION_ID, 0));
        Utils.setTypefaceToAllViews (this, rlBack);
    
        if (Build.VERSION.SDK_INT >= 21) {
            progressBar.setProgressTintList (ColorStateList.valueOf (getResources ().getColor (R.color.primary_dark)));
            progressBar.setIndeterminateTintList (ColorStateList.valueOf (getResources ().getColor (R.color.primary_dark)));
        } else {
            progressBar.getProgressDrawable ().setColorFilter (
                    getResources ().getColor (R.color.primary_dark), android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.getIndeterminateDrawable ().setColorFilter (
                    getResources ().getColor (R.color.primary_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
    
    private void initListener () {
        webviewPlaceAnOffer.setWebViewClient (new WebViewClient () {
            public void onPageFinished (WebView view, String url) {
                fl1.setVisibility (View.GONE);
            }
        });
    
        rlBack.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View v) {
                finish ();
            }
        });
    }
    
    private void initView () {
        webviewPlaceAnOffer = (WebView) findViewById (R.id.webviewPlaceAnOffer);
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        progressBar = (ProgressBar) findViewById (R.id.progressBar);
        fl1 = (FrameLayout) findViewById (R.id.fl1);
    }
    
    final class MyWebChromeClient extends WebChromeClient {
        public void onProgressChanged (WebView view, int progress) {
            if (progress < 30) {
                progressBar.setIndeterminate (true);
            }
            if (progress > 70) {
                progressBar.setIndeterminate (true);
            }
            if (progress < 70 && progress > 30) {
                progressBar.setIndeterminate (false);
                progressBar.setProgress (progress + 10);
            }
        }
        @Override
        public boolean onJsAlert (WebView view, String url, String message, final JsResult result) {
            MaterialDialog dialog = new MaterialDialog.Builder (PlaceOfferActivity.this)
                    .alwaysCallInputCallback ()
                    .content (message.trim ())
                    .canceledOnTouchOutside (false)
                    .positiveText ("OK")
                    .typeface (SetTypeFace.getTypeface (PlaceOfferActivity.this), SetTypeFace.getTypeface (PlaceOfferActivity.this))
                    .onPositive (new MaterialDialog.SingleButtonCallback () {
                        @Override
                        public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish ();
                            result.confirm ();
                            dialog.dismiss ();
                        }
                    }).build ();
            dialog.show ();
            return true;
        }
        
        @Override
        public boolean onJsConfirm (WebView view, String url, String message, final JsResult result) {
            MaterialDialog dialog = new MaterialDialog.Builder (PlaceOfferActivity.this)
                    .alwaysCallInputCallback ()
                    .content (message.trim ())
                    .canceledOnTouchOutside (false)
                    .positiveText ("OK")
                    .typeface (SetTypeFace.getTypeface (PlaceOfferActivity.this), SetTypeFace.getTypeface (PlaceOfferActivity.this))
                    .onPositive (new MaterialDialog.SingleButtonCallback () {
                        @Override
                        public void onClick (@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            finish ();
                            result.confirm ();
                            dialog.dismiss ();
                        }
                    }).build ();
            dialog.show ();
            return true;
        }
    }
}