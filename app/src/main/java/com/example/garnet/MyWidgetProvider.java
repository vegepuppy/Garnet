package com.example.garnet;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.RemoteViews;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyWidgetProvider extends AppWidgetProvider{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
        final String formattedDate = formatter.format(calendar.getTime());
        GarnetDatabaseHelper widget_helper;
        widget_helper = new GarnetDatabaseHelper(context);
        List<HomeItem> widget_list = new ArrayList<>();
        widget_list = widget_helper.loadHome();
        for (int appWidgetId : appWidgetIds){
            //获取界面
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.home_widget);
            views.setTextViewText(R.id.widget_title,formattedDate);
            appWidgetManager.updateAppWidget(appWidgetId, views);

            //点击日期跳转到原应用
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_title, pendingIntent);
            //更新组件
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}