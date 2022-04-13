package com.example.mousedetection;

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
import com.example.mousedetection.databinding.FragmentDetectionsBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DetectionsFragment extends Fragment {

    private FragmentDetectionsBinding binding;

    TextView resultsTextView;
    String myURL = Constants.serverAddress + Constants.endpointGetAll;
    DetectionAdapter detectionAdapter;
    ListView listView;

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
                ArrayList<String> arrDate = new ArrayList<>();
                ArrayList<String> arrTime = new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject detectionJSON = jsonArray.getJSONObject(i);
                    String datetime = detectionJSON.getString("datetime");
                    boolean isVerified = detectionJSON.getBoolean("verified");
                    String img = detectionJSON.getString("img");
                    Log.d("IIIIMG", img);
                    Log.d("IIIIMG", detectionJSON.toString());
                    try {
                        Date date = MyDateFormatter.baseFormatter.parse(datetime);
                        arrDate.add(MyDateFormatter.dateFormatter.format(date));
                        arrTime.add(MyDateFormatter.timeFormatter.format(date));
                        detectionList.add(new Detection(img,arrDate.get(i),arrTime.get(i),isVerified));
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
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("detection",String.valueOf(jsonArray.getJSONObject(i)));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        NavHostFragment.findNavController(DetectionsFragment.this)
                                .navigate(R.id.action_DetectionsFragment_to_DetailsFragment, bundle);
                    }
                });

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