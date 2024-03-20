package com.example.garnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView infoItemListRecyclerView;
    private myAdapter myAdapter;
    private List<InfoItem> infoItemList = new ArrayList<InfoItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //infoItem部分
        infoItemListRecyclerView = findViewById(R.id.info_item_recyclerview);
        myAdapter = new myAdapter();
        infoItemListRecyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
        infoItemListRecyclerView.setLayoutManager(layoutManager);


        //数据部分
        infoItemList.add(new InfoItem("高等数学"));
        infoItemList.add(new InfoItem("学校通知"));
        for (int i = 0; i < 20; i++) {
            infoItemList.add(new InfoItem(Integer.toString(i)+" infoitem"));
        }
        //TODO 构造测试数据
    }

    private class myAdapter extends RecyclerView.Adapter<MyViewHolder> {
        //此处可能不安全
        private View view;
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //view = View.inflate(MainActivity.this,R.layout.layout_info_list_item,null);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_list_item, parent, false);
            // 我不理解，不要动这里

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoItem infoItem = infoItemList.get(position);
            holder.infoListTitleTextView.setText(infoItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return infoItemList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView infoListTitleTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoListTitleTextView = itemView.findViewById(R.id.text_in_card);
        }
    }

}
