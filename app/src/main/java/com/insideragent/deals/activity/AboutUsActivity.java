package com.insideragent.deals.activity;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.insideragent.deals.R;
import com.insideragent.deals.utils.BuyerDetailsPref;
import com.insideragent.deals.utils.Constants;
import com.insideragent.deals.utils.Utils;


/**
 * Created by l on 22/03/2018.
 */

public class AboutUsActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBar;
    Toolbar toolbar;
   // TextView tvHyperlink;
    RelativeLayout rlBack;
    WebView webViewAboutUs;
    BuyerDetailsPref buyerDetailsPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        rlBack = (RelativeLayout) findViewById (R.id.rlBack);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
      //  tvHyperlink=(TextView)findViewById(R.id.tvHyperlink);
        webViewAboutUs = (WebView) findViewById (R.id.webviewAboutus);
    }

    private void initData() {
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        collapsingToolbarLayout.setTitleEnabled (false);
        appBar.setExpanded (true);
        Utils.setTypefaceToAllViews (this, rlBack);
    
        webViewAboutUs.loadDataWithBaseURL (
                "www.google.com",
                new SpannableStringBuilder ("<style>@font-face{font-family: myFont; font-size: 14px;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + buyerDetailsPref.getStringPref (this, BuyerDetailsPref.ABOUT_US)).toString (),
                "text/html",
                "UTF-8",
                "");
    }

    private void initListener() {
       // tvHyperlink.setClickable(true);
      //  tvHyperlink.setMovementMethod(LinkMovementMethod.getInstance());
      //  String text = "<a href='http://www.google.com'> Google </a>";
      //  tvHyperlink.setText(Html.fromHtml(text));
    
    
        appBar.addOnOffsetChangedListener (new AppBarLayout.OnOffsetChangedListener () {
            @Override
            public void onOffsetChanged (AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs (verticalOffset) == appBarLayout.getTotalScrollRange ()) {
                    // Collapsed
                    getWindow ().clearFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    collapsingToolbarLayout.setScrimAnimationDuration ((long) 400);
                    collapsingToolbarLayout.setContentScrimColor (getResources ().getColor (R.color.primary));
                } else if (verticalOffset == 0) {
                    // Expanded
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
                    // Somewhere in between
                    collapsingToolbarLayout.setScrimsShown (false);
                    collapsingToolbarLayout.setContentScrim (null);
    
                    if (Build.VERSION.SDK_INT >= 21) {
                        Window window = getWindow ();
                        getWindow ().getDecorView ().setSystemUiVisibility (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                        window.addFlags (WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor (Color.TRANSPARENT);
                    } else {
                        getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                        getWindow ().addFlags (WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    }
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
    }
    
    @Override
    public void onBackPressed () {
        finish ();
        overridePendingTransition (R.anim.slide_in_left, R.anim.slide_out_right);
    }
    
}




