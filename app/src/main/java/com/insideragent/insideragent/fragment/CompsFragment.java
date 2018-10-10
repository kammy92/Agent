package com.insideragent.insideragent.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.webkit.WebView;

import com.insideragent.insideragent.R;
import com.insideragent.insideragent.utils.Constants;
import com.insideragent.insideragent.utils.PropertyDetailsPref;


/**
 * Created by l on 23/03/2017.
 */

public class CompsFragment extends Fragment {
    PropertyDetailsPref propertyDetailsPref;
    WebView webView;

//    LinearLayout llComps;
//    LinearLayout llLoading;
//    CardView cardView1;
    
    Animation animation1, animation2;
    
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate (R.layout.fragment_comps, container, false);
        initView (rootView);
        initData ();
        initListener ();
        return rootView;
    
    }
    
    private void initView (View rootView) {
        webView = (WebView) rootView.findViewById (R.id.wvDescription);
//        llLoading = (LinearLayout) rootView.findViewById (R.id.llLoading);
//        llComps = (LinearLayout) rootView.findViewById (R.id.llComps);
    }
    
    private void initData () {
        propertyDetailsPref = PropertyDetailsPref.getInstance ();

//        animation1 = new AlphaAnimation (0.0f, 1.0f);
//        animation1.setDuration (1000);
//        animation2 = new AlphaAnimation (1.0f, 0.0f);
//        animation2.setDuration (1000);
//
//        llLoading.startAnimation (animation2);
//
//
        if (propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_COMPS).length () > 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder ("<style>@font-face{font-family: myFont;src: url(file:///android_asset/" + Constants.font_name + ");}</style>" + propertyDetailsPref.getStringPref (getActivity (), PropertyDetailsPref.PROPERTY_COMPS));
            webView.loadDataWithBaseURL ("www.google.com", spannableStringBuilder.toString (), "text/html", "UTF-8", "");
//            llLoading.setVisibility (View.GONE);
//            llComps.setVisibility (View.VISIBLE);
//
////            Animation a = new Animation () {
////                @Override
////                protected void applyTransformation (float interpolatedTime, Transformation t) {
////                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////                    params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
////                    cardView1.setLayoutParams (params);
////                }
////            };
////            a.setDuration (100); // in ms
////            cardView1.startAnimation (a);
////            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams (RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
////            params.setMargins ((int) (Utils.pxFromDp (getActivity (), 8.0f)), 0, (int) (Utils.pxFromDp (getActivity (), 8.0f)), (int) (Utils.pxFromDp (getActivity (), 8.0f)));
////            cardView1.setLayoutParams (params);
//
        } else {
//            llComps.setVisibility (View.GONE);
//            llLoading.setVisibility (View.VISIBLE);
        }
    }
    
    private void initListener () {
//        animation1.setAnimationListener (new Animation.AnimationListener () {
//
//            @Override
//            public void onAnimationEnd (Animation arg0) {
//                // start animation2 when animation1 ends (continue)
//                llLoading.startAnimation (animation2);
//            }
//
//            @Override
//            public void onAnimationRepeat (Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationStart (Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//        });
//
//        animation2.setAnimationListener (new Animation.AnimationListener () {
//
//            @Override
//            public void onAnimationEnd (Animation arg0) {
//                // start animation1 when animation2 ends (repeat)
//                llLoading.startAnimation (animation1);
//            }
//
//            @Override
//            public void onAnimationRepeat (Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onAnimationStart (Animation arg0) {
//                // TODO Auto-generated method stub
//
//            }
//
//        });
    }
}
