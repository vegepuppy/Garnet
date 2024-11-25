package com.example.garnet;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListWidgetFactory(this.getApplicationContext());
    }
}