package com.example.garnet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

import com.example.garnet.utils.LogUtils;

import java.util.Collections;
import java.util.List;

public class AppInfoItem extends InfoItem{
    public static final String BILI_IDENTIFIER = "https://b23.tv";
    public static final String ZHIHU_IDENTIFIER ="https://www.zhihu.com";
    public static final String BILI_PACKAGE_NAME = "tv.danmaku.bili";
    public static final String ZHIHU_PACKAGE_NAME = "com.zhihu.android";

    public String getAppPackageName() {
        return appPackageName;
    }

    private String appPackageName;

    public AppInfoItem(String appPackageName, String display, String uri, long belong, long id) {
        super(display, uri, belong, id);
        this.appPackageName = appPackageName;
    }

    public AppInfoItem(String display, String uri, long belong, long id) {
        super(display, uri, belong, id);
        if (this.content.contains(AppInfoItem.BILI_IDENTIFIER)) {
            this.appPackageName = BILI_PACKAGE_NAME;
        }else if (this.content.contains(AppInfoItem.ZHIHU_IDENTIFIER)){
            this.appPackageName = ZHIHU_PACKAGE_NAME;
        } else {
            this.appPackageName = "com.android.settings";
        }
    }

    @Override
    void show(Context context) {
        LogUtils.logShare("AppInfoItem.show() called");
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(appPackageName, PackageManager.GET_ACTIVITIES);
        } catch ( PackageManager.NameNotFoundException e){
            LogUtils.logShare("package name " +appPackageName+" not found");
            Toast.makeText(context, "无效app外链！", Toast.LENGTH_SHORT).show();
        }

        Intent intent = pm.getLaunchIntentForPackage(appPackageName);
        if (intent != null) {
            intent.putExtra(Intent.EXTRA_TEXT, this.content);
            context.startActivity(intent);
        }else {
            LogUtils.logShare("null share intent");
        }
    }

    // 一下两个函数只用于调试，在业务中不发挥实际作用
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
