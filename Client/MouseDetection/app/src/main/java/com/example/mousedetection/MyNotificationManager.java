package com.example.mousedetection;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class MyNotificationManager {
    private Context myContext;
    private static MyNotificationManager notificationManager;

    MyNotificationManager(Context context){
        myContext = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context){
        if(notificationManager == null){
            notificationManager = new MyNotificationManager(context);
        }
        return notificationManager;
    }

    public void displayNotification(String title, String body) {
        NotificationCompat.Builder myBuilder = new NotificationCompat.Builder(myContext, myContext.getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.mouse_icon)
                .setContentTitle(title)
                .setContentText(body);

        Intent resultIntent = new Intent(myContext, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(myContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        myBuilder.setContentIntent(pendingIntent);

        NotificationManager myNotificationManager = (NotificationManager) myContext.getSystemService(NOTIFICATION_SERVICE);

        if (myNotificationManager != null) {
            myNotificationManager.notify(1, myBuilder.build());
        }
    }
}
