package com.fekrah.halan.customer.places;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Geometry implements Serializable {

    @SerializedName("location")
    @Expose
    private ResultLocation location;

    public Geometry() {
    }

    public ResultLocation getResultLocation() {
        return location;
    }

    public void setResultLocation(ResultLocation resultLocation) {
        this.location = resultLocation;
    }


}
