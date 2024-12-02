package com.example.garnet;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyWidgetProvider extends AppWidgetProvider{
    public static final String DELETE_ACTION = "ACTION_DELETE_ITEM";
    public static final String extra_item = "ITEM_POSITION";
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
    final String formattedDate = formatter.format(calendar.getTime());
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        startAlarm(context);
    }

    private void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 4598456, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 设置每 15min 更新一次
        long intervalMillis = 15 * 60 * 1000; // 15min
        long triggerAtMillis = System.currentTimeMillis() + intervalMillis;

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, intervalMillis, pendingIntent);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        stopAlarm(context);
    }

    private void stopAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1233123, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (DELETE_ACTION.equals(intent.getAction())) {
            Log.v("delete_robot", "v: I am testing the delete function." + intent.getIntExtra("ITEM_POSITION", -2));
            int position = intent.getIntExtra(extra_item, -1);
            if (position != -1) {
                new Thread(()->{
                    // 从数据源中删除对应的项
                    deleteItem(position,context);
                    // 更新小组件
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
                    onUpdate(context, appWidgetManager, appWidgetIds);  // 更新视图
                }).start();
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.home_widget);
            remoteViews.setTextViewText(R.id.widget_title,"今日待办");
            //获得listview的适配器
            Intent listIntent = new Intent(context, ListWidgetService.class);
            remoteViews.setRemoteAdapter(R.id.widget_lv,listIntent);
            //设置PendingIntent模板
            Intent intentTemplate = new Intent(context, MyWidgetProvider.class);
            intentTemplate.setAction(DELETE_ACTION);
            PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(context, 1563298, intentTemplate, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setPendingIntentTemplate(R.id.widget_lv, pendingIntentTemplate);
            //点击日期跳转到原应用
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 789813, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_lv);
            Log.v("robot", "v: I am testing the log print function.");
        }
    }

    private void deleteItem(int position, Context context) {
        GarnetDatabaseHelper widget_helper = new GarnetDatabaseHelper(context);
        List<HomeItem> widget_items = widget_helper.loadHome();
        HomeItem hi = widget_items.get(position);
        widget_helper.updateWidgetStatus(hi);
    }
}