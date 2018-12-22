package com.fekrah.halan.models;

public  class Skill {

    private String skill_name;
    private String skill_key;
    public Skill() {
    }

    public Skill(String skill_name, String skill_key) {
        this.skill_name = skill_name;
        this.skill_key = skill_key;
    }


    public String getSkill_name() {
        return skill_name;
    }

    public void setSkill_name(String skill_name) {
        this.skill_name = skill_name;
    }

    public String getSkill_key() {
        return skill_key;
    }

    public void setSkill_key(String skill_key) {
        this.skill_key = skill_key;
    }
}