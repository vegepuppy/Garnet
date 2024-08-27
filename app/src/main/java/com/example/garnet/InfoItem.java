package com.example.garnet;

import android.content.Context;

import java.io.Serializable;

public abstract class InfoItem implements Serializable {
    protected String displayString;
    protected String content;
    protected long id;
    protected long belong;

    public static final long LACK_ID = -1;

    abstract void show(Context context);

    public InfoItem(String displayString, String content, long belong, long id) {
        this.displayString = displayString;
        this.content = content;
        this.id = id;
        this.belong = belong;
    }

    public final String getDisplayString() {
        return displayString;
    }

    public final String getContent() {
        return content;
    }

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public final long getBelong() {
        return belong;
    }
}
