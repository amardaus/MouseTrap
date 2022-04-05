package com.example.mousedetection;

public class Detection {
    private int img;
    private String date;
    private String time;

    public Detection(int img, String date, String time){
        this.img = img;
        this.date = date;
        this.time = time;
    }

    public void setImg(int img){
        this.img = img;
    }
    public int getImg(){
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
}
