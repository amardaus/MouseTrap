package com.example.mousedetection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.mousedetection.databinding.FragmentFirstBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;
    SharedPreferences pref;

    ImageView alertImage;
    TextView alertText;
    CircularProgressIndicator progressIndicator;
    LinearLayout linearLayout;
    TextView settingsLink;

    public class AsyncTaskFetchLast extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";

            URL url;
            HttpURLConnection connection = null;
            try {
                String server_ip = pref.getString("server_ip", "127.0.0.1");
                String server_port = pref.getString("server_port", "5000");
                url = new URL(Constants.getURL(server_ip,server_port) + Constants.endpointGetLast);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();
                while(data != -1){
                    result += (char) data;
                    data = inputStreamReader.read();
                }
                return result;
            }
            catch (SocketTimeoutException e){
                Log.d("exception", "SocketTimeoutException");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(connection != null){
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            alertImage.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.GONE);

            Integer id = null;
            String date = null;
            String date_extended = null;
            String time = null;
            String img = null;
            boolean isVerified = false;

            if(s.equals("{}") || s.equals("{}\n")){
                settingsLink.setVisibility(View.GONE);
                alertImage.setImageResource(R.drawable.eyes);
                alertText.setText("Waiting for a mouse...");
                if(isVisible() && isResumed()) {
                    binding.buttonCamera.setEnabled(true);
                    binding.buttonDetections.setEnabled(true);
                }
            }
            else if(s.isEmpty()){
                settingsLink.setVisibility(View.VISIBLE);
                if(isVisible() && isResumed()){
                    binding.buttonCamera.setEnabled(false);
                    binding.buttonDetections.setEnabled(false);
                }
                alertImage.setImageResource(R.drawable.no_internet);
                alertText.setText("Error fetching data from server");

                if(settingsLink.getParent() != null){
                    ((ViewGroup)settingsLink.getParent()).removeView(settingsLink);
                }
                linearLayout.addView(settingsLink);
            }
            else{
                settingsLink.setVisibility(View.GONE);
                try {
                    JSONObject detectionJSON = new JSONObject(s);
                    id = detectionJSON.getInt("id");
                    String datetime = detectionJSON.getString("datetime");
                    img = detectionJSON.getString("img");
                    isVerified = detectionJSON.getBoolean("verified");
                    Date dateAndTime = null;
                    try
                    {
                        dateAndTime = MyDateFormatter.baseFormatter.parse(datetime);
                        date = MyDateFormatter.dateFormatter.format(dateAndTime);
                        date_extended = MyDateFormatter.dateExtendedFormatter.format(dateAndTime);
                        time = MyDateFormatter.timeFormatter.format(dateAndTime);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                alertImage.setImageResource(R.drawable.detection_alert);
                alertImage.setVisibility(View.VISIBLE);
                alertText.setText("MOUSE DETECTED!!");
                progressIndicator.setVisibility(View.GONE);

                if(isVisible() && isResumed()) {
                    binding.buttonCamera.setEnabled(true);
                    binding.buttonDetections.setEnabled(true);
                }

                Detection detection = new Detection(id, date, date_extended, time, img, isVerified);

                alertText.setClickable(true);
                alertText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("detection",detection);
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_DetailsFragment, bundle);
                    }
                });

                alertImage.setClickable(true);
                alertImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("detection",detection);
                        NavHostFragment.findNavController(FirstFragment.this)
                                .navigate(R.id.action_FirstFragment_to_DetailsFragment, bundle);
                    }
                });
            }
        }
    }

    private void setAsyncTask(){
        final Handler handler = new Handler();
        Timer timer =  new Timer();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        AsyncTaskFetchLast asyncTask = new AsyncTaskFetchLast();
                        asyncTask.execute();
                    }
                });
            }
        };

        int seconds = 10;
        timer.schedule(timerTask, 0, 1000*seconds);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CameraActivity.class);
                startActivity(intent);
            }
        });

        binding.buttonDetections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_DetectionsFragment);
            }
        });

        alertImage = (ImageView) getView().findViewById(R.id.alertImg);
        alertText = (TextView) getView().findViewById(R.id.alertText);
        progressIndicator = (CircularProgressIndicator) getView().findViewById(R.id.progress_circular);

        linearLayout = getView().findViewById(R.id.linearLayout);
        settingsLink = new TextView(getContext());
        settingsLink.setTextSize(20);
        settingsLink.setPaintFlags(settingsLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        settingsLink.setTextColor(getResources().getColor(R.color.light_blue_900));
        settingsLink.setText("Change server IP");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20,20,20,20);
        settingsLink.setLayoutParams(params);
        settingsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        setAsyncTask();
    }
}