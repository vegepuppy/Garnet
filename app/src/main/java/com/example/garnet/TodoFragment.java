package com.example.garnet;

import static java.util.Collections.sort;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment {
    private final List<TodoGroup> mainList = new ArrayList<>();
    private final MyAdapter adapter = new MyAdapter();
    private GarnetDatabaseHelper mDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo,container,false);

        mDatabaseHelper = new GarnetDatabaseHelper(getActivity());

        // rv的初始化
        RecyclerView rv = view.findViewById(R.id.todo_rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

        // fab初始化
        FloatingActionButton fab = view.findViewById(R.id.add_todo_fab);
        fab.setOnClickListener(new FabOnClickListener());

        // 读取数据库
        loadData();
        return view;
    }


    private class FabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final AddTodoDialogFragment df = new AddTodoDialogFragment();
            df.setStateListener(new AddTodoDialogFragment.StateListener(){
                @Override
                public void onConfirmed(String date, String task) {
                    TodoItem ti =  new TodoItem(task,date,TodoItem.LACK_ID,false);
                    TodoItem tiWithId = mDatabaseHelper.insertTodo(ti);
                    updateMainList(tiWithId);
                }
            });
            // 根据IDE提示将getActivity()改为requireActivity()
            // 展示dialogFragment
            df.show(requireActivity().getSupportFragmentManager(), "TAG");//这里乱取了一个tag，小心！！！
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
            holder.initItem(currentTodoGroup); //这个函数需要设置标题（日期），设置内部的adapter，设置内部的layoutManager，初始化内部rv
        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tv;
        private final RecyclerView rv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.due_date_tv);
            rv = itemView.findViewById(R.id.tasks_for_a_day_rv);
        }
        public void initItem(@NonNull TodoGroup todoGroup){
            tv.setText(todoGroup.getDate());
            todoGroup.setRv(rv);
            todoGroup.setStateListener(new TodoGroup.StateListener() {
                @Override
                public void startAttachActivity(TodoItem todoItem) {
                    Log.d("TAG","StartViewActivity");
                    Intent intent = new Intent();
                    intent.setClass(requireActivity(), AttachInfoGroupActivity.class);
                    intent.putExtra(AttachInfoGroupActivity.TODO_ITEM, todoItem);
                    startActivity(intent);
                }
            });
            todoGroup.initRv(getActivity());

        }
    }

    // 从数据库载入数据
    private void loadData(){
        List<TodoItem> todoItemsFound = mDatabaseHelper.loadTodo();
        updateMainList(todoItemsFound);
    }

    // 某一个TodoItem加入到mainList中，并刷新adapter
    private void updateMainList(TodoItem ti) {

        boolean isAdded = false;
        for (int i = 0; i < mainList.size(); i++) {
            TodoGroup todoGroup = mainList.get(i);
            if (ti.getDueDate().equals(todoGroup.getDate())) {
                todoGroup.addTodoItem(ti);
                todoGroup.notifyDataAdded();
                adapter.notifyItemChanged(i);
                isAdded = true;
                break;
            }
        }
        // 插入的元素所属的日期在原列表中没有出现过
        if (!isAdded) {
            TodoGroup todoGroup = new TodoGroup(mDatabaseHelper);
            todoGroup.addTodoItem(ti);
            mainList.add(todoGroup);

            sort(mainList);

            int idx = 0; // idx是插入的位置
            for (int i = 0; i < mainList.size(); i++) {
                if (mainList.get(i).equals(todoGroup)) {
                    idx = i;
                    break;
                }
            }
            adapter.notifyItemInserted(idx);//就是notifyDataSetChanged需要排序之后
        }
    }
    private void updateMainList(List<TodoItem> tiList) {
        for (TodoItem ti : tiList){
            updateMainList(ti);
        }
    }
}