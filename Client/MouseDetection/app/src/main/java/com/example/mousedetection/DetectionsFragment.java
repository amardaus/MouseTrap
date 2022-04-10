package com.example.mousedetection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mousedetection.databinding.FragmentDetectionsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetectionsFragment extends Fragment {

    private FragmentDetectionsBinding binding;

    TextView resultsTextView;
    String myURL = Constants.serverAddress + Constants.endpointGetAll;
    ProgressDialog progressDialog;
    DetectionAdapter detectionAdapter;
    ListView listView;

    public class AsyncTaskFetchAll extends AsyncTask<String,String,String> {

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

                    String datetime = obj.getString("datetime");
                    SimpleDateFormat formatter =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    SimpleDateFormat dateFormatter =
                            new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat timeFormatter =
                            new SimpleDateFormat("HH:mm:ss");
                    try {
                        Date date = formatter.parse(datetime);
                        arrDate.add(dateFormatter.format(date));
                        arrTime.add(timeFormatter.format(date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
            Bundle savedInstanceState) {
        binding = FragmentDetectionsBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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