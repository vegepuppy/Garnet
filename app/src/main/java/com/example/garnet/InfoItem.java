package com.example.garnet;

public class InfoItem {
    private String uri;
    private long belong;
    // 以上两个变量不能是final，用户可能会改，忽视IDE提示

    public static final long LACK_ID = -1;
    long id;

    public String getUri() {
        return uri;
    }

    public long getBelong() {
        return belong;
    }

    public long getId() {
        return id;
    }

    public InfoItem(String uri, long belong, long id) {
        this.uri = uri;
        this.belong = belong;
        this.id = id;
    }
}
