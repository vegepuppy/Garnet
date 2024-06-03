package com.example.garnet;

import java.util.List;

public class DateCardContent {
    private String date;
    private List<TodoItem> todoItemList;
    private TodoFragment.Adapter4InnerRv adapter;

    public DateCardContent(String date, List<TodoItem> todoItemList) {
        this.date = date;
        this.todoItemList = todoItemList;
    }

    public String getDate() {
        return date;
    }

    public List<TodoItem> getTodoItemList() {
        return todoItemList;
    }

    // 将一个新的时间加入到这个DateWithTodo对象中去
    public void addTodoItem(TodoItem t){
        this.todoItemList.add(t);
    }

    public void setAdapter(TodoFragment.Adapter4InnerRv adapter) {
        this.adapter = adapter;
    }

    public TodoFragment.Adapter4InnerRv getAdapter() {
        return adapter;
    }
}
