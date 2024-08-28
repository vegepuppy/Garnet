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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {
    public List<String> homeItemList = new ArrayList<>();
    private List<HomeItem> homeItems = new ArrayList<>();
    private final MyAdapter adapter = new MyAdapter();
    private List<String> link = new ArrayList<>();
    private List<InfoItem> ContentList = new ArrayList<>();
    private TextView tv;
    private TextView tv1;
    private final innerAdapter inneradapter = new innerAdapter();

    public List<String> getlinkList(int position) {
        return homeItems.get(position).getLink();
    }

    private GarnetDatabaseHelper homeHelper;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeHelper = new GarnetDatabaseHelper(getActivity());
        homeItems = homeHelper.loadHome();
        if (homeItems.get(0)!=null) {
            for (int i = 0; i < homeItems.size(); i++) {
                if (homeItems.get(i).getDone()) {
                    homeItems.remove(i);
                    i--;
                }
            }
        }
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY年MM月dd日");
        String formattedDate = formatter.format(calendar.getTime());
        if (homeItems.isEmpty()){
            View view = inflater.inflate(R.layout.home_none,container,false);
            tv = view.findViewById(R.id.Date_tv);
            tv.setText(formattedDate);
            tv1 = view.findViewById(R.id.none_tv);
            tv1.setText("今天任务完成了！");
            return view;
        }
        else if(Objects.equals(homeItems.get(0), null)){
            View view = inflater.inflate(R.layout.home_none,container,false);
            tv = view.findViewById(R.id.Date_tv);
            tv.setText(formattedDate);
            tv1 = view.findViewById(R.id.none_tv);
            tv1.setText("今天没有任务哦!");
            return view;
        }
        else {
            for (int i=0; i<homeItems.size(); i++){
                homeItemList.add(homeItems.get(i).getHomeTask());
            }
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            //Textview的初始化
            tv = view.findViewById(R.id.home_top_tv);
            tv.setText(formattedDate);
            //home_rv的初始化
            RecyclerView rv = view.findViewById(R.id.home_rv);
            rv.setAdapter(adapter);
            rv.setLayoutManager(new LinearLayoutManager(getActivity()));
            return view;
        }
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
            int cnt = 0;
            for(int i = 0; i<homeItems.size(); i++){
                if (!homeItems.get(i).getDone()){
                    cnt++;
                }
            }
            return cnt;
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
        public void onBindViewHolder(@NonNull innerViewHolder holder, int position) {
            holder.inner_initItem(position);
        }

        @Override
        public int getItemCount() {
            return link.size();
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
            HomeItem hi = homeItems.get(position);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    homeHelper.updateHomeStatus(hi,cb);
                    if (isChecked) {
                        // CheckBox 被选中
                        adapter.notifyItemRemoved(homeItems.indexOf(hi));
                        homeItems.remove(hi);
                        homeItemList.remove(hi.getHomeTask());
                    }
                }
            });
            link = getlinkList(position);
            HomeItem homeItem = homeItems.get(position);
            ContentList = homeItem.getinfolist();
            rv.setAdapter(inneradapter);
            rv.setLayoutManager(new LinearLayoutManager(getContext()));
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
            String item = link.get(position);
            InfoItem content = ContentList.get(position);
            task_view.append(item);
            task_view.append("\n");
            task_view.setOnClickListener(v->{
                content.show(getContext());
            });
        }
    }
}