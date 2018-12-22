package com.fekrah.halan.models;

import java.io.Serializable;

public class Order implements Serializable {

    private String order_key;
    private String cost;
    private String receiver_location;
    private String arrival_location;
    private String distance;
    private String details;
    private String user_key;
    private double r_l_lat;
    private double r_l_lng;
    private double a_l_lat;
    private double a_l_lng;
    private String name;
    private String image;
    private long created_at;
    private String rout_time;

    public Order() {
    }

    public Order(String order_key, String cost, String receiver_location, String arrival_location, String distance, String details, String user_key, double r_l_lat, double r_l_lng, double a_l_lat, double a_l_lng, String name, String image, long created_at, String rout_time) {
        this.order_key = order_key;
        this.cost = cost;
        this.receiver_location = receiver_location;
        this.arrival_location = arrival_location;
        this.distance = distance;
        this.details = details;
        this.user_key = user_key;
        this.r_l_lat = r_l_lat;
        this.r_l_lng = r_l_lng;
        this.a_l_lat = a_l_lat;
        this.a_l_lng = a_l_lng;
        this.name = name;
        this.image = image;
        this.created_at = created_at;
        this.rout_time = rout_time;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getR_l_lat() {
        return r_l_lat;
    }

    public void setR_l_lat(double r_l_lat) {
        this.r_l_lat = r_l_lat;
    }

    public double getR_l_lng() {
        return r_l_lng;
    }

    public void setR_l_lng(double r_l_lng) {
        this.r_l_lng = r_l_lng;
    }

    public double getA_l_lat() {
        return a_l_lat;
    }

    public void setA_l_lat(double a_l_lat) {
        this.a_l_lat = a_l_lat;
    }

    public double getA_l_lng() {
        return a_l_lng;
    }

    public void setA_l_lng(double a_l_lng) {
        this.a_l_lng = a_l_lng;
    }

    public String getOrder_key() {
        return order_key;
    }

    public void setOrder_key(String order_key) {
        this.order_key = order_key;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }


    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    public String getRout_time() {
        return rout_time;
    }

    public void setRout_time(String rout_time) {
        this.rout_time = rout_time;
    }
}
