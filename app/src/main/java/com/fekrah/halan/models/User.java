package com.fekrah.halan.models;

import java.io.Serializable;

/**
 * Created by admin on 6/19/2017.
 */

public class User implements Serializable {

    private String name;
    private String email;
    private String img;
    private String mobile;
    private String device_token;
    private String user_key;

    public User() {
    }

    public User(String name, String email, String img, String mobile, String device_token, String user_key) {
        this.name = name;
        this.email = email;
        this.img = img;
        this.mobile = mobile;
        this.device_token = device_token;
        this.user_key = user_key;
    }

    public User(String name, String img, String user_key) {
        this.name = name;
        this.img = img;
        this.user_key = user_key;
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
}