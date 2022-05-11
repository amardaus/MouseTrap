package com.example.mousedetection;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.example.mousedetection.databinding.FragmentDetectionsBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DetectionsFragment extends Fragment {

    private FragmentDetectionsBinding binding;

    TextView resultsTextView;
    String myURL;
    DetectionAdapter detectionAdapter;
    ListView listView;
    SharedPreferences pref;

    public class AsyncTaskFetchAll extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try{
                URL url;
                HttpURLConnection connection = null;
                try{
                    url = new URL(myURL);
                    connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                    int data = inputStreamReader.read();

                    while(data != -1){
                        result += (char) data;
                        data = inputStreamReader.read();
                    }
                    return result;
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
                return "Exception: " + e.getMessage();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s){
            try{
                JSONArray jsonArray = new JSONArray(s);

                ArrayList<Detection> detectionList = new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject detectionJSON = jsonArray.getJSONObject(i);

                    Integer id = detectionJSON.getInt("id");
                    String datetime = detectionJSON.getString("datetime");
                    String img = detectionJSON.getString("img");
                    boolean isVerified = detectionJSON.getBoolean("verified");

                    try {
                        Date dateAndTime = MyDateFormatter.baseFormatter.parse(datetime);
                        String date = MyDateFormatter.dateFormatter.format(dateAndTime);
                        String date_extended = MyDateFormatter.dateExtendedFormatter.format(dateAndTime);
                        String time = MyDateFormatter.timeFormatter.format(dateAndTime);

                        detectionList.add(new Detection(id,date,date_extended,time,img,isVerified));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                detectionAdapter = new DetectionAdapter(getContext(), detectionList);
                listView = (ListView) getView().findViewById(R.id.listView);
                listView.setAdapter(detectionAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        verifyDetection(detectionList.get(i).getID());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("detection", detectionList.get(i));
                        NavHostFragment.findNavController(DetectionsFragment.this)
                                .navigate(R.id.action_DetectionsFragment_to_DetailsFragment, bundle);
                    }
                });
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    class VerifyDetectionTask extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                int id = Integer.parseInt(strings[0]);
                String server_ip = pref.getString("server_ip", "127.0.0.1");
                String server_port = pref.getString("server_port", "5000");
                URL url = new URL(Constants.getURL(server_ip,server_port)
                        + Constants.endpointVerify + id);
                Log.d("verify detection url: ", url.toString());
                HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d("verify detection url: ", String.valueOf(connection.getResponseCode()));
                Log.d("verify detection url: ", String.valueOf(connection.getContent()));
                connection.disconnect();

            } catch (MalformedURLException e) {
                Log.d("ERR: ", "1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("ERR: ", "2");
                e.printStackTrace();
            }
            return "";
        }
    }

    private void verifyDetection(Integer detectionID){
        VerifyDetectionTask verifyDetectionTask = new VerifyDetectionTask();
        verifyDetectionTask.execute(String.valueOf(detectionID));
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentDetectionsBinding.inflate(inflater, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String server_ip = pref.getString("server_ip", "127.0.0.1");
        String server_port = pref.getString("server_port", "5000");
        myURL = Constants.getURL(server_ip,server_port) + Constants.endpointGetAll;

        resultsTextView = (TextView) getView().findViewById(R.id.results_textview);
        AsyncTaskFetchAll fetchAll = new AsyncTaskFetchAll();
        fetchAll.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}