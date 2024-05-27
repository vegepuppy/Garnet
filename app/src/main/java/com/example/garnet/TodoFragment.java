package com.example.garnet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TodoFragment extends Fragment {

    private RecyclerView todoRecyclerview;
    private MyAdapter myAdapter;
    private Adapter4InnerRv adapter4InnerRv = new Adapter4InnerRv();
    private FloatingActionButton fab;
    private SQLiteDatabase db;
    private Cursor cursor;
    private List<TodoItem> todoItemList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

//        for (int i = 0; i < 5; i++) {
//            taskList.add("SAMPLE TASK #" + Integer.toString(i));
//        }
//
//        for (int i = 0; i < 3; i++) {
//            dueDateList.add("SAMPLE DATE # "+Integer.toString(i));
//        }

        // item部分
        todoRecyclerview = view.findViewById(R.id.todo_rv);
        myAdapter = new MyAdapter();
        todoRecyclerview.setAdapter(myAdapter);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        // 记住，这里必须直接getActivity()，不能getContext，也不能把getActivity()的结果用一个变量储存起来

        todoRecyclerview.setLayoutManager(lm);

        // FAB部分
        fab = view.findViewById(R.id.add_todo_fab);
        fab.setOnClickListener(new myClickListener());

        // TODO: 2024-04-28 SQLite部分
        try{
            SQLiteOpenHelper sqLiteOpenHelper = new GarnetDatabaseHelper(getActivity());
            db = sqLiteOpenHelper.getWritableDatabase();
            cursor = db.query("TODO",
                    new String[] {"_id","TASK","DUE","DONE"},
                    null,null,null,null,null);

            // 把数据库中的信息全读取到List中去
            if(cursor.moveToFirst()){
                do{
                    String taskFound = cursor.getString(1);
                    String dueDateFound = cursor.getString(2);

                    todoItemList.add(new TodoItem(taskFound, dueDateFound));

                }while(cursor.moveToNext());
            }

        }catch (SQLException e){
            Toast.makeText(getActivity(),"数据库不可用",Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private class myClickListener implements View.OnClickListener{
        private EditText addTaskEt;
        private Button dateButton;

        @Override
        public void onClick(View v) {
            final View windowView = TodoFragment.this.getLayoutInflater().inflate(R.layout.add_todo_dialog, null);
            addTaskEt = windowView.findViewById(R.id.task_et);
            dateButton = windowView.findViewById(R.id.choose_date_btn);

            if(v.getId() == R.id.add_todo_fab){
                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Calendar calendar = Calendar.getInstance();

                        // 这个OnDateSetListener在下面初始化Dialog时候用了
                        DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                // 将Button的显示改为用户选中的日期（格式化为YYYY-MM-DD）
                                String date = formatDate(year, month, dayOfMonth);

                                dateButton.setText(date);
                            }
                            private String formatDate(int year, int month, int dayOfMonth){
                                String monthStr, dayStr;
                                if (month +1 < 10)monthStr = "0"+(month+1);
                                else monthStr = Integer.toString(month+1);

                                if (dayOfMonth < 10)dayStr = "0"+(dayOfMonth);
                                else dayStr = Integer.toString(dayOfMonth);
                                return year + "-" + monthStr + "-" + dayStr;
                            }
                        };

                        // 设置一个用于展示选择日期的Dialog
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),onDateSetListener,
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerDialog.show();
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("创建待办");

                // 设置对应的xml为布局
                builder.setView(windowView);

                // 创建一个监听器用于检测已有的字数
                final TextView countTv = windowView.findViewById(R.id.word_count_tv);
                addTaskEt.addTextChangedListener(new TextLengthLimiter(countTv));


                // 设置确定、取消按钮
                builder.setPositiveButton("确定",null);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = builder.create();

                alertDialog.show();// 大的

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String task = addTaskEt.getText().toString();

                        // 判断标题不为空，如果为空的话不允许添加
                        if (task.trim().isEmpty()){
                            Toast.makeText(getActivity(),"事项名不能为空！",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // 通过一个精妙的方式实现了传参：
                            // 在AlertDialog内部，将用户选择的的日期设置为dateButton的显示文字
                            // 读取这个显示文字，获得的就是用户选择的日期
                            // 余以为妙绝
                            todoItemList.add(new TodoItem(task,dateButton.getText().toString()));
                            adapter4InnerRv.notifyItemChanged(todoItemList.size());
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        }

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
            holder.dueDateTextView.setText(todoItemList.get(position).getDueDate());

            // 为内部的Rv设置adapter和layoutManager
            holder.innerRecyclerView.setAdapter(adapter4InnerRv);
            LinearLayoutManager lm = new LinearLayoutManager(getActivity());
            holder.innerRecyclerView.setLayoutManager(lm);
        }

        @Override
        public int getItemCount() {
            return todoItemList.size();
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
            String s = todoItemList.get(position).getTask();
            holder.checkBox.setText(s);
        }

        // 这里的getItemCount有问题，应该是一个Card内部的元素总数
        @Override
        public int getItemCount() {
            return todoItemList.size();
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
