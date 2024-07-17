package com.example.garnet;

import android.database.sqlite.SQLiteDatabase;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toolbar;

public class TodoItem{
    private String task;
    private String dueDate;
    private long id;
    private boolean isDone = false;

    private CheckBox checkBox;

    public static final long LACK_ID = -1;
    public static final String LACK_DATE = "无日期";

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

    public void setId(long id) {
        this.id = id;
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
                DataBaseOperator operator = DataBaseOperator.getInstance();
                operator.setTodoStatus(TodoItem.this);
            }
        });
    }
}
