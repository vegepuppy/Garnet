package com.example.garnet;

import android.icu.text.IDNA;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 包含一整个infolist对象
 * 包含一个String表示infolist的title，包含一个List表示LinkTitle
 */
@Deprecated
public class InfoItem {
    private String title;
//    private List<String> infoLinkTitleList;
    //TODO 以后要实现链接，可能不是用的String，而是一个新的类

    private List<String> urlList = new ArrayList<>();


    public String getTitle() {
        return title;
    }

    public InfoItem(String title) {
        this.title = title;
        this.urlList.add("http://www.bilibili.com");
        this.urlList.add("http://www.baidu.com");
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // testing in new branch
}
