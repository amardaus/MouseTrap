package com.example.mousedetection;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;

@SuppressLint("SimpleDateFormat")
public  class MyDateFormatter {
    static SimpleDateFormat baseFormatter;
    static SimpleDateFormat dateFormatter;
    static SimpleDateFormat dateExtendedFormatter;
    static SimpleDateFormat timeFormatter;

    static {
        baseFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateExtendedFormatter = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        timeFormatter = new SimpleDateFormat("HH:mm:ss");
    }
}
