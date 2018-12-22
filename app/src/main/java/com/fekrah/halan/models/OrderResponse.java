package com.fekrah.halan.models;

import java.io.Serializable;

public class OrderResponse  implements Serializable {

    private String driver_name;
    private String Response_key;
    private String distance;
    private String driver_key;
    private String driver_image;
    private String edtimated_time;


    public OrderResponse() {
    }

    public OrderResponse(String driver_name, String response_key, String distance, String driver_key, String driver_image, String edtimated_time) {
        this.driver_name = driver_name;
        Response_key = response_key;
        this.distance = distance;
        this.driver_key = driver_key;
        this.driver_image = driver_image;
        this.edtimated_time = edtimated_time;
    }

    public String getEdtimated_time() {
        return edtimated_time;
    }

    public void setEdtimated_time(String edtimated_time) {
        this.edtimated_time = edtimated_time;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getResponse_key() {
        return Response_key;
    }

    public void setResponse_key(String response_key) {
        Response_key = response_key;
    }


    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDriver_key() {
        return driver_key;
    }

    public void setDriver_key(String driver_key) {
        this.driver_key = driver_key;
    }

    public String getDriver_image() {
        return driver_image;
    }

    public void setDriver_image(String driver_image) {
        this.driver_image = driver_image;
    }

}
