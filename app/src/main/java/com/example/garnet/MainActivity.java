package com.example.garnet;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView infoItemListRecyclerView;
    private MyAdapter myAdapter;
    private List<InfoItem> infoItemList = new ArrayList<InfoItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoItemListRecyclerView = findViewById(R.id.info_item_recyclerview);
        myAdapter = new MyAdapter();
        infoItemListRecyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        infoItemListRecyclerView.setLayoutManager(layoutManager);
        infoItemList.add(new InfoItem("高等数学"));
        infoItemList.add(new InfoItem("学校通知"));

        //TODO 构造测试数据
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(MainActivity.this, R.layout.layout_info_list_item, null);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoItem infoItem = infoItemList.get(position);
            holder.infoListItemTitleTextView.setText(infoItem.getTitle());
            //TODO 设置holder的另一个属性
        }

        @Override
        public int getItemCount() {
            return infoItemList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView infoListItemTitleTextView;
//        RecyclerView linkListRecyclerView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoListItemTitleTextView = itemView.findViewById(R.id.info_list_item_title);
//            this.linkListRecyclerView = itemView.findViewById(R.id.link_list_recyclerview);
        }
    }
    public void trygit(){return ;}
}