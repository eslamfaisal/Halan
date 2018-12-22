package com.fekrah.halan.models;

public  class State {

    private String status;
    private String has_order;
    public State() {
    }

    public State(String status, String has_order) {
        this.status = status;
        this.has_order = has_order;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHas_order() {
        return has_order;
    }

    public void setHas_order(String has_order) {
        this.has_order = has_order;
    }
}