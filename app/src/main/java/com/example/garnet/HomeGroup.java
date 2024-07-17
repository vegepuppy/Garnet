package com.example.garnet;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class HomeGroup {
    private List<Homeitem> homeList = new ArrayList<>();
    private RecyclerView rv;
    private MyAdapter adapter = new MyAdapter();

    public void initRv(Activity activity){
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(activity));
    }

    //todo:这里有一个更新数据

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @NonNull
        @Override

        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_link,parent,false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.initItem(position);
        }

        @Override
        public int getItemCount(){return homeList.size();}
    }

    private class  MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.home_lk);
        }
        public void initItem(int position){
            Homeitem hi = homeList.get(position);
            tv.setText(hi.getLink_task());
        }
    }

    public void addtohomeList(Homeitem hi){homeList.add(hi);}

    public void setRv(RecyclerView rv) {
        this.rv = rv;
    }
}
