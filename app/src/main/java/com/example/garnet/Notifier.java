package com.example.garnet;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

// 用于创建通知
public class Notifier extends BroadcastReceiver {
    public static final String CHANNEL_ID = "channel1";
    public static final int  NOTIFICATION_ID = 1;
    public static final String MESSAGE_EXTRA = "MESSAGE_EXTRA";

    @Override
    public void onReceive(Context context, Intent intent) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("今日待办")
                .setContentText(intent.getStringExtra(MESSAGE_EXTRA))
                .build();
        Log.d("TAG","Notification sent!");

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, notification);
    }
}
