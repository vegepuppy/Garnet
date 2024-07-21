package com.example.garnet;


import android.content.Context;

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
     * @return 返回得到的title
     */
    public static String fetchBiliTitle(String url) {
        String title = null;
        try {
            Connection connection = Jsoup.connect(url).timeout(10000);
            //此处要设置userAgent,否则会得不到bilibili允许,此处设置的是我的浏览器
            Document document = connection.get();
            title = document.title();
        } catch (IOException e) {
            title = "获取网页信息失败";
        } catch (java.lang.IllegalArgumentException e){
            title = "找不到网页";
        }
        return title;
    }
}
