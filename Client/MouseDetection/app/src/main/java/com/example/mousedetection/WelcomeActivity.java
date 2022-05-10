package com.example.mousedetection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class WelcomeActivity extends AppCompatActivity {
    Button btn;

    public class AsyncTaskSendToken extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String serverIP = strings[0];
                String serverPort = strings[1];
                String token = strings[2];
                Log.d(serverIP, serverPort + token);
                URL url = new URL(Constants.getURL(serverIP,serverPort)
                        + Constants.endpointChangeToken + token);
                HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d("sent", String.valueOf(connection.getResponseCode()));
                Log.d("sent", String.valueOf(url));
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

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

                String token = pref.getString("token", "");
                AsyncTaskSendToken sendToken = new AsyncTaskSendToken();
                sendToken.execute(serverIP,serverPort,token);
            }
        });
    }
}