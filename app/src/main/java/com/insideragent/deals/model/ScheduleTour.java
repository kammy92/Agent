package com.insideragent.deals.model;

public class ScheduleTour {
    int id;
    String address, date, time, buyer_address;
    
    public ScheduleTour(int id, String address, String date, String time, String buyer_address) {
        this.id = id;
        this.address = address;
        this.date = date;
        this.time = time;
        this.buyer_address = buyer_address;
    }
    
    public ScheduleTour() {
    }
    
    public int getId () {
        return id;
    }
    
    public void setId (int id) {
        this.id = id;
    }
    
    public String getAddress () {
        return address;
    }
    
    public void setAddress (String address) {
        this.address = address;
    }
    
    public String getDate () {
        return date;
    }
    
    public void setDate (String date) {
        this.date = date;
    }
    
    public String getTime () {
        return time;
    }
    
    public void setTime (String time) {
        this.time = time;
    }
    
    public String getBuyer_address () {
        return buyer_address;
    }
    
    public void setBuyer_address (String buyer_address) {
        this.buyer_address = buyer_address;
    }
}
