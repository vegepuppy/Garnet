package com.example.garnet;

import android.icu.text.IDNA;

import java.util.ArrayList;
import java.util.List;

/**
 * 包含一整个infolist对象
 * 包含一个String表示infolist的title，包含一个List表示LinkTitle
 */
public class InfoItem {
    private String title;
//    private List<String> infoLinkTitleList;
    //TODO 以后要实现链接，可能不是用的String，而是一个新的类

    public String getTitle() {
        return title;
    }

    public InfoItem(String title) {
        this.title = title;
    }
}
