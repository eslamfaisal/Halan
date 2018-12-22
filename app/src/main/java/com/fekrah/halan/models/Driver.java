package com.fekrah.halan.models;

import java.io.Serializable;

public class Driver implements Serializable {

    private String name;
    private String email;
    private String img;
    private String mobile;
    private String device_token;
    private String user_key;
    private String city;
    private String car_color;
    private String car_model;
    private String car_form_img;
    private String car_license_img;
    private String car_front_img;
    private String car_back_img;
    private String national_id;
    private String car_id;
    private int available_balance;
    private int taken_balance;

    public Driver(String name, String email, String img, String mobile, String device_token, String user_key, String city, String car_color, String car_model, String car_form_img, String car_license_img, String car_front_img, String car_back_img, String national_id, String car_id, int available_balance, int taken_balance) {
        this.name = name;
        this.email = email;
        this.img = img;
        this.mobile = mobile;
        this.device_token = device_token;
        this.user_key = user_key;
        this.city = city;
        this.car_color = car_color;
        this.car_model = car_model;
        this.car_form_img = car_form_img;
        this.car_license_img = car_license_img;
        this.car_front_img = car_front_img;
        this.car_back_img = car_back_img;
        this.national_id = national_id;
        this.car_id = car_id;
        this.available_balance = available_balance;
        this.taken_balance = taken_balance;
    }

    public Driver() {
    }


    public int getAvailable_balance() {
        return available_balance;
    }

    public void setAvailable_balance(int available_balance) {
        this.available_balance = available_balance;
    }

    public int getTaken_balance() {
        return taken_balance;
    }

    public void setTaken_balance(int taken_balance) {
        this.taken_balance = taken_balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getUser_key() {
        return user_key;
    }

    public void setUser_key(String user_key) {
        this.user_key = user_key;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCar_color() {
        return car_color;
    }

    public void setCar_color(String car_color) {
        this.car_color = car_color;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_form_img() {
        return car_form_img;
    }

    public void setCar_form_img(String car_form_img) {
        this.car_form_img = car_form_img;
    }

    public String getCar_license_img() {
        return car_license_img;
    }

    public void setCar_license_img(String car_license_img) {
        this.car_license_img = car_license_img;
    }

    public String getCar_front_img() {
        return car_front_img;
    }

    public void setCar_front_img(String car_front_img) {
        this.car_front_img = car_front_img;
    }

    public String getCar_back_img() {
        return car_back_img;
    }

    public void setCar_back_img(String car_back_img) {
        this.car_back_img = car_back_img;
    }

    public String getNational_id() {
        return national_id;
    }

    public void setNational_id(String national_id) {
        this.national_id = national_id;
    }

    public String getCar_id() {
        return car_id;
    }

    public void setCar_id(String car_id) {
        this.car_id = car_id;
    }
}
