package com.example.garnet;

import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import java.io.Serializable;

public abstract class InfoItem implements Serializable {
    public static final int TYPE_LINK = 1;
    public static final int TYPE_NOTE = 2;
    public static final int TYPE_APP = 3;
    protected String displayString;
    protected String content;
    protected long id;
    protected long belong;

    public static final long LACK_ID = -1;

    abstract void show(Context context);
    abstract void show(Context context, ActivityResultLauncher<Intent> launcher);// FIXME: 2024-09-22 这里不应该是Intent，最好要是InfoItem，但是这样需要自定义协议
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

    public void setDisplayString(String displayString) {
        this.displayString = displayString;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public final long getBelong() {
        return belong;
    }
}
