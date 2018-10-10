package com.insideragent.insideragent.fragment;

import android.animation.LayoutTransition;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.insideragent.insideragent.R;
import com.insideragent.insideragent.activity.PlaceOfferActivity;
import com.insideragent.insideragent.activity.ScheduleTourActivity;
import com.insideragent.insideragent.utils.AppConfigTags;
import com.insideragent.insideragent.utils.BuyerDetailsPref;
import com.insideragent.insideragent.utils.Constants;
import com.insideragent.insideragent.utils.PropertyDetailsPref;
import com.insideragent.insideragent.utils.Utils;


public class OverviewFragment extends Fragment {
    static ProgressBar progressBar;
    PropertyDetailsPref propertyDetailsPref;
    WebView wvDescription;
    TextView tvYear;
    TextView tvBedroom;
    TextView tvBathroom;
    TextView tvPropertyRate;
    TextView tvStatus;
    TextView tvAddress1;
    TextView tvAddress2;
    TextView tvSqFeet;
    ProgressDialog progressDialog;
    BuyerDetailsPref buyerDetailsPref;
    CardView cvDescription;
    Button btShowMoreDescription;
    TextView tvPlaceAnOffer;
    boolean descriptionExpanded = true;
    WebView wvKeyDetail;
    Button btShowMoreKeyDetail;
    boolean keyDetailExpanded = true;
    boolean showofferDialog = true;
    CardView cvKeyDetails;
    TextView tvScheduleTour;
    EditText etOfferUsd;
    LinearLayout llMain;
    LinearLayout llDescription;
    LinearLayout llKeyDetails;
    LinearLayout llOverview;
    CardView cvOverview;
    Button btShowMoreOverview;
    WebView wvOverview;
    boolean overviewExpanded = true;
    LinearLayout llWorkScope;
    CardView cvWorkScope;
    Button btShowMoreWorkScope;
    WebView wvWorkScope;
    boolean workscopeExpanded = true;
    LinearLayout llFinishedProduct;
    WebView wvFinishedProduct;
    LinearLayout llClosingDetails;
    WebView wvClosingDetails;
    LinearLayout llRealtor;
    WebView wvRealtor;
    Animation animation1, animation2;
    setPropertyDetails setPropertyDetail;

//    static RelativeLayout rlErrorOccurred;
//    static TextView tvRetry;
    
    public static void hideProgressBar (boolean flag) {
        if (progressBar != null) {
            if (flag) {
                progressBar.setVisibility (View.GONE);
            } else {
                progressBar.setVisibility (View.VISIBLE);
            }
        }
    }
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_overview, container, false);
        initView (rootView);
        initData ();
        initListener ();
        //    getOverviewData();
        return rootView;
    }
    
    private void initView (View rootView) {
        // clMain = (CoordinatorLayout)rootView.findViewById(R.id.clMain);
        
        progressBar = (ProgressBar) rootView.findViewById (R.id.progressBar);
        
        tvYear = (TextView) rootView.findViewById (R.id.tvYear);
        tvBedroom = (TextView) rootView.findViewById (R.id.tvBedroom);
        tvBathroom = (TextView) rootView.findViewById (R.id.tvBathroom);
        tvPropertyRate = (TextView) rootView.findViewById (R.id.tvPropertyRate);
        tvStatus = (TextView) rootView.findViewById (R.id.tvStatus);
        tvAddress1 = (TextView) rootView.findViewById (R.id.tvAddress1);
        tvAddress2 = (TextView) rootView.findViewById (R.id.tvAddress2);
        tvSqFeet = (TextView) rootView.findViewById (R.id.tvSqFeet);
        
        llMain = (LinearLayout) rootView.findViewById (R.id.llMain);
        
        
        cvDescription = (CardView) rootView.findViewById (R.id.cvDescription);
        llDescription = (LinearLayout) rootView.findViewById (R.id.llDescription);
        wvDescription = (WebView) rootView.findViewById (R.id.wvDescription);
        btShowMoreDescription = (Button) rootView.findViewById (R.id.btShowMoreDescription);
        
        llOverview = (LinearLayout) rootView.findViewById (R.id.llOverview);
        cvOverview = (CardView) rootView.findViewById (R.id.cvOverview);
        wvOverview = (WebView) rootView.findViewById (R.id.wvOverview);
        btShowMoreOverview = (Button) rootView.findViewById (R.id.btShowMoreOverview);
        
        tvScheduleTour = (TextView) rootView.findViewById (R.id.tvScheduleTour);
    
        llWorkScope = (LinearLayout) rootView.findViewById (R.id.llWorkScope);
        cvWorkScope = (CardView) rootView.findViewById (R.id.cvWorkScope);
        wvWorkScope = (WebView) rootView.findViewById (R.id.wvWorkScope);
        btShowMoreWorkScope = (Button) rootView.findViewById (R.id.btShowMoreWorkScope);
        
        llFinishedProduct = (LinearLayout) rootView.findViewById (R.id.llFinishedProduct);
        wvFinishedProduct = (WebView) rootView.findViewById (R.id.wvFinishedProduct);
        
        llClosingDetails = (LinearLayout) rootView.findViewById (R.id.llClosingDetails);
        wvClosingDetails = (WebView) rootView.findViewById (R.id.wvClosingDetails);
        
        llKeyDetails = (LinearLayout) rootView.findViewById (R.id.llKeyDetails);
        cvKeyDetails = (CardView) rootView.findViewById (R.id.cvKeyDetails);
        wvKeyDetail = (WebView) rootView.findViewById (R.id.wvKeyDetail);
        btShowMoreKeyDetail = (Button) rootView.findViewById (R.id.btShowMoreKeyDetail);
        
        tvPlaceAnOffer = (TextView) rootView.findViewById (R.id.tvPlaceAnOffer);
        
        llRealtor = (LinearLayout) rootView.findViewById (R.id.llRealtor);
        wvRealtor = (WebView) rootView.findViewById (R.id.wvRealtor);
    }
    
    private void initData () {
        setPropertyDetail = new setPropertyDetails ();
        buyerDetailsPref = BuyerDetailsPref.getInstance ();
        propertyDetailsPref = PropertyDetailsPref.getInstance ();
//        progressDialog = new ProgressDialog (getActivity ());
        
        llMain.setLayoutTransition (new LayoutTransition ());
        
        animation1 = new AlphaAnimation (0.5f, 1.0f);
        animation1.setDuration (2000);
        animation2 = new AlphaAnimation (1.0f, 0.5f);
        animation2.setDuration (2000);
        
        
        tvYear.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_YEAR_BUILD));
        tvAddress1.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_ADDRESS1));
        tvAddress2.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_ADDRESS2));
        tvPropertyRate.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_PRICE));
        tvBedroom.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_BEDROOM));
        tvBathroom.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_BATHROOM));
        tvSqFeet.setText (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_AREA));
        switch (propertyDetailsPref.getIntPref (getActivity (), PropertyDetailsPref.PROPERTY_AUCTION_STATUS)) {
            case 1:
                Drawable img = getActivity ().getResources ().getDrawable (R.drawable.circle_green);
                img.setBounds (0, 0, 20, 20);
                tvStatus.setCompoundDrawables (img, null, null, null);
                tvStatus.setText ("Available");
                break;
            case 2:
                Drawable img2 = getActivity ().getResources ().getDrawable (R.drawable.circle_yellow);
                img2.setBounds (0, 0, 20, 20);
                tvStatus.setCompoundDrawables (img2, null, null, null);
                tvStatus.setText ("Pending");
                break;
            case 3:
                Drawable img3 = getActivity ().getResources ().getDrawable (R.drawable.circle_red);
                img3.setBounds (0, 0, 20, 20);
                tvStatus.setCompoundDrawables (img3, null, null, null);
                tvStatus.setText ("Sold");
                break;
            case 4:
                Drawable img4 = getActivity ().getResources ().getDrawable (R.drawable.circle_red);
                img4.setBounds (0, 0, 20, 20);
                tvStatus.setCompoundDrawables (img4, null, null, null);
                tvStatus.setText ("Closed");
                break;
            case 9:
                Drawable img9 = getActivity ().getResources ().getDrawable (R.drawable.circle_red);
                img9.setBounds (0, 0, 20, 20);
                tvStatus.setCompoundDrawables (img9, null, null, null);
                tvStatus.setText ("Offer Window Closing");
                break;
        }
        
        
        setPropertyDetail.execute ();


//        progressBar.setVisibility (View.GONE);
        
        Utils.setTypefaceToAllViews (getActivity (), tvAddress1);
    }
    
    private void initListener () {
        btShowMoreWorkScope.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (workscopeExpanded) {
                    btShowMoreWorkScope.setText ("LESS");
                    workscopeExpanded = false;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                            cvWorkScope.setLayoutParams (params);
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvWorkScope.startAnimation (a);
                } else {
                    btShowMoreWorkScope.setText ("MORE");
                    workscopeExpanded = true;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            if ((1.0f - interpolatedTime) < 1.0f) {
                                if ((cvWorkScope.getHeight () * (1.0f - interpolatedTime)) <= Utils.pxFromDp (getActivity (), 200.0f)) {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (Utils.pxFromDp (getActivity (), 200.0f)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvWorkScope.setLayoutParams (params);
                                } else {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (cvOverview.getHeight () * (1.0f - interpolatedTime)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvWorkScope.setLayoutParams (params);
                                }
                            }
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvWorkScope.startAnimation (a);
                }
            }
        });
        
        btShowMoreDescription.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (descriptionExpanded) {
                    btShowMoreDescription.setText ("LESS");
                    descriptionExpanded = false;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                            cvDescription.setLayoutParams (params);
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvDescription.startAnimation (a);
                } else {
                    btShowMoreDescription.setText ("MORE");
                    descriptionExpanded = true;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            if ((1.0f - interpolatedTime) < 1.0f) {
                                if ((cvDescription.getHeight () * (1.0f - interpolatedTime)) <= Utils.pxFromDp (getActivity (), 200.0f)) {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (Utils.pxFromDp (getActivity (), 200.0f)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvDescription.setLayoutParams (params);
                                } else {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (cvDescription.getHeight () * (1.0f - interpolatedTime)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvDescription.setLayoutParams (params);
                                }
                            }
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvDescription.startAnimation (a);
                }
            }
        });
        
        btShowMoreKeyDetail.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (keyDetailExpanded) {
                    btShowMoreKeyDetail.setText ("HIDE FULL DETAILS");
                    keyDetailExpanded = false;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                            cvKeyDetails.setLayoutParams (params);
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvKeyDetails.startAnimation (a);
                } else {
                    btShowMoreKeyDetail.setText ("SHOW FULL DETAILS");
                    keyDetailExpanded = true;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            if ((1.0f - interpolatedTime) < 1.0f) {
                                if ((cvKeyDetails.getHeight () * (1.0f - interpolatedTime)) <= Utils.pxFromDp (getActivity (), 200.0f)) {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (Utils.pxFromDp (getActivity (), 200.0f)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvKeyDetails.setLayoutParams (params);
                                } else {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (cvKeyDetails.getHeight () * (1.0f - interpolatedTime)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvKeyDetails.setLayoutParams (params);
                                }
                            }
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvKeyDetails.startAnimation (a);
                }
            }
        });
    
        tvPlaceAnOffer.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent placeOffer = new Intent (getActivity (), PlaceOfferActivity.class);
                placeOffer.putExtra (AppConfigTags.PROPERTY_ID, propertyDetailsPref.getIntPref (getActivity (), PropertyDetailsPref.PROPERTY_ID));
                placeOffer.putExtra (AppConfigTags.BUYER_ID, buyerDetailsPref.getIntPref (getActivity (), BuyerDetailsPref.BUYER_ID));
                placeOffer.putExtra (AppConfigTags.PROPERTY_BID_AUCTION_ID, propertyDetailsPref.getIntPref (getActivity (), PropertyDetailsPref.PROPERTY_AUCTION_ID));
                startActivity (placeOffer);
                getActivity ().overridePendingTransition (R.anim.slide_in_right, R.anim.slide_out_left);
            }
            
        });
        
        tvScheduleTour.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                Intent scheduleTour = new Intent (getActivity (), ScheduleTourActivity.class);
                scheduleTour.putExtra (AppConfigTags.PROPERTY_ID, propertyDetailsPref.getIntPref (getActivity (), PropertyDetailsPref.PROPERTY_ID));
                scheduleTour.putExtra (AppConfigTags.PROPERTY_ADDRESS, propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_ADDRESS1));
                scheduleTour.putExtra (AppConfigTags.PROPERTY_CITY, propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_ADDRESS2));
                scheduleTour.putExtra (AppConfigTags.PROPERTY_ADDRESS_FULL, propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_ADDRESS_FULL));
                startActivity (scheduleTour);
            }
        });
    
        btShowMoreOverview.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view) {
                if (overviewExpanded) {
                    btShowMoreOverview.setText ("LESS");
                    overviewExpanded = false;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                            cvOverview.setLayoutParams (params);
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvOverview.startAnimation (a);
                } else {
                    btShowMoreOverview.setText ("MORE");
                    overviewExpanded = true;
                    Animation a = new Animation () {
                        @Override
                        protected void applyTransformation (float interpolatedTime, Transformation t) {
                            if ((1.0f - interpolatedTime) < 1.0f) {
                                if ((cvOverview.getHeight () * (1.0f - interpolatedTime)) <= Utils.pxFromDp (getActivity (), 200.0f)) {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (Utils.pxFromDp (getActivity (), 200.0f)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvOverview.setLayoutParams (params);
                                } else {
                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.MATCH_PARENT, (int) (cvOverview.getHeight () * (1.0f - interpolatedTime)));
                                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
                                    cvOverview.setLayoutParams (params);
                                }
                            }
                        }
                    };
                    a.setDuration (1500); // in ms
                    cvOverview.startAnimation (a);
                }
            }
        });
        
        animation1.setAnimationListener (new Animation.AnimationListener () {
            @Override
            public void onAnimationEnd (Animation arg0) {
            }
    
            @Override
            public void onAnimationRepeat (Animation arg0) {
                // TODO Auto-generated method stub
        
            }
    
            @Override
            public void onAnimationStart (Animation arg0) {
                // TODO Auto-generated method stub
        
            }
    
        });
        
        animation2.setAnimationListener (new Animation.AnimationListener () {
            
            @Override
            public void onAnimationEnd (Animation arg0) {
            }
            
            @Override
            public void onAnimationRepeat (Animation arg0) {
                // TODO Auto-generated method stub
                
            }
    
            @Override
            public void onAnimationStart (Animation arg0) {
                // TODO Auto-generated method stub
            }
        });
    }
    
    @Override
    public void onDestroyView () {
        super.onDestroyView ();
//        setPropertyDetail.cancel (true);
    }
    
    private class setPropertyDetails extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground (String... params) {
            getActivity ().runOnUiThread (new Runnable () {
                @Override
                public void run () {
                    wvDescription.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_DESCRIPTION)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
                    wvKeyDetail.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_KEY_DETAILS)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
                    wvRealtor.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_REALTOR)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
                    wvOverview.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_OVERVIEW)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
                    wvWorkScope.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_WORK_SCOPE)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
                    wvFinishedProduct.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_FINISHED_PRODUCT)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
    
                    wvClosingDetails.loadDataWithBaseURL (
                            "www.google.com",
                            new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_CLOSING_DETAILS)).toString (),
                            "text/html",
                            "UTF-8",
                            "");
                }
            });
            
            return "Executed";
        }
        
        @Override
        protected void onPostExecute (String result) {
            Log.e ("karman", "executed");
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_DESCRIPTION).length () > 0) {
                llDescription.setVisibility (View.VISIBLE);
                if (propertyDetailsPref.getIntPref (getActivity (), PropertyDetailsPref.PROPERTY_AUCTION_STATUS) == 1) {
                    tvPlaceAnOffer.setVisibility (View.VISIBLE);
                } else {
                    tvPlaceAnOffer.setVisibility (View.GONE);
                }
    
                if (propertyDetailsPref.getIntPref (getActivity (), PropertyDetailsPref.PROPERTY_TOUR_STATUS) == 1) {
                    tvScheduleTour.setVisibility (View.VISIBLE);
                } else {
                    tvScheduleTour.setVisibility (View.GONE);
                }
                progressBar.setVisibility (View.GONE);
    
            } else {
                llDescription.setVisibility (View.GONE);
            }
    
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_KEY_DETAILS).length () > 0) {
                llKeyDetails.setVisibility (View.VISIBLE);
            } else {
                llKeyDetails.setVisibility (View.GONE);
            }
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_REALTOR).length () > 0) {
                llRealtor.setVisibility (View.VISIBLE);
            } else {
                llRealtor.setVisibility (View.GONE);
            }
    
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_OVERVIEW).length () > 0) {
                llOverview.setVisibility (View.VISIBLE);
            } else {
                llOverview.setVisibility (View.GONE);
            }
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_WORK_SCOPE).length () > 0) {
                llWorkScope.setVisibility (View.VISIBLE);
            } else {
                llWorkScope.setVisibility (View.GONE);
            }
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_FINISHED_PRODUCT).length () > 0) {
                llFinishedProduct.setVisibility (View.VISIBLE);
            } else {
                llFinishedProduct.setVisibility (View.GONE);
            }
            if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_CLOSING_DETAILS).length () > 0) {
                llClosingDetails.setVisibility (View.VISIBLE);
            } else {
                llClosingDetails.setVisibility (View.GONE);
            }
    
        }
        
        @Override
        protected void onPreExecute () {
        }
        
        @Override
        protected void onProgressUpdate (Void... values) {
        }
    }
}