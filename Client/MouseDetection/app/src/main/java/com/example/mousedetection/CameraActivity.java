package com.example.mousedetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

public class CameraActivity extends AppCompatActivity {
    SharedPreferences pref;
    String videoURL;
    MjpegSurfaceView mjpegView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        String server_ip = pref.getString("server_ip", "127.0.0.1");
        String server_port = pref.getString("camera_port", "8085");
        videoURL = Constants.getURL(server_ip,server_port);

        //videoURL = pref.getString("camera_addr", "http://192.168.134.156:8081");
        Log.d("VID", videoURL);

        mjpegView = findViewById(R.id.video_feed);

        int TIMEOUT = 5; //seconds
        Mjpeg.newInstance()
                .open(videoURL, TIMEOUT)
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                },throwable -> {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public void onBackPressed(){
        mjpegView.stopPlayback();
        mjpegView.clearStream();
        super.onBackPressed();
    }
}