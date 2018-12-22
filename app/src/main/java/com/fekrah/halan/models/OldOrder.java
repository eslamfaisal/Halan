package com.fekrah.halan.models;

import java.io.Serializable;

public class OldOrder implements Serializable {
    private String old_order_key;
    private String driver_key;
    private String cost;
    private String receiver_location;
    private String arrival_location;
    private String distance;
    private String user_key;
    private long time;
    private String details;

    public OldOrder() {
    }

    public OldOrder(String old_order_key, String driver_key, String cost, String receiver_location, String arrival_location, String distance, String user_key, long time) {
        this.old_order_key = old_order_key;
        this.driver_key = driver_key;
        this.cost = cost;
        this.receiver_location = receiver_location;
        this.arrival_location = arrival_location;
        this.distance = distance;
        this.user_key = user_key;
        this.time = time;
    }

    public OldOrder(String old_order_key, String driver_key, String cost, String receiver_location, String arrival_location, String distance, String user_key, long time, String details) {
        this.old_order_key = old_order_key;
        this.driver_key = driver_key;
        this.cost = cost;
        this.receiver_location = receiver_location;
        this.arrival_location = arrival_location;
        this.distance = distance;
        this.user_key = user_key;
        this.time = time;
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getOld_order_key() {
        return old_order_key;
    }

    public void setOld_order_key(String old_order_key) {
        this.old_order_key = old_order_key;
    }

    public String getDriver_key() {
        return driver_key;
    }

    public void setDriver_key(String driver_key) {
        this.driver_key = driver_key;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getReceiver_location() {
        return receiver_location;
    }

    public void setReceiver_location(String receiver_location) {
        this.receiver_location = receiver_location;
    }

    public String getArrival_location() {
        return arrival_location;
    }

    public void setArrival_location(String arrival_location) {
        this.arrival_location = arrival_location;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
