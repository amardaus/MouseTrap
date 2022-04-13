package com.example.mousedetection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mousedetection.databinding.FragmentDetailsBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

public class DetailsFragment extends Fragment {

    private FragmentDetailsBinding binding;

    TextView detailsDateTextView, detailsTimeTextView;
    String detectionID = "";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailsDateTextView = (TextView) getView().findViewById(R.id.details_date);
        detailsTimeTextView = (TextView) getView().findViewById(R.id.details_time);

        String detection = getArguments().getString("detection");
        try {
            JSONObject detectionJSON = new JSONObject(detection);
            String datetime = detectionJSON.getString("datetime");

            try {
                Date date = MyDateFormatter.baseFormatter.parse(datetime);
                detailsDateTextView.setText(MyDateFormatter.dateFormatter.format(date));
                detailsTimeTextView.setText(MyDateFormatter.timeFormatter.format(date));

                ImageView imageView = (ImageView) getView().findViewById(R.id.details_image);
                String url = detectionJSON.getString("img");
                Log.d("uuu", url);
                loadImage(imageView, url);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Button verifyBtn = (Button) getView().findViewById(R.id.btn_verify);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDetection(detectionID);
            }
        });

        Button ignoreBtn = (Button) getView().findViewById(R.id.btn_ignore);
        ignoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(DetailsFragment.this).navigate(R.id.action_DetailsFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    class VerifyDetectionTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                int id = Integer.valueOf(strings[0]);
                //int id = Integer.parseInt(sid);
                Log.d("IDDDD: ", String.valueOf(id));
                URL url = new URL(Constants.serverAddress
                        + Constants.endpointVerify + id);
                Log.d("IDDDD: ", url.toString());
                HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d("CONN", String.valueOf(connection.getResponseMessage()));
                connection.disconnect();
                Log.d("CONN", url.toString());

                //  The application may be doing too much work on its main thread ???
            } catch (MalformedURLException e) {
                Log.d("ERR: ", "1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("ERR: ", "2");
                e.printStackTrace();
            }
            getFragmentManager().popBackStack();
            return "";
        }
    }

    class LoadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView imageView;

        public LoadImageTask(ImageView imView){
            this.imageView = imView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            String url = urls[0];

            try {
                InputStream is = (InputStream) new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    private void verifyDetection(String sid){
        VerifyDetectionTask verifyDetectionTask = new VerifyDetectionTask();
        verifyDetectionTask.execute(sid);
    }

    private void loadImage(ImageView imageView, String url){
        LoadImageTask loadImageTask = new LoadImageTask(imageView);
        loadImageTask.execute(url);
    }
}