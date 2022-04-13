package com.example.mousedetection;

public class Detection {
    private String img;
    private String date;
    private String time;
    private boolean verified;

    public Detection(String img, String date, String time, boolean verified){
        this.img = img;
        this.date = date;
        this.time = time;
        this.verified = verified;
    }

    public void setImg(String imgURL){
        this.img = img;
    }
    public String getImg(){
        return img;
    }
    public void setDate(String date){
        this.date = date;
    }
    public String getDate(){
        return date;
    }
    public void setTime(String time){
        this.time = time;
    }
    public String getTime(){
        return time;
    }
    public void verify(boolean verified){
        this.verified = verified;
    }
    public boolean ifVerified(){
        return verified;
    }
}
