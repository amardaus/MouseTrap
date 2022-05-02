package com.example.mousedetection;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String token){
        super.onNewToken(token);
        Log.e("Refreshed token: ", token);
        sendTokenToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        super.onMessageReceived(remoteMessage);
        Log.d("onmessagereceived: ", remoteMessage.getNotification().getBody());
    }

    private void sendTokenToServer(String token){
        Constants.token = token;
        try {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String server_ip = pref.getString("server_ip", "127.0.0.1");
            String server_port = pref.getString("server_port", "5000");
            URL url = new URL(Constants.getURL(server_ip,server_port)
                    + Constants.endpointChangeToken + Constants.token);
            HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            Log.d("sent", String.valueOf(connection.getResponseCode()));
            Log.d("sent", String.valueOf(url));
            connection.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
