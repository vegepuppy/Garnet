package com.example.garnet;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.Calendar;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        SwitchCompat switchCompat = rootView.findViewById(R.id.daily_notification_switch);

        createNotificationChannel();

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    turnOnNotification();
                    Log.d("TAG","turned on notification");
                }else {
                    Log.d("TAG","turned off notification");
//                    turnOffNotification();
                }
            }
        });
        return rootView;
    }

    private void createNotificationChannel() {
        // Create a notification channel for devices running
        // Android Oreo (API level 26) and above
        String name = "Notify Channel";
        String desc = "A Description of the Channel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(Notifier.CHANNEL_ID, name, importance);
        channel.setDescription(desc);
        // Get the NotificationManager service and create the channel
        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private void turnOnNotification() {
        if (checkNotificationPermissions(requireActivity())){
            scheduleNotification();
        }
    }

    private boolean checkNotificationPermissions(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean isEnabled = notificationManager.areNotificationsEnabled();

        if (!isEnabled) {
            // Open the app notification settings if notifications are not enabled
            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            context.startActivity(intent);

            return false;
        }

        // Permissions are granted
        return true;
    }

    private void scheduleNotification() {
        // Create an intent for the Notification BroadcastReceiver
        Intent intent = new Intent(requireActivity(), Notifier.class);

        // Extract title and message from user input
        String title = "待办提示";
        String message = "做题";

        // Add title and message as extras to the intent
        intent.putExtra(Notifier.TITLE_EXTRA, title);
        intent.putExtra(Notifier.MESSAGE_EXTRA, message);

        // Create a PendingIntent for the broadcast
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                requireActivity(),
                Notifier.NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE + PendingIntent.FLAG_UPDATE_CURRENT // kotlin 'or' 转写为|
        );

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE); // 可能有问题

        // Get the selected time and schedule the notification
        long time = getTime();
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                time,
                5000, // 每天重复
                pendingIntent);

        Log.d("TAG", "done setting notification");
    }

    //获取当前时间的下一分钟，用于调试
    private long getTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024,Calendar.AUGUST,1,8,0);
        return calendar.getTimeInMillis();
    }
}