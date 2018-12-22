package com.fekrah.halan.models;

import java.io.Serializable;

public class Search implements Serializable {

    private String category;
    private String jobType;
    private String experiences;
    private String status;
    private String country;
    private String town;

    public Search() {
    }


    public Search(String category, String jobType, String experiences, String status, String country, String town) {
        this.category = category;
        this.jobType = jobType;
        this.experiences = experiences;
        this.status = status;
        this.country = country;
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }


    public String getExperiences() {
        return experiences;
    }

    public void setExperiences(String experiences) {
        this.experiences = experiences;
    }

}
