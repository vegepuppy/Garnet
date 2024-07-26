package com.example.garnet;

import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.io.Serializable;

public class TodoItem implements Serializable {
    private String task;
    private String dueDate;
    private long id;
    private boolean isDone = false;

    public static final long LACK_ID = -1;//设置为-1，这样通过id查找时会报错
    public static final String LACK_DATE = "无日期";

    /**构造TodoItem
     * @param id 如果没有就LACK_ID
     * @param dueDate 如果没有就LACK_DATE*/
    // 不要重载这个类。不能设计没有id和没有dueDate的TodoItem，而是用LACK_ID, LACK_Date替代
    public TodoItem(String task, String dueDate, long id, boolean isDone) {
        this.task = task;
        this.dueDate = dueDate;
        this.isDone = isDone;
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public String getDueDate() {
        return dueDate;
    }

    public long getId(){
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
