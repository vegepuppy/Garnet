package com.example.garnet;

import static java.util.Collections.sort;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    public final List<String> infoItemList = new ArrayList<>();//看你添加的东西，应该叫infoItemList更合适吧，我直接改了
    private final MyAdapter adapter = new MyAdapter();
    private TextView tv;
    private final innerAdapter inneradapter = new innerAdapter();
    private RecyclerView inner_rv;//看上去这个变量没有任何作用呀

    // getXXX这种方法一般是用来获取的，不要在里面写添加的逻辑
    public List<String> getTasklist(int position) {
        infoItemList.add("bilibili.com");
        infoItemList.add("zhihu.com");
        infoItemList.add("mooc.com");
        return infoItemList;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        //Textview的初始化
        tv = view.findViewById(R.id.home_top_tv);
        tv.setText("2023年7月18日");
        //home_rv的初始化
        RecyclerView rv = view.findViewById(R.id.home_rv);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
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
           return infoItemList.size()+1;//为啥+1？
       }
    }
    //第二层RecyclerView的Adapter
     private class innerAdapter extends RecyclerView.Adapter<innerViewHolder>{

        @NonNull
        @Override
        public innerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View innerview = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_card,parent,false);
            return new innerViewHolder(innerview);
        }

        @Override
        public void onBindViewHolder(@NonNull innerViewHolder holder, int inner_position) {
            holder.inner_initItem(inner_position);
        }

        @Override
        public int getItemCount() {
            return infoItemList.size();
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

       /* 最主要的问题出在这个方法里面，其他的可能也有问题，但是我就没细看了，你自己检查下
       我们设计initItem的初衷是在这个方法里完成内部元素的所有初始化，包括设置显示的内容，为内部的RecyclerView设置Adapter等
       你这个没有给内部的RecyclerView设置，当然不会显示了
        */
       public void initItem(int position){
           cb.setText("高等数学");
           cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                   if (isChecked) {
                       // CheckBox 被选中
                       Toast.makeText(getActivity(), "Task is finished", Toast.LENGTH_SHORT).show();
                   } else {
                       // CheckBox 被取消选中
                       Toast.makeText(getActivity(), "Task is undone", Toast.LENGTH_SHORT).show();
                   }
               }
           });
           setRv(rv);//这行没有任何作用，rv这个变量也没有任何作用
           initRV(getActivity());//也没有任何作用，你在initRV()这个方法里面是对inner_rv进行的adapter设置，inner_rv这个变量看上去也没有作用
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
            String item = getTasklist(position).toString();
            task_view.append(item);//错了
        }
    }
    //下面两个函数初始化第二层的RecyclerView
    public void initRV(Activity activity){
        inner_rv.setAdapter(inneradapter);
        inner_rv.setLayoutManager(new LinearLayoutManager(activity));
    }

    // 就一行，也没有复用，没必要弄成函数
    public void setRv(RecyclerView rv){
        this.inner_rv = rv;
    }
   
}
