package com.example.mousedetection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.mousedetection.databinding.FragmentFirstBinding;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class FirstFragment extends Fragment {
    private FragmentFirstBinding binding;

    ImageView alertImage;
    TextView alertText;
    CircularProgressIndicator progressIndicator;

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
                url = new URL(Constants.serverAddress + Constants.endpointGetLast);
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
                Log.d("tttt", "TIMEOUT!");
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
            Detection detection = null;
            alertImage.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.GONE);

            if(s.equals("{}") || s.equals("{}\n")){
                alertImage.setImageResource(R.drawable.alert_green);
                alertText.setText("Waiting for a mouse...");
                if(isVisible() && isResumed()) {
                    binding.buttonCamera.setEnabled(true);
                    binding.buttonDetections.setEnabled(true);
                }
            }
            else if(s.isEmpty()){
                if(isVisible() && isResumed()){
                    binding.buttonCamera.setEnabled(false);
                    binding.buttonDetections.setEnabled(false);
                }
                alertImage.setImageResource(R.drawable.no_internet);
                alertText.setText("Error fetching data from server");
            }
            else{
                try {
                    JSONObject obj = new JSONObject(s);

                    String datetime = obj.getString("datetime");
                    try {
                        Date date = MyDateFormatter.baseFormatter.parse(datetime);
                        //String detectionDate = MyDateFormatter.dateFormatter.format(date);
                        //String detectionTime = MyDateFormatter.timeFormatter.format(date);
                        //detection = new Detection(123, detectionDate, detectionTime);
                        //String detectionURL = obj.getString("url");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                alertImage.setImageResource(R.drawable.alert_red);
                alertImage.setVisibility(View.VISIBLE);
                alertText.setText("MOUSE DETECTED!!");
                progressIndicator.setVisibility(View.GONE);

                if(isVisible() && isResumed()) {
                    binding.buttonCamera.setEnabled(true);
                    binding.buttonDetections.setEnabled(true);
                }

                alertText.setClickable(true);
                alertText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putString("detection", s);
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
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_CameraFragment);
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

        setAsyncTask();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}