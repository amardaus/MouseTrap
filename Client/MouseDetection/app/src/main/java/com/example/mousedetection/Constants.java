package com.example.mousedetection;

public class Constants {
    public static String username = "Kurczak";
    public static String token = "";
    public static String serverAddress = "http://10.0.2.2:5000";

    public static String endpointGetAll = "/get_all";
    public static String endpointChangeToken = "/change_token/"
                        + username + "/" + token;
    public static String endpointGetLast = "/get_last";
    public static String endpointVerify = "/verify/";
}
