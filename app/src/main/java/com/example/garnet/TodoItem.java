package com.example.garnet;

public class TodoItem{
    String task;
    String dueDate;
    long id;
    boolean isDone = false;

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
}
