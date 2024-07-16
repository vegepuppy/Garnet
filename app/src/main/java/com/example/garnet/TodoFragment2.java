package com.example.garnet;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment2 extends Fragment {
    private List<TodoGroup> mainList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_todo,container,false);

        // rv的初始化
        RecyclerView rv = view.findViewById(R.id.todo_rv);
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        // fab初始化
        FloatingActionButton fab = view.findViewById(R.id.add_todo_fab);
        fab.setOnClickListener(new FabOnClickListener());

        // 读取数据库
        loadData();

        // TODO: 2024-07-15 写这些类和函数
        return view;
    }


    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final AddTodoDialogFragment df = new AddTodoDialogFragment();
            df.setStateListener(new AddTodoDialogFragment.StateListener(){
                @Override
                public String onConfirmed() {
                    return "";
                }
            });

            df.show(getActivity().getSupportFragmentManager(), "123456");//这里乱取了一个tag，小心！！！
        }
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.todo_card_layout,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            TodoGroup currentTodoGroup = mainList.get(position);
            holder.initItem(currentTodoGroup); //这个函数需要设置标题（日期），设置内部的adapter，设置内部的layoutmanager，初始化内部rv


        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }


    private class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv;
        private RecyclerView rv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.due_date_tv);
            rv = itemView.findViewById(R.id.tasks_for_a_day_rv);
        }
        public void initItem(TodoGroup todoGroup){
            tv.setText(todoGroup.getDate());
            todoGroup.setRv(rv);
            todoGroup.initRv(getActivity());
        }
    }

    // 从数据库载入数据
    private void loadData(){
        SQLiteDatabase db = null;
        try{
            SQLiteOpenHelper sqLiteOpenHelper = new GarnetDatabaseHelper(getActivity());
            db = sqLiteOpenHelper.getWritableDatabase();
        }catch (SQLException e){
            Toast.makeText(getActivity(),"数据库打开错误",Toast.LENGTH_SHORT).show();
        }
        if(db != null) {
            Cursor cursor = db.query("TODO",
                    new String[] {"_id","TASK","DUE","DONE"},
                    null,null,null,null,null);
            if(cursor.moveToFirst()){
                do{
                    // TODO: 2024-07-15 下面的idx要改成四个常量，注意getString这一类方法要避免-1
                    String taskFound = null;
                    String dateFound = null;
                    long idFound = 0;
                    boolean doneFound = false;

                    int dueIdx = cursor.getColumnIndex("DUE");
                    if (dueIdx > -1){
                        dateFound = cursor.getString(dueIdx);
                    }
                    int taskIdx = cursor.getColumnIndex("TASK");

                    if (taskIdx > -1){
                        taskFound = cursor.getString(taskIdx);
                    }

                    int doneIdx = cursor.getColumnIndex("DONE");
                    if (doneIdx > -1){
                        doneFound = cursor.getInt(doneIdx) > 0;
                    }

                    int idIdx = cursor.getColumnIndex("_id");
                    if (idIdx > -1){
                        idFound = cursor.getLong(idIdx);
                    }

                    TodoItem ti = new TodoItem(taskFound,dateFound,idFound,doneFound);

                    updateMainList(ti);
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
    }

    // 将读取到的数据加入到mainlist中
    private void updateMainList(TodoItem ti) {
        boolean isAdded = false;
        for(TodoGroup todoGroup : mainList){
            if(ti.getDueDate().equals(todoGroup.getDate())){
                todoGroup.addToTodoList(ti);
                isAdded = true;
                break;
            }
        }
        if(!isAdded){
            TodoGroup todoGroup = new TodoGroup();
            todoGroup.addToTodoList(ti);
            mainList.add(todoGroup);
        }

    }


}