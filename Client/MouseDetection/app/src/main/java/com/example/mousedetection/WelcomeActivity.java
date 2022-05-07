package com.example.mousedetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class WelcomeActivity extends AppCompatActivity {
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getSupportActionBar().setTitle(R.string.title_activity_welcome);

        btn = findViewById(R.id.welcome_ok_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();

                EditText edit_ip = findViewById(R.id.edit_ip);
                EditText edit_port = findViewById(R.id.edit_port);
                EditText edit_camera_port = findViewById(R.id.edit_camera_port);

                String serverIP = edit_ip.getText().toString();
                String serverPort = edit_port.getText().toString();
                String cameraPort = edit_camera_port.getText().toString();

                editor.putString("server_ip", serverIP);
                editor.putString("server_port", serverPort);
                editor.putString("camera_port", cameraPort);

                editor.apply();
                finish();
            }
        });
    }
}