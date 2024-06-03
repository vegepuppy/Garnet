package com.example.garnet;

public class TodoItem{
    String task;
    String dueDate;
    long _id;
    boolean isDone = false;

    public TodoItem(String task, String dueDate, long _id, boolean isDone) {
        this.task = task;
        this.dueDate = dueDate;
        this.isDone = isDone;
        this._id = _id;
    }

    public String getTask() {
        return task;
    }

    public String getDueDate() {
        return dueDate;
    }

    public boolean isDone() {
        return isDone;
    }
}
