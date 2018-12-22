package com.fekrah.halan.models;

public class Message {

    private String message;
    private String type;
    private long time;
    private String imageUrl;
    private String from;
    private String message_key;
    private int delivery_cost;
    private int purchases_cost;
    private int total_cost;
    public Message() {
    }

    public Message(String message, String type, long time, String imageUrl, String from, String message_key) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.imageUrl = imageUrl;
        this.from = from;
        this.message_key = message_key;
    }

    public Message(String type, long time, String imageUrl,
                   String from, String message_key, int delivery_cost,
                   int purchases_cost, int total_cost) {
        this.type = type;
        this.time = time;
        this.imageUrl = imageUrl;
        this.from = from;
        this.message_key = message_key;
        this.delivery_cost = delivery_cost;
        this.purchases_cost = purchases_cost;
        this.total_cost = total_cost;
    }

    public int getDelivery_cost() {
        return delivery_cost;
    }

    public void setDelivery_cost(int delivery_cost) {
        this.delivery_cost = delivery_cost;
    }

    public int getPurchases_cost() {
        return purchases_cost;
    }

    public void setPurchases_cost(int purchases_cost) {
        this.purchases_cost = purchases_cost;
    }

    public int getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(int total_cost) {
        this.total_cost = total_cost;
    }

    public String getMessage_key() {
        return message_key;
    }

    public void setMessage_key(String message_key) {
        this.message_key = message_key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
