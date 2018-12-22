package com.fekrah.halan.models;

public class Room {

    private String img;
    private String last_message;
    private long time;
    private String room_key;
    private String receiver_name;
    private String from;

    public Room(String img, String last_message, long time, String room_key, String receiver_name) {
        this.img = img;
        this.last_message = last_message;
        this.time = time;
        this.room_key = room_key;
        this.receiver_name = receiver_name;
    }

    public Room(String img, String last_message, long time, String room_key, String receiver_name, String from) {
        this.img = img;
        this.last_message = last_message;
        this.time = time;
        this.room_key = room_key;
        this.receiver_name = receiver_name;
        this.from = from;
    }

    public Room() {
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getRoom_key() {
        return room_key;
    }

    public void setRoom_key(String room_key) {
        this.room_key = room_key;
    }


    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }
}
