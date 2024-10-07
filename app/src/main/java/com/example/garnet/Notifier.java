package com.example.garnet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

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

    public static final int DAILY_NOTIFICATION_ID = 1;
    public static final int START_ACTIVITY_CODE = 2; // TODO: 2024-08-05 这些pendingIntent的Id应该专门存储
    public static final int WEEKLY_NOTIFICATION_ID = 3;
    public static final int CLEARED_NOTIFICATION_ID = 4;// TODO: 2024-09-19 notificationId 有什么用？
    public static final String DAILY_NOTIFICATION = "dailyNotification";
    public static final String WEEKLY_NOTIFICATION = "weeklyNotification";
    public static final String NOTIFICATION_TYPE = "notificationType";
    public static final String CLEARED_NOTIFICATION = "clearedNotification";

    public static final String ACTION_NOTIFIER_RECEIVER = "ACTION_NOTIFIER_RECEIVER";

    @Override
    public void onReceive(Context context, Intent intent) {
        Date dateToday = Calendar.getInstance().getTime();

        Log.d("NOTI", "dateToday is: " + dateToday.toString());
        Log.d("Broadcast", "notifier receive");
        //给notification添加一个pendingIntent，点击时打开应用
        Intent startActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                Notifier.START_ACTIVITY_CODE,
                startActivity,
                PendingIntent.FLAG_IMMUTABLE);

        String notificationType = intent.getStringExtra(NOTIFICATION_TYPE);
        Log.d("Broadcast", "notificationType is: " + notificationType);

        if (notificationType.equals(DAILY_NOTIFICATION)) {
            String message = new GarnetDatabaseHelper(context).loadTodoString(dateToday);
            Log.d("NOTI", "preparing to fire notification message: " + message);
            if ( message != null ){//如果当日有未完成待办才发送通知
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("今日待办")
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .build();
                Log.d("NOTI","Notification sent!");
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(DAILY_NOTIFICATION_ID, notification);
            }
        } else if (notificationType.equals(WEEKLY_NOTIFICATION)) {
            String message = new GarnetDatabaseHelper(context).loadTodoString("无日期");
            Log.d("NOTI", "preparing to fire notification message: " + message);
            if ( message != null ){
                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("未完成无日期待办")
                        .setContentText(message)
                        .setContentIntent(pendingIntent)
                        .build();
                Log.d("NOTI","Notification sent!");
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(DAILY_NOTIFICATION_ID, notification);// TODO: 2024-08-05 这里这个id有什么用？为什么要重复用1
            }
        } else if (notificationType.equals(CLEARED_NOTIFICATION)) {
            String message = "已完成定时清理！";
            Notification notification = new NotificationCompat.Builder(context,CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("待办清理")
                    .setContentText(message)
                    .setContentIntent(pendingIntent)
                    .build();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(CLEARED_NOTIFICATION_ID, notification);
        }
    }
}
