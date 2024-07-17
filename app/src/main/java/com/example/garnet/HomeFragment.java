package com.example.garnet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private MyAdapter myAdapter;
    private SQLiteDatabase db;
    private Cursor cursor;
    private List<HomeGroup> tasklist = new ArrayList<>();
    private boolean isdone = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        RecyclerView homeRecyclerview = view.findViewById(R.id.home_rv);
        myAdapter = new MyAdapter();
        homeRecyclerview.setAdapter(myAdapter);

        homeRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        

        //TODO:2024-07-17 数据库的问题还没有解决

        return view;
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.home_card,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            HomeGroup currentHomeGroup = tasklist.get(position);
            holder.initItem(currentHomeGroup);
        }

        @Override
        public int getItemCount() {
            return tasklist.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        private RecyclerView rv;
        private CheckBox cb;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            rv = itemView.findViewById(R.id.home_task_rv);
            cb = itemView.findViewById(R.id.home_cb);
        }
        public void initItem(HomeGroup homeGroup){
            homeGroup.setRv(rv);
            cb.setText("高等数学");
            isdone = cb.isChecked();
            cb.setChecked(isdone);
            homeGroup.initRv(getActivity());
        }
    }
}