package com.example.garnet;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DataClearReceiver extends BroadcastReceiver {
    public static final String CLEAR_TYPE = "cleartype";
    public static final String CLEAR_DONE = "cleardone";
    public static final int DATA_CLEAR_CODE = 103;//这里是弄一个特殊的数字，避免以后的requestCode冲突
    public static final int CLEARED_NOTIFICATION_CODE = 9973;

    public static final String ACTION_DATA_CLEAR_RECEIVER = "ACTION_DATA_CLEAR_RECIEVER";
    @Override
    public void onReceive(Context context, Intent intent) {
        String clearType = intent.getStringExtra(CLEAR_TYPE);
        Log.d("Broadcast", "DataClearReceiver receive");

        if(clearType.equals(CLEAR_DONE)){
            GarnetDatabaseHelper mDatabaseHelper = new GarnetDatabaseHelper(context);
            mDatabaseHelper.deleteDone();

            Intent notificationIntent = new Intent(context, Notifier.class);
            notificationIntent.putExtra(Notifier.NOTIFICATION_TYPE, Notifier.CLEARED_NOTIFICATION);
            notificationIntent.setAction(Notifier.ACTION_NOTIFIER_RECEIVER);
            context.sendBroadcast(notificationIntent);
        }

    }
}