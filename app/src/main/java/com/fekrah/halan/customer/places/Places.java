package com.fekrah.halan.customer.places;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Places implements Serializable,Comparable {

    @SerializedName("next_page_token")
    @Expose
    private String next_page_token;

    @SerializedName("results")
    @Expose
    private List<Results> results = new ArrayList<Results>();

    @SerializedName("status")
    @Expose
    private String status;


    public Places() {
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return 0;
    }
}
