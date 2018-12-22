package com.fekrah.halan.models;

public class Offer {

    private String cost;
    private String time;
    private String driver_key;
    private String driver_name;
    private String driver_img;
    private String device_token;
    private int rating_count;
    private float rating;

    public Offer() {

    }

    public Offer(String cost, String time, String driver_key) {
        this.cost = cost;
        this.time = time;
        this.driver_key = driver_key;
    }

    public Offer(String cost, String time, String driver_key, String driver_name,
                 String driver_img, String device_token, int rating_count, float rating) {
        this.cost = cost;
        this.time = time;
        this.driver_key = driver_key;
        this.driver_name = driver_name;
        this.driver_img = driver_img;
        this.device_token = device_token;
        this.rating_count = rating_count;
        this.rating = rating;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_img() {
        return driver_img;
    }

    public void setDriver_img(String driver_img) {
        this.driver_img = driver_img;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public int getRating_count() {
        return rating_count;
    }

    public void setRating_count(int rating_count) {
        this.rating_count = rating_count;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDriver_key() {
        return driver_key;
    }

    public void setDriver_key(String driver_key) {
        this.driver_key = driver_key;
    }
}
