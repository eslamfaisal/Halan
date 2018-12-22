package com.fekrah.halan.models;

public class JobInformation {

    private String category;
    private String job_name;
    private String years_experience;
    private String job_type;

    public JobInformation() {
    }

    public JobInformation(String category, String job_name,
                          String years_experience, String job_type) {
        this.category = category;
        this.job_name = job_name;
        this.years_experience = years_experience;
        this.job_type = job_type;

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getYears_experience() {
        return years_experience;
    }

    public void setYears_experience(String years_experience) {
        this.years_experience = years_experience;
    }

    public String getJob_type() {
        return job_type;
    }

    public void setJob_type(String job_type) {
        this.job_type = job_type;
    }


}
