package com.example.garnet;

import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ToDoListActivity extends AppCompatActivity {

    private RecyclerView todoRecyclerview;
    private MyAdapter myAdapter;
    private List<String> taskList = new ArrayList<>();
    private List<String> dueDateList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        for (int i = 0; i < 5; i++) {
            taskList.add("SAMPLE TASK # " + Integer.toString(i));
        }

        for (int i = 0; i < 3; i++) {
            dueDateList.add("SAMPLE DATE # "+Integer.toString(i));
        }

        // item部分
        todoRecyclerview = findViewById(R.id.todo_rv);
        myAdapter = new MyAdapter();
        todoRecyclerview.setAdapter(myAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(ToDoListActivity.this);
        todoRecyclerview.setLayoutManager(lm);

        // TODO: 2024-04-28 FAB部分未完成

        // TODO: 2024-04-28 SQLite部分未完成
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_card_layout,parent,false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.dueDateTextView.setText(dueDateList.get(position));

            // 为内部的Rv设置adapter和layoutManager
            holder.innerRecyclerView.setAdapter(new Adapter4InnerRv());
            LinearLayoutManager lm = new LinearLayoutManager(ToDoListActivity.this);
            holder.innerRecyclerView.setLayoutManager(lm);
        }

        @Override
        public int getItemCount() {
            return dueDateList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        TextView dueDateTextView;
        RecyclerView innerRecyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.dueDateTextView = itemView.findViewById(R.id.due_date_tv);
            this.innerRecyclerView = itemView.findViewById(R.id.tasks_for_a_day_rv);
        }
    }

    private class Adapter4InnerRv extends RecyclerView.Adapter<ViewHolder4InnerRv>{
        @NonNull
        @Override
        public ViewHolder4InnerRv onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_checkbox,parent,false);
            return new ViewHolder4InnerRv(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder4InnerRv holder, int position) {
            String s = taskList.get(position);
            holder.checkBox.setText(s);
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }
    }
    private class ViewHolder4InnerRv extends RecyclerView.ViewHolder{
        CheckBox checkBox;

        public ViewHolder4InnerRv(@NonNull View itemView) {
            super(itemView);
            this.checkBox = itemView.findViewById(R.id.todo_cb);
            // TODO: 2024-04-28 设置长按跳转到对应链接
        }
    }
}