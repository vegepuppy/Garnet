package com.example.garnet;


import android.content.Context;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;


public class LinkTitleFetcher {
    // TODO: 2024-07-21 这一部分不应该传入context,但是没有找到toast的好方法
    // TODO: 2024-07-21 这一部分甚至不能使用toast，因为不能在非UI线程toast，于是用了逆天的方法，将toast显示在tv上面
    /**
     * 给定一个bilibili链接，返回title
     * @param url 传入链接
     * @param context 传入当前的Activity或Fragment
     * @return 返回得到的title
     */
    public static String biliTitleFetcher(String url, Context context) {
        String title = null;
        try {
            Connection connection = Jsoup.connect(url).timeout(10000).header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0");
            //此处要设置userAgent,否则会得不到bilibili允许,此处设置的是我的浏览器
            Document document = connection.get();
            title = document.title();
        } catch (IOException e) {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            title = "获取网页信息失败";
        } catch (java.lang.IllegalArgumentException e){
//            Toast.makeText(context, "找不到网页", Toast.LENGTH_SHORT).show();
            title = "找不到网页";
        }
        return title;
    }

    /**
     * 给定一个知乎链接，返回title
     * @param url 传入链接
     * @param context 传入当前的Activity或Fragment
     * @return 返回得到的title
     */
    public static String zhihuTitleFetcher(String url, Context context) {
        String title;
        try {
            Connection connection = Jsoup.connect(url).timeout(10000).header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0");
            Document document = connection.get();
            title = document.title(); // TODO: 2024-07-21 只能获取到空字符
        } catch (IOException e) {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            title = "获取网页信息失败";
        } catch (java.lang.IllegalArgumentException e){
//            Toast.makeText(context, "找不到网页", Toast.LENGTH_SHORT).show();
            title = "找不到网页";
        }
        return title;
    }

    /**
     * 给定一个csdn链接，返回title
     * @param url 传入链接
     * @param context 传入当前的Activity或Fragment
     * @return 返回得到的title，但是比网页原显示多一段相似内容
     */
    public static String csdnTitleFetcher(String url, Context context) {
        String title = null;
        try {
            Connection connection = Jsoup.connect(url).timeout(10000).header("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36 Edg/126.0.0.0");
            Document document = connection.get();
            title = document.title();
        } catch (IOException e) {
//            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            title = "获取网页信息失败";
        } catch (java.lang.IllegalArgumentException e){
//            Toast.makeText(context, "找不到网页", Toast.LENGTH_SHORT).show();
            title = "找不到网页";

        }
        return title; // TODO: 2024-07-21 这一段title需要处理，因为csdn网页的title显示与源码不同
    }
}
