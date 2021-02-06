package com.example.heartbeat;

public class Users {

    String bpm;
    String condition;
    String time;
    String name;

    public Users(){}
    public Users(String bpm, String condition, String time) {
        this.bpm = bpm;
        this.condition = condition;
        this.time = time;
    }

    public String getBpm() {
        return bpm;
    }

    public void setBpm(String bpm) {
        this.bpm = bpm;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}