package com.example.mousedetection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.net.URL;
import com.example.mousedetection.databinding.FragmentCameraBinding;
import java.net.HttpURLConnection;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;

    private static final String TAG = "MjpegActivity";

    private MjpegView mv;
    // Physical display width and height.
    private static int displayWidth = 0;
    private static int displayHeight = 0;
    String URL = Constants.serverAddress + Constants.endPointCamera;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCameraBinding.inflate(inflater, container, false);

        /*ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.video_view, container, false);
        mv = (MjpegView) getView().findViewById(R.id.surfaceView);
        mv = (MjpegView) viewGroup.findViewById(R.id.surfaceView);
        new DoRead().execute(URL);
        return viewGroup;*/
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mv = (MjpegView) getView().findViewById(R.id.surfaceView);
        //new DoRead().execute(URL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void onPause(){
        super.onPause();
        mv.stopPlayback();
    }

    public void onResume(){
        super.onResume();
    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... Url) {
            //TODO: if camera has authentication deal with it and don't just not work

//            HttpResponse res = null;
//            DefaultHttpClient httpclient = new DefaultHttpClient();
            Log.d(TAG, "1. Sending http request");
            try {
                java.net.URL url = new URL(Url[0]); // here is your URL path
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                int responseCode=conn.getResponseCode();

//                res = httpclient.execute(new HttpGet(URI.create(url[0])));
//                Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                Log.d(TAG, "2. Request finished, status = " + responseCode);
                if(responseCode==401){
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(conn.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Request failed-ClientProtocolException", e);
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            if(result != null) {
                mv.setSource(result);
                mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
                mv.showFps(true);
            }
        }
    }
}