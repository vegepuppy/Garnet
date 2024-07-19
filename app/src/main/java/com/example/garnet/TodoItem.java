package com.example.garnet;

import android.widget.CheckBox;
import android.widget.CompoundButton;

public class TodoItem{
    private String task;
    private String dueDate;
    private long id;
    private boolean isDone = false;

    private CheckBox checkBox;

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

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public long getId(){
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setCheckBox(CheckBox cb) {
        // 设置cb为这个TodoItem的勾选框，并根据ti的状态设置CheckBox的文字及状态
        checkBox = cb;
        cb.setText(task);
        cb.setChecked(isDone);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TodoItem.this.isDone = isChecked;
                DataBaseAction.Update.updateTodoStatus(TodoItem.this);
            }
        });
    }
}
