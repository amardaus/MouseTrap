package com.example.mousedetection;

import android.os.Parcel;
import android.os.Parcelable;

public class Detection implements Parcelable {
    private Integer id;
    private String date;
    private String img;
    private String time;
    private boolean verified;

    public Detection(Integer id, String date, String time, String img, boolean verified){
        this.id = id;
        this.date = date;
        this.time = time;
        this.img = img;
        this.verified = verified;
    }

    public Detection(Parcel parcel){
        this.id = parcel.readInt();
        this.date = parcel.readString();
        this.time = parcel.readString();
        this.img = parcel.readString();
        this.verified = parcel.readInt() != 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        @Override
        public Detection createFromParcel(Parcel parcel) {
            return new Detection(parcel);
        }

        @Override
        public Detection[] newArray(int size) {
            return new Detection[size];
        }
    };

    public void setID(String date){
        this.id = id;
    }
    public Integer getID(){
        return id;
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
    public void setImg(String imgURL){
        this.img = img;
    }
    public String getImg(){
        return img;
    }
    public void verify(boolean verified){
        this.verified = verified;
    }
    public boolean ifVerified(){
        return verified;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.id);
        parcel.writeString(this.date);
        parcel.writeString(this.time);
        parcel.writeString(this.img);
        parcel.writeInt(this.verified ? 1 : 0);
    }
}
