package com.fekrah.halan.models;

public class Notification {

    private String img;
    private String content;
    private String driver_name;

    public Notification(String img, String content, String driver_name) {
        this.img = img;
        this.content = content;
        this.driver_name = driver_name;
    }

    public Notification() {
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }
}
