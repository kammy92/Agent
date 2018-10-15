package com.insideragent.insideragent.model;

/**
 * Created by actiknow on 9/21/17.
 */

public class MyAppointment {
    int appointment_id, buyer_id, access_token_id, property_id;
    String property_address, user_comment, user_address, time_frame, access_start_date, status;
    
    public MyAppointment(int appointment_id, int buyer_id, int access_token_id, int property_id, String status, String property_address, String user_comment, String user_address,
                         String time_frame, String access_start_date) {
        this.appointment_id = appointment_id;
        this.buyer_id = buyer_id;
        this.access_token_id = access_token_id;
        this.property_id = property_id;
        this.status = status;
        this.property_address = property_address;
        this.user_comment = user_comment;
        this.user_address = user_address;
        this.time_frame = time_frame;
        this.access_start_date = access_start_date;
    }
    
    public int getAppointment_id () {
        return appointment_id;
    }
    
    public void setAppointment_id (int appointment_id) {
        this.appointment_id = appointment_id;
    }
    
    public int getAccess_token_id () {
        return access_token_id;
    }
    
    public void setAccess_token_id (int access_token_id) {
        this.access_token_id = access_token_id;
    }
    
    
    public int getProperty_id () {
        return property_id;
        
    }
    
    public void setProperty_id (int property_id) {
        this.property_id = property_id;
    }
    
    public int getBuyer_id () {
        return buyer_id;
    }
    
    public void setBuyer_id (int buyer_id) {
        this.buyer_id = buyer_id;
    }
    
    public String getStatus () {
        return status;
    }
    
    public void setStatus (String status) {
        this.status = status;
    }
    
    public String getProperty_address () {
        return property_address;
    }
    
    public void setProperty_address (String property_address) {
        this.property_address = property_address;
    }
    
    public String getUser_comment () {
        return user_comment;
    }
    
    public void setUser_comment (String user_comment) {
        this.user_comment = user_comment;
    }
    
    public String getUser_address () {
        return user_address;
    }
    
    public void setUser_address (String user_address) {
        this.user_address = user_address;
    }
    
    public String getTime_frame () {
        return time_frame;
    }
    
    public void setTime_frame (String time_frame) {
        this.time_frame = time_frame;
    }
    
    public String getAccess_start_date () {
        return access_start_date;
    }
    
    public void setAccess_start_date (String access_start_date) {
        this.access_start_date = access_start_date;
    }
    
    
}
