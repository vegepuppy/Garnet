package com.example.garnet;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyWidgetProvider extends AppWidgetProvider{
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
    final String formattedDate = formatter.format(calendar.getTime());

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.home_widget);
            remoteViews.setTextViewText(R.id.widget_title,formattedDate);
            //获得listview的适配器
            Intent listIntent = new Intent(context, ListWidgetService.class);
            remoteViews.setRemoteAdapter(R.id.widget_lv,listIntent);

            //点击日期跳转到原应用
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            remoteViews.setOnClickPendingIntent(R.id.widget_title, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId,remoteViews);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_lv);
        }
    }
}