package com.example.mousedetection;

import android.content.SharedPreferences;
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
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.mousedetection.databinding.FragmentDetailsBinding;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DetailsFragment extends Fragment {
    private FragmentDetailsBinding binding;
    TextView detailsDateTextView, detailsTimeTextView;
    String detectionID = "";
    SharedPreferences pref;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        detailsDateTextView = (TextView) getView().findViewById(R.id.details_date);
        detailsTimeTextView = (TextView) getView().findViewById(R.id.details_time);
        ImageView imageView = (ImageView) getView().findViewById(R.id.details_image);

        Detection detection = getArguments().getParcelable("detection");
        detectionID = detection.getID().toString();

        loadImage(imageView, detection.getID().toString());
        detailsDateTextView.setText(detection.getDate());
        detailsTimeTextView.setText(detection.getTime());

        Button verifyBtn = (Button) getView().findViewById(R.id.btn_verify);
        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyDetection(detectionID);
                // dopisać żeby wracało do głównego ekranu
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
                String server_ip = pref.getString("server_ip", "127.0.0.1");
                String server_port = pref.getString("server_port", "5000");
                URL url = new URL(Constants.getURL(server_ip,server_port)
                        + Constants.endpointVerify + id);
                Log.d("IDDDD: ", url.toString());
                HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d("CONN", String.valueOf(connection.getResponseMessage()));
                connection.disconnect();
                Log.d("CONN", url.toString());

            } catch (MalformedURLException e) {
                Log.d("ERR: ", "1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("ERR: ", "2");
                e.printStackTrace();
            }
            //getActivity().getSupportFragmentManager().popBackStack();
            // TO CHYBA PSUJE?

            return "";
        }
    }

    class LoadImageTask extends AsyncTask<String, Void, Bitmap>{
        ImageView imageView;

        public LoadImageTask(ImageView imView){
            this.imageView = imView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;

            int id = Integer.valueOf(strings[0]);
            String server_ip = pref.getString("server_ip", "127.0.0.1");
            String server_port = pref.getString("server_port", "5000");
            String url = Constants.getURL(server_ip,server_port) + Constants.endpointImage + id;

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

    private void loadImage(ImageView imageView, String sid){
        LoadImageTask loadImageTask = new LoadImageTask(imageView);
        loadImageTask.execute(sid);
    }
}