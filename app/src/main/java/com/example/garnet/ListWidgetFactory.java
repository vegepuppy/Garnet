package com.example.garnet;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class ListWidgetFactory implements RemoteViewsService.RemoteViewsFactory{

    private final List<String> widget_list = new ArrayList<>();
    private final Context context;
    public ListWidgetFactory(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        loadData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (widget_list.isEmpty()){return 1;}
        else {return widget_list.size();}
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (widget_list.isEmpty()){
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_none);
            views.setTextViewText(R.id.widget_empty,"今天没有任务哦");
            return views;
        }
        else {
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_link);
            views.setTextViewText(R.id.widget_link,widget_list.get(position));
            // 创建 Intent 用于删除
            Intent intent = new Intent(context, MyWidgetProvider.class);
            intent.setAction("ACTION_DELETE_ITEM");
            intent.putExtra("ITEM_POSITION", position); // 传递要删除的项的索引
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, position, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            // 设置复选框的点击事件
            views.setOnClickPendingIntent(R.id.widget_link, pendingIntent);
            String num = String.valueOf(position);
            Log.v("d_robot", num);
            return views;
        }
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void loadData(){
        widget_list.clear();
        GarnetDatabaseHelper widget_helper = new GarnetDatabaseHelper(context);
        List<HomeItem> widget_items = widget_helper.loadHome();
        if (widget_items.get(0)!=null){
            for (int i = 0; i< widget_items.size(); i++){
                if (!widget_items.get(i).getDone()) {
                    String widget_task = widget_items.get(i).getHomeTask();
                    widget_list.add(widget_task);
                }
            }
        }
    }
}
