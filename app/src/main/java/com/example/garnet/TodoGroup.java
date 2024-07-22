package com.example.garnet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TodoGroup implements Comparable<TodoGroup>{
    private final List<TodoItem> todoList = new ArrayList<>();
    private RecyclerView rv;
    private final MyAdapter adapter = new MyAdapter();
    private final GarnetDatabaseHelper mDatabaseHelper;
    private StateListener mStateListener;

    public TodoGroup(GarnetDatabaseHelper mDatabaseHelper) {
        this.mDatabaseHelper = mDatabaseHelper;
    }

    // 为内部的rv设置adapter
    public void initRv(Activity activity) {
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }

    public void notifyDataAdded() {
        adapter.notifyItemInserted(todoList.size());
    }

    @Override
    public int compareTo(TodoGroup tg) {
        if (this.getDate().equals(TodoItem.LACK_DATE)){
            return -1;// 负数表示放在最前面
        } else if (tg.getDate().equals(TodoItem.LACK_DATE)){
            return +1;
        } else return this.getDate().compareTo(tg.getDate());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        @Override
        public int getItemCount() {
            return todoList.size();
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item_display,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.initItem(position);
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        private final CheckBox cb;
        private final Button viewAttachButton;
        private final Button addAttachButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.todo_display_cb);
            viewAttachButton = itemView.findViewById(R.id.todo_display_view_attach_button);
            addAttachButton = itemView.findViewById(R.id.todo_display_attach_button);
        }

        public void initItem(int position) {
            // 设置每一个CheckBox的状态
            TodoItem todoItem = todoList.get(position);
            cb.setChecked(todoItem.isDone());
            cb.setText(todoItem.getTask());
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                todoItem.setDone(isChecked);
                mDatabaseHelper.updateTodoStatus(todoItem, cb);
            });

            // 设置两个Button对应的Listener
            long itemId = todoList.get(position).getId();
            viewAttachButton.setOnClickListener(v -> mStateListener.StartViewActivity(itemId));
            addAttachButton.setOnClickListener(v -> mStateListener.StartAddActivity(itemId));
        }
    }

    public void addTodoItem(TodoItem ti)  {
            todoList.add(ti);
    }

    public void setRv(RecyclerView rv){
        this.rv = rv;
    }

    public String getDate() {
        return todoList.get(0).getDueDate();
    }

    public interface StateListener{
        void StartViewActivity(long itemId);
        void StartAddActivity(long itemId);
    }

    public void setStateListener(StateListener s){
        this.mStateListener = s;
    }
}
