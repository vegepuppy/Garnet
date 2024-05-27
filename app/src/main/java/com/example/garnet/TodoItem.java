package com.example.garnet;

public class TodoItem{

    String task;
    String dueDate;
    boolean isDone = false;

    public TodoItem(String task, String dueDate) {
        this.task = task;
        this.dueDate = dueDate;
        this.isDone = false;
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
