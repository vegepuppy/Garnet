package com.example.garnet;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;

import android.icu.text.IDNA;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.garnet.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingFragment extends Fragment {

    private static final int LAUNCH_LOGIN_ACTIVITY = 100;
    private SwitchCompat dailyNotificationSwitchCompat;
    private SwitchCompat weeklyNotificationSwitchCompat;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private TextView userNameTextView;

    private static final String PREF_TABLE_NAME = "prefTableName";
    private static final String PREF_DAILY_NOTIFICATION = "prefDailyNotification";
    private static final String PREF_WEEKLY_NOTIFICATION = "prefWeeklyNotification";
    private static final String PREF_WEEKLY_CLEAR = "prefWeeklyClear";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        initDailyNotificationSwitch(rootView);
        initWeeklyNotificationSwitch(rootView);
        initClearDoneNowButton(rootView, requireActivity());
        initLogInPart(rootView);
        createNotificationChannel();

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
//                        Snackbar.make(rootView, "已开启", Snackbar.LENGTH_SHORT).show();
                    } else {
//                        Snackbar.make(rootView, "未开启", Snackbar.LENGTH_SHORT).show();
                    }
                }
        );
        return rootView;
    }

    private void initLogInPart(View rootView) {
        Button logInButton = rootView.findViewById(R.id.log_in_button);
        userNameTextView = rootView.findViewById(R.id.user_name_tv);
        logInButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LogInActivity.class);
            startActivityForResult(intent, LAUNCH_LOGIN_ACTIVITY);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_LOGIN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String userName = data.getStringExtra("username");
                userNameTextView.setText(userName);
                syncDatabase();
            }
        }
    }

    // 先只做待办
    private void syncDatabase() {
        GarnetDatabaseHelper helper = new GarnetDatabaseHelper(getContext());
        List<InfoItem> testInfoItemList = helper.loadInfo(1);//当前只测试id为1的InfoGroup
        OkHttpClient client = new OkHttpClient();

        JSONArray jsonArray = new JSONArray();
        for (InfoItem infoItem : testInfoItemList) {
            putInfoItemJson(infoItem, jsonArray);
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, jsonArray.toString());

        String url = "http://10.0.2.2:3001/infoitem";

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String message = response.isSuccessful() ? response.body().string() : "Error: " + response.code();
//                runOnUiThread(() -> Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show());
                LogUtils.logWeb(message);
            } catch (IOException e) {
                e.printStackTrace();
//                runOnUiThread(() -> Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT).show());
                LogUtils.logWeb("error occurred");
            }
        }).start();
    }

    private void putInfoItemJson(InfoItem infoItem, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", infoItem.getId());
            jsonObject.put("content", infoItem.getContent());
            jsonObject.put("belong", infoItem.getBelong());
            jsonObject.put("display", infoItem.getDisplayString());
        } catch (Exception e) {
            Log.e("TAG", "putInfoItemJson: error occurred in putting in JSON.");
        }
        jsonArray.put(jsonObject);
    }

    private void initClearDoneNowButton(View rootView, Context context) {
        Button clearNowButton = rootView.findViewById(R.id.clear_done_now_button);
        SwitchCompat clearSwitchCompat = rootView.findViewById(R.id.weekly_clear_switch);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean prefBoolean = sharedPreferences.getBoolean(PREF_WEEKLY_CLEAR, false);
        clearSwitchCompat.setChecked(prefBoolean);

        clearNowButton.setOnClickListener(v -> {
            GarnetDatabaseHelper mDatabaseHelper = new GarnetDatabaseHelper(context);
            mDatabaseHelper.deleteDone();
            Toast.makeText(context, "已清理", Toast.LENGTH_SHORT).show();
        });

        clearSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Intent intent = new Intent(requireActivity(), DataClearReceiver.class);
            intent.putExtra(DataClearReceiver.CLEAR_TYPE, DataClearReceiver.CLEAR_DONE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireActivity(),
                    DataClearReceiver.DATA_CLEAR_CODE,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            AlarmManager alarmManager;// 可能有问题
            if (isChecked) {
                alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
                long time = getDailySendTimeInMillis();

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                        time,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingIntent);

            } else {
                alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);

            }
            editor.putBoolean(PREF_WEEKLY_CLEAR, isChecked);
            editor.apply();
        });
    }

    private void initDailyNotificationSwitch(View rootView) {
        dailyNotificationSwitchCompat = rootView.findViewById(R.id.daily_notification_switch);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean notification = sharedPreferences.getBoolean(PREF_DAILY_NOTIFICATION, false);
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
                    editor.putBoolean(PREF_DAILY_NOTIFICATION, true);
                    dailyNotificationSwitchCompat.setChecked(true);
                } else {
                    dailyNotificationSwitchCompat.setChecked(false);
                }
            } else {
                Log.d("NOTI", "turned off notification");
                AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE); // 可能有问题
                alarmManager.cancel(pendingIntent);
                editor.putBoolean(PREF_DAILY_NOTIFICATION, false);
                //保存设置
            }
            editor.apply();
        });

    }

    private void initWeeklyNotificationSwitch(View rootView) {
        weeklyNotificationSwitchCompat = rootView.findViewById(R.id.weekly_notification_switch);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREF_TABLE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean notification = sharedPreferences.getBoolean(PREF_WEEKLY_NOTIFICATION, false);
        weeklyNotificationSwitchCompat.setChecked(notification);

        weeklyNotificationSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
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
                        scheduleDailyNotification(pendingIntent);
                        editor.putBoolean(PREF_WEEKLY_NOTIFICATION, true);
                        dailyNotificationSwitchCompat.setChecked(true);
                    } else {
                        dailyNotificationSwitchCompat.setChecked(false);
                        editor.putBoolean(PREF_WEEKLY_NOTIFICATION, false);
                    }
                } else {
                    Log.d("NOTI", "turned off notification");
                    AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE); // 可能有问题
                    alarmManager.cancel(pendingIntent);
                    editor.putBoolean(PREF_WEEKLY_NOTIFICATION, false);
                }

                editor.apply();
            }
        });
    }

    private void createNotificationChannel() {
        // Android Oreo (API level 26) and above
        String name = "待办通知";
        String desc = "提醒待办事项";
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
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
        isEnabled = notificationManager.areNotificationsEnabled();
        return isEnabled;
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


    private void scheduleWeeklyNotification(PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
        long time = getWeeklySendTimeInMillis();
        Log.d("NOTI", "time: " + time);
        Log.d("NOTI", "Calendar.getInstance().getTimeInMillis(): " + Calendar.getInstance().getTimeInMillis());

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                time,
                AlarmManager.INTERVAL_DAY * 7,
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

    private long getWeeklySendTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        if (currentDayOfWeek == Calendar.SUNDAY) {
            Log.d("NOTI", "Sunday today");
        } else {
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