package com.example.garnet;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingFragment extends Fragment {

    private SwitchCompat dailyNotificationSwitchCompat;
    private SwitchCompat weeklyNotificationSwitchCompat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        initDailyNotificationSwitch(rootView);
        initConfirmButton(rootView);
        initWeeklyNotificationSwitch(rootView);
        initClearDoneNowButton(rootView, requireActivity());
        createNotificationChannel();

        return rootView;
    }

    private void initClearDoneNowButton(View rootView, Context context) {
        Button clearNowButton = rootView.findViewById(R.id.clear_done_now_button);
        clearNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GarnetDatabaseHelper mDatabaseHelper = new GarnetDatabaseHelper(context);
                mDatabaseHelper.deleteDone();
                Toast.makeText(context, "已清理", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initDailyNotificationSwitch(View rootView){
        dailyNotificationSwitchCompat = rootView.findViewById(R.id.daily_notification_switch);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean notification = sharedPreferences.getBoolean("dailyNotification", false);
        dailyNotificationSwitchCompat.setChecked(notification);

        dailyNotificationSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Create an intent for the Notification BroadcastReceiver
            Intent intent = new Intent(requireActivity(), Notifier.class);
            intent.putExtra(Notifier.NOTIFICATION_TYPE, Notifier.DAILY_NOTIFICATION);
            // Create a PendingIntent for the broadcast
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireActivity(),
                    Notifier.DAILY_NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            if (isChecked) {
                Log.d("NOTI", "turned on notification");
                if (checkNotificationPermissions(requireActivity())) {

                    scheduleDailyNotification(pendingIntent);

                }
            } else {
                Log.d("NOTI", "turned off notification");
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE); // 可能有问题
                alarmManager.cancel(pendingIntent);
            }
        });

    }
    private void initWeeklyNotificationSwitch(View rootView){
        weeklyNotificationSwitchCompat = rootView.findViewById(R.id.weekly_notification_switch);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean notification = sharedPreferences.getBoolean("weeklyNotification", false);
        weeklyNotificationSwitchCompat.setChecked(notification);

        weeklyNotificationSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent intent = new Intent(requireActivity(), Notifier.class);
            intent.putExtra(Notifier.NOTIFICATION_TYPE, Notifier.WEEKLY_NOTIFICATION);
            // Create a PendingIntent for the broadcast
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireActivity(),
                    Notifier.WEEKLY_NOTIFICATION_ID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            if (isChecked) {
                Log.d("NOTI", "turned on notification");
                if (checkNotificationPermissions(requireActivity())) {
                    scheduleWeeklyNotification(pendingIntent);
                }
            } else {
                Log.d("NOTI", "turned off notification");
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE); // 可能有问题
                alarmManager.cancel(pendingIntent);
            }
        });
    }
    private void initConfirmButton(View rootView){
        FloatingActionButton confirmButton = rootView.findViewById(R.id.setting_confirm_fab);

        confirmButton.setOnClickListener(v -> {
            SharedPreferences preferences = getActivity().getSharedPreferences("settings",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("dailyNotification", dailyNotificationSwitchCompat.isChecked());
            editor.putBoolean("weeklyNotification", weeklyNotificationSwitchCompat.isChecked());
            editor.apply();

//            Intent intent1 = new Intent(requireActivity(), Notifier.class);
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                    requireActivity(),
//                    Notifier.NOTIFICATION_ID,
//                    intent1,
//                    PendingIntent.FLAG_IMMUTABLE
//            );
//            AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);
//
            Toast.makeText(getActivity(), "设置已保存", Toast.LENGTH_SHORT).show();
        });
    }

    private void createNotificationChannel() {
        // Android Oreo (API level 26) and above
        String name = "每日通知";
        String desc = "提醒当日的待办事项";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel channel = new NotificationChannel(Notifier.CHANNEL_ID, name, importance);
        channel.setDescription(desc);

        NotificationManager notificationManager = (NotificationManager) requireActivity().getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
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


    private void scheduleDailyNotification(PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        long time = getDailySendTimeInMillis();//以毫秒计，发送通知的时间

        Log.d("NOTI", "time: " + time);
        Log.d("NOTI", "Calendar.getInstance().getTimeInMillis(): " + Calendar.getInstance().getTimeInMillis());

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY, // 每天重复
                pendingIntent);
        Log.d("NOTI", "alarm set at: " + new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").format(time));

        Log.d("NOTI", "done setting pending intent");
    }


    private void scheduleWeeklyNotification(PendingIntent pendingIntent){
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        long time = getWeeklySendTimeInMillis();
        Log.d("NOTI", "time: " + time);
        Log.d("NOTI", "Calendar.getInstance().getTimeInMillis(): " + Calendar.getInstance().getTimeInMillis());

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY*7,
                pendingIntent);
        Log.d("NOTI", "alarm set at: " + new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").format(time));

        Log.d("NOTI", "done setting pending intent");
    }

    /**
     * 获取第一次发送通知的时间，如果今天没到8.00am就是今天8.00am，如果今天过了8.00am就是明天的8.00am
     */
    @SuppressLint("SimpleDateFormat")//用于打log，忽视这warning

    private long getDailySendTimeInMillis() {

        Calendar calendar = Calendar.getInstance();
        // 获得今天8:00:00am
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {// 如果今天已经过了8.00，就明天发通知
            Log.d("NOTI", "8:00AM today has passed");
            calendar.add(Calendar.DATE, 1);
        }
        Log.d("NOTI", "will trigger on: " +
                new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").format(calendar.getTime()));//打Log不需要在意warning
        //HH24小时制，hh12小时制
        return calendar.getTimeInMillis();
    }
    private long getWeeklySendTimeInMillis(){
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if(currentDayOfWeek == Calendar.SUNDAY){
            Log.d("NOTI", "Sunday today");
        }
        else{
            calendar.set(Calendar.DAY_OF_WEEK, 7);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Log.d("NOTI", "will trigger on: " +
                    new SimpleDateFormat("yyyy-MM-dd, HH:mm:ss").format(calendar.getTime()));//打Log不需要在意warning
        }
        return calendar.getTimeInMillis();
    }

}