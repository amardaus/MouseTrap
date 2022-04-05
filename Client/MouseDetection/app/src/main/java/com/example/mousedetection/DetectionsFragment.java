package com.example.mousedetection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mousedetection.databinding.FragmentEventsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetectionsFragment extends Fragment {

    private FragmentEventsBinding binding;

    TextView resultsTextView;
    String myURL = "http://10.0.2.2:5000/get_all";
    ProgressDialog progressDialog;
    DetectionAdapter detectionAdapter;
    ListView listView;

    public class AsyncTaskFetch extends AsyncTask<String,String,String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching data...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            try{
                java.net.URL url;
                HttpURLConnection urlConnection = null;
                try{
                    url = new URL(myURL);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
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
                    if (urlConnection != null) {
                        urlConnection.disconnect();
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
            progressDialog.dismiss();
            try{
                JSONArray jsonArray = new JSONArray(s);

                /*
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray1 = jsonObject.getJSONArray("users");
                JSONObject jsonObject1 =jsonArray1.getJSONObject(index_no);
                String id = jsonObject1.getString("id");
                String name = jsonObject1.getString("name");
                String my_users = "User ID: "+id+"\n"+"Name: "+name;
                */

                ArrayList<Integer> arrImg = new ArrayList<>();
                ArrayList<String> arrDate = new ArrayList<>();
                ArrayList<String> arrTime = new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++){
                    arrImg.add(R.drawable.alert_red);
                    JSONObject obj = jsonArray.getJSONObject(i);
                    arrDate.add(obj.getString("date"));
                    arrTime.add(obj.getString("time"));
                }



                ArrayList<Detection> detectionList = new ArrayList<>();

                for(int i = 0; i < arrImg.size(); i++){
                    detectionList.add(new Detection(arrImg.get(i),arrDate.get(i),arrTime.get(i)));
                }

                detectionAdapter = new DetectionAdapter(getContext(), detectionList);
                listView = (ListView) getView().findViewById(R.id.listView);
                listView.setAdapter(detectionAdapter);



                //resultsTextView.setVisibility(View.VISIBLE);
                //resultsTextView.setText(output);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEventsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resultsTextView = (TextView) getView().findViewById(R.id.results_textview);

        AsyncTaskFetch myAsync = new AsyncTaskFetch();
        myAsync.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}