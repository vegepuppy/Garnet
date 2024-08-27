package com.example.garnet;

import android.content.Context;

public class LinkInfoItem extends InfoItem{

    private boolean isLinkFetched;
    // 以上两个变量不能是final，用户可能会改，忽视IDE提示

    public static final long LACK_ID = -1;

    public long getBelong() {
        return belong;
    }


    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    @Override
    void show(Context context) {
        // TODO: 2024-08-27 网页跳转

    }

    public LinkInfoItem(String display, String uri, long belong, long id) {
        super(display, uri, belong, id);

        // TODO: 2024-08-27 爬虫获取连接
    }

    public boolean isLinkFetched() {
        return isLinkFetched;
    }

    public void setLinkFetched(boolean linkFetched) {
        isLinkFetched = linkFetched;
    }
}
