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
    private final Context in_context;
    public ListWidgetFactory(Context context,Intent intent) {
        in_context = context;
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
            RemoteViews views = new RemoteViews(in_context.getPackageName(),R.layout.widget_none);
            views.setTextViewText(R.id.widget_empty,"今天没有任务哦");
            return views;
        }
        else {
            RemoteViews views = new RemoteViews(in_context.getPackageName(),R.layout.widget_link);
            views.setTextViewText(R.id.widget_link,widget_list.get(position));
            Intent deleteIntent = new Intent();
            deleteIntent.putExtra("ITEM_POSITION", position);
            views.setOnClickFillInIntent(R.id.widget_link, deleteIntent);
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
        GarnetDatabaseHelper widget_helper = new GarnetDatabaseHelper(in_context);
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
