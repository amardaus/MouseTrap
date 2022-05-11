package com.example.mousedetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

public class CameraActivity extends AppCompatActivity {
    SharedPreferences pref;
    String videoURL;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        getSupportActionBar().setTitle(R.string.title_activity_camera);
        String server_ip = pref.getString("server_ip", "127.0.0.1");
        String server_port = pref.getString("camera_port", "8085");

        videoURL = Constants.getURL(server_ip, server_port);
        //videoURL = pref.getString("camera_addr", "http://192.168.134.156:8081");
        videoURL = "http://192.168.112.122:8081/";
        Log.d("VID", videoURL);

        webView = findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(videoURL);
    }

    @Override
    public void onBackPressed(){
        webView.stopLoading();
        webView.loadUrl("about:blank");
        super.onBackPressed();
    }
}