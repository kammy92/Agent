package com.insideragent.insideragent.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PropertyDetailsPref {
    
    public static String PROPERTY_ID = "property_id";
    public static String PROPERTY_STATE = "property_state";
    public static String PROPERTY_ADDRESS1 = "property_address1";
    public static String PROPERTY_ADDRESS2 = "property_address2";
    public static String PROPERTY_ADDRESS_FULL = "property_address_full";
    public static String PROPERTY_LATITUDE = "property_latitude";
    public static String PROPERTY_LONGITUDE = "property_longitude";
    public static String PROPERTY_PRICE = "property_price";
    public static String PROPERTY_YEAR_BUILD = "property_year_build";
    public static String PROPERTY_BEDROOM = "property_bedroom";
    public static String PROPERTY_BATHROOM = "property_bathroom";
    public static String PROPERTY_AREA = "property_area";
    public static String PROPERTY_DESCRIPTION = "property_description";
    public static String PROPERTY_OVERVIEW = "property_overview";
    public static String PROPERTY_COMPS = "property_comps";
    public static String PROPERTY_KEY_DETAILS = "property_key_details";
    
    public static String PROPERTY_COMPS_ADDRESSES = "property_comps_addresses";
    public static String PROPERTY_REALTOR = "property_realtor";
    public static String PROPERTY_IMAGES = "property_images";
    public static String PROPERTY_IMAGE_COUNT = "property_image_count";
    public static String PROPERTY_AUCTION_ID = "auction_id";
    public static String PROPERTY_AUCTION_STATUS = "auction_status";
    
    public static String PROPERTY_TOUR_STATUS = "tour_status";
    
    public static String PROPERTY_WORK_SCOPE = "property_work_scope";
    public static String PROPERTY_FINISHED_PRODUCT = "property_finished_product";
    public static String PROPERTY_CLOSING_DETAILS = "property_closing_details";
    public static String PROPERTY_DETAILS = "PROPERTY_DETAILS";
    private static PropertyDetailsPref propertyDetailsPref;
    
    public static PropertyDetailsPref getInstance () {
        if (propertyDetailsPref == null)
            propertyDetailsPref = new PropertyDetailsPref ();
        return propertyDetailsPref;
    }
    
    private SharedPreferences getPref (Context context) {
        return context.getSharedPreferences (PROPERTY_DETAILS, Context.MODE_PRIVATE);
    }
    
    public String getStringPref (Context context, String key) {
        try {
            return getPref (context).getString (key, "");
        } catch (Exception e) {
            e.printStackTrace ();
            return "";
        }
    }
    
    public int getIntPref (Context context, String key) {
        try {
            return getPref (context).getInt (key, 0);
        } catch (Exception e) {
            e.printStackTrace ();
            return 0;
        }
    }
    
    public boolean getBooleanPref (Context context, String key) {
        return getPref (context).getBoolean (key, false);
    }
    
    public void putBooleanPref (Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putBoolean (key, value);
        editor.apply ();
    }
    
    public void putStringPref (Context context, String key, String value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putString (key, value);
        editor.apply ();
    }
    
    public void putIntPref (Context context, String key, int value) {
        SharedPreferences.Editor editor = getPref (context).edit ();
        editor.putInt (key, value);
        editor.apply ();
    }
}
