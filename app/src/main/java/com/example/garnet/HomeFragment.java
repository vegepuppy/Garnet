package com.example.garnet;

import static java.util.Collections.sort;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    public List<String> infoItemList = new ArrayList<>();
    public List<String> homeItemList = new ArrayList<>();
    private final MyAdapter adapter = new MyAdapter();
    private TextView tv;
    private final innerAdapter inneradapter = new innerAdapter();
    public List<String> getinfolist(int position) {
        return infoItemList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        //Textview的初始化
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
        String formattedDate = formatter.format(calendar.getTime());
        tv = view.findViewById(R.id.home_top_tv);
        tv.setText(formattedDate);
        //home_rv的初始化
        RecyclerView rv = view.findViewById(R.id.home_rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        infoItemList.add("bilibili.com");
        infoItemList.add("zhihu.com");
        infoItemList.add("mooc.com");
        homeItemList.add("高等数学");
        homeItemList.add("大学物理");
        homeItemList.add("离散数学");
        return view;
    }

    //第一层RecyclerView的Adapter
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.initItem(position);
        }

        @Override
        public int getItemCount() {
            return homeItemList.size();
        }
    }
    //第二层RecyclerView的Adapter
    private class innerAdapter extends RecyclerView.Adapter<innerViewHolder>{

        @NonNull
        @Override
        public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View innerview = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_link,parent,false);
            return new innerViewHolder(innerview);
        }

        @Override
        public void onBindViewHolder(@NonNull innerViewHolder holder, int inner_position) {
            holder.inner_initItem(inner_position);
        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }
    //第一层RecyclerView的ViewHolder
    private class MyViewHolder extends RecyclerView.ViewHolder{
        private final CheckBox cb;
        private final RecyclerView rv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.home_cb);
            rv = itemView.findViewById(R.id.home_task_rv);
        }


        public void initItem(int position){
            cb.setText(homeItemList.get(position).toString());
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        // CheckBox 被选中
                        Toast.makeText(getActivity(), "Task is finished", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rv.setAdapter(inneradapter);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
    //第二层RecyclerView的ViewHolder
    private class innerViewHolder extends RecyclerView.ViewHolder{
        private final TextView task_view;
        public innerViewHolder(@NonNull View itemView) {
            super(itemView);
            task_view = itemView.findViewById(R.id.home_lk);
        }
        public void inner_initItem(int position){
            for(int i = 0; i< getinfolist(position).size(); i++){
                String item = getinfolist(position).get(i).toString();
                task_view.append(item);
                task_view.append("\n");
            }
        }
    }
}