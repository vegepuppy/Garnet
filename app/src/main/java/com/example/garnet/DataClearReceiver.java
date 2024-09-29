package com.example.garnet;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DataClearReceiver extends BroadcastReceiver {
    public static final String CLEAR_TYPE = "cleartype";
    public static final String CLEAR_DONE = "cleardone";
    public static final int DATA_CLEAR_CODE = 103;//这里是弄一个特殊的数字，避免以后的requestCode冲突
    public static final int CLEARED_NOTIFICATION_CODE = 9973;

    @Override
    public void onReceive(Context context, Intent intent) {
        String clearType = intent.getStringExtra(CLEAR_TYPE);

        if(clearType.equals(CLEAR_DONE)){
            GarnetDatabaseHelper mDatabaseHelper = new GarnetDatabaseHelper(context);
            mDatabaseHelper.deleteDone();
            // TODO: 2024-09-29 清理完之后应当发送通知，还没做

        }
    }
}