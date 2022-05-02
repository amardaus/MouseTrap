package com.example.mousedetection;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.net.URL;
import com.example.mousedetection.databinding.FragmentCameraBinding;
import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.github.niqdev.mjpeg.MjpegSurfaceView;

public class CameraFragment extends Fragment {

    private FragmentCameraBinding binding;
    SharedPreferences pref;
    String videoURL;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCameraBinding.inflate(inflater, container, false);
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String server_ip = pref.getString("server_ip", "127.0.0.1");
        String server_port = pref.getString("server_port", "5000");
        videoURL = Constants.getURL(server_ip,server_port) + Constants.endPointCamera;

        int TIMEOUT = 5; //seconds

        MjpegSurfaceView mjpegView = getView().findViewById(R.id.VIEW_NAME);
        Mjpeg.newInstance()
                .open(videoURL, TIMEOUT)
                .subscribe(inputStream -> {
                    mjpegView.setSource(inputStream);
                    mjpegView.setDisplayMode(DisplayMode.BEST_FIT);
                    mjpegView.showFps(true);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}