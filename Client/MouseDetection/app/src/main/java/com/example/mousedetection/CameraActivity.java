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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
        videoURL = Constants.getCameraURL(server_ip);
        Log.d("VID", videoURL);

        CheckCameraTask checkCameraTask = new CheckCameraTask();
        checkCameraTask.execute();

        webView = findViewById(R.id.webview);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.loadUrl(videoURL);
        Log.d("UUUURL", videoURL);
    }

    @Override
    public void onBackPressed(){
        webView.stopLoading();
        webView.loadUrl("about:blank");
        super.onBackPressed();
    }

    class CheckCameraTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                String server_ip = pref.getString("server_ip", "127.0.0.1");
                URL url = new URL(Constants.getCameraURL(server_ip)
                        + Constants.endpointStatus);
                HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
                Log.d("response","addssd");
                String status = sb.toString();
                connection.disconnect();
                if(status.contains("ACTIVE")){
                    Log.d("response","1");
                }
                else{
                    Log.d("response","2");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("response","3");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("response","4");
            }
            Log.d("response","7");
            return "";
        }
    }
}