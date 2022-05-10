package com.example.mousedetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

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
        videoURL = Constants.getURL(server_ip,server_port);

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