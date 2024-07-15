package com.example.garnet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoGroup {
    private List<TodoItem> todoList = new ArrayList<>();
    private RecyclerView rv;

    public String getDate() {
        return todoList.get(0).getDueDate();
    }
    public void initRv(Activity activity) {
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        @Override
        public int getItemCount() {
            return todoList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_checkbox,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.initItem(position);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        private CheckBox cb;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.todo_cb);
        }

        public void initItem(int position) {
            TodoItem ti = todoList.get(position);
            cb.setText(ti.getTask());
            cb.setChecked(ti.isDone());
        }
    }

    public void addToTodoList(TodoItem ti){
        todoList.add(ti);
    }

    public void setRv(RecyclerView rv){
        this.rv = rv;
    }

}
