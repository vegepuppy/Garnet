package com.example.garnet;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView infoItemListRecyclerView;
    private FirstAdapter firstAdapter;
    private List<InfoItem> infoItemList = new ArrayList<InfoItem>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //infoItem部分
        infoItemListRecyclerView = findViewById(R.id.info_item_recyclerview);
        firstAdapter = new FirstAdapter();
        infoItemListRecyclerView.setAdapter(firstAdapter);
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

    private class FirstAdapter extends RecyclerView.Adapter<FirstViewHolder> {
        //此处可能不安全
        private View view;
        @NonNull
        @Override
        public FirstViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            view = View.inflate(MainActivity.this,R.layout.layout_info_list_item,null);
            FirstViewHolder firstViewHolder = new FirstViewHolder(view);
            return firstViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull FirstViewHolder holder, int position) {
            InfoItem infoItem = infoItemList.get(position);
            holder.infoListButton.setText(infoItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return infoItemList.size();
        }
    }

    private class FirstViewHolder extends RecyclerView.ViewHolder {
        Button infoListButton;

        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoListButton = itemView.findViewById(R.id.info_list_button);
        }
    }

}