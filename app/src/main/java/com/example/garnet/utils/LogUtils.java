package com.example.garnet.utils;

import android.util.Log;

public class LogUtils {
    private static final String TAG_SHARE = "SHARE";
    private static final String TAG_WEB = "WEB";

    public static void logShare(String msg){
        Log.d(TAG_SHARE, msg);
    }

    public static void logWeb(String msg){Log.d(TAG_WEB, msg);}

}
