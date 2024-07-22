package com.example.garnet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class InfoItem {
    private String uri;
    private long belong;
    private String displayString;
    // 以上两个变量不能是final，用户可能会改，忽视IDE提示

    public static final long LACK_ID = -1;
    private long id;

    public String getUri() {
        return uri;
    }

    public String getDisplayString() {
        return displayString;
    }

    public long getBelong() {
        return belong;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){this.id = id;}

    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    public InfoItem(String uri, long belong, long id) {
        this.uri = uri;
        this.belong = belong;
        this.id = id;
    }

    public InfoItem(String uri, long belong, long id, String display) {
        this.uri = uri;
        this.belong = belong;
        this.id = id;
        this.displayString = display;
    }
}
