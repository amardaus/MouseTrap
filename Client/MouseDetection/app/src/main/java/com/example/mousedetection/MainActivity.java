package com.example.mousedetection;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

import com.example.mousedetection.databinding.ActivityMainBinding;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        if(getIntent().getExtras() != null){
            for(String key: getIntent().getExtras().keySet()){
                Object value = getIntent().getExtras().get(key);
                Log.d("EXTRAS ::::: ", "key:  " + key + " value: " + value);
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String channel_id = getString(R.string.notification_channel_id);
            String channel_name = getString(R.string.notification_channel_name);
            String channel_description = getString(R.string.notification_channel_description);

            //NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);

            NotificationChannel notificationChannel = new NotificationChannel(channel_id, channel_name, importance);
            notificationChannel.setDescription(channel_description);
            notificationChannel.setLightColor(Color.RED);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        //MyNotificationManager.getInstance(this).displayNotification("uytgrfedwsert", "qwertyui");
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstOpened = pref.getBoolean("first_opened", true);
        if(firstOpened){
            //SharedPreferences.Editor editor = pref.edit();
            //editor.putBoolean("first_opened", false);
            //editor.apply();
            // TO ODKOMENTOWAC ZEBY TYLKO ZA PIERWSZYM RAZEM SIE WYSWIETLALO!

            showWelcomeScreen();
        }
    }

    public void showWelcomeScreen(){
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}