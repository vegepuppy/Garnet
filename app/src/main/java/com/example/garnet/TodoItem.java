package com.example.garnet;

public class TodoItem{
    private String task;
    private String dueDate;
    private long id;
    private boolean isDone = false;
    public static final long LACK_ID = -1;

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

    public void setId(long id) {
        this.id = id;
    }

    public boolean isDone() {
        return isDone;
    }
}
