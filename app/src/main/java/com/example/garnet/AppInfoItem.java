package com.example.garnet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

// TODO: 2024-09-22 实机测试一下这个类是否正常工作
public class AppInfoItem extends InfoItem{
    public String appPackageName = "tv.danmaku.bili";

    public AppInfoItem(String appPackageName, String display, String uri, long belong, long id) {
        super(display, uri, belong, id);
        this.appPackageName = appPackageName;
    }

    public AppInfoItem(String display, String uri, long belong, long id) {
        super(display, uri, belong, id);
        this.appPackageName = "tv.danmaku.bili";
    }

    @Override
    void show(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(appPackageName);
        if (intent != null) {
            intent.putExtra("type", "110");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }else {
            Log.e("share", "null share intent");
        }
    }
}
