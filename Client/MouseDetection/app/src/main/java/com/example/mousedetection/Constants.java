package com.example.mousedetection;

public class Constants {
    //public static String username = "User";
    //public static String serverAddress = "http://10.0.2.2:5000";
    public static String cameraPort = "8081";

    public static String endpointGetAll = "/get_all";
    public static String endpointChangeToken = "/change_token/" ;
    public static String endpointGetLast = "/get_last";
    public static String endpointVerify = "/verify/";
    public static String endpointOpenTrap = "/open_trap";
    public static String endpointImage = "/get_image/";
    public static String endpointStatus = "/0/detection/status";

    public static String getURL(String ip, String port){
        return "http://" + ip + ":" + port;
    }
    public static String getCameraURL(String ip){
        return "http://" + ip + ":" + cameraPort + "/stream.mjpeg";
    }
}
