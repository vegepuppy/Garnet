package com.example.garnet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import com.example.garnet.utils.LogUtils;

import java.util.Collections;
import java.util.List;

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
        getAllApp(context);
        getPackageActivities(context, "tv.danmaku.bili");

        LogUtils.logShare("AppInfoItem.show() called");
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
        } catch ( PackageManager.NameNotFoundException e){
            LogUtils.logShare("package name " +appPackageName+" not found");
        }

        Intent intent = pm.getLaunchIntentForPackage(appPackageName);
        if (intent != null) {
            intent.putExtra(Intent.EXTRA_TEXT, this.content);
//            intent.setFlags(Intent.FLAG);
            context.startActivity(intent);
        }else {
            LogUtils.logShare("null share intent");
        }
    }

    /**
     * 通过PackageManager获取手机内所有app的包名和启动页（首个启动Activity的类名）
     * 可根据自己业务需求封装方法的返回体，可以是单app信息，也可以是appList
     */
    public void getAllApp(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> appsInfo = packageManager.queryIntentActivities(intent, 0);
        Collections.sort(appsInfo, new ResolveInfo.DisplayNameComparator(packageManager));
        for (ResolveInfo info : appsInfo) {
            String pkg = info.activityInfo.packageName;
            String cls = info.activityInfo.name;
            Log.e("app_info", "pkg:" + pkg + " —— cls:" + cls);
        }
    }

    private void getPackageActivities(Context context, String packageName){
        try{
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packageInfo.activities;
            if (activities != null){
                for (ActivityInfo i : activities){
                    Log.d("app_info", i.name);
                }
            }
        }catch (PackageManager.NameNotFoundException e){
            LogUtils.logShare("name not found");
        }
    }


}
