package com.example.garnet;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;
import java.util.Date;

// 用于创建通知
public class Notifier extends BroadcastReceiver {
    public static final String CHANNEL_ID = "daily_todo_channel";
    public static final int  NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Date dateToday = Calendar.getInstance().getTime();
        String message = new GarnetDatabaseHelper(context).loadTodoString(dateToday);
        Log.d("NOTI", "preparing to fire notification message: " + message);
        Log.d("NOTI", "dateToday is: " + dateToday.toString());
        if ( message != null ){//如果当日有未完成待办才发送通知
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("今日待办")
                    .setContentText(message)
                    .build();
            Log.d("NOTI","Notification sent!");
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NOTIFICATION_ID, notification);
        }
    }
}
