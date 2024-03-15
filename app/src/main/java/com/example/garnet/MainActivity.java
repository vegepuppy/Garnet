package com.example.garnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
    private List<InfoLink> infoLinkList = new ArrayList<>();

    private SecondAdapter secondAdapter;
    private RecyclerView infoLinkListRecyclerView;

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
        infoLinkList.add(new InfoLink("bilibili"));
        infoLinkList.add(new InfoLink("zhihu"));
        for (int i = 0; i < 20; i++) {
            infoItemList.add(new InfoItem(Integer.toString(i)+" infoitem"));
            infoLinkList.add(new InfoLink(Integer.toString(i)+"infolink"));

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
            holder.infoListItemTitleTextView.setText(infoItem.getTitle());
            //TODO 设置holder的另一个属性
            infoLinkListRecyclerView = view.findViewById(R.id.info_link_recyclerview);
            secondAdapter = new SecondAdapter();
            infoLinkListRecyclerView.setAdapter(secondAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            infoLinkListRecyclerView.setLayoutManager(layoutManager);//此处可能有问题，运用了同一个LayoutManager
        }

        @Override
        public int getItemCount() {
            return infoItemList.size();
        }
    }

    private class FirstViewHolder extends RecyclerView.ViewHolder {
        TextView infoListItemTitleTextView;
//        RecyclerView linkListRecyclerView;


        public FirstViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoListItemTitleTextView = itemView.findViewById(R.id.info_list_item_title);
//            this.linkListRecyclerView = itemView.findViewById(R.id.link_list_recyclerview);
        }
    }
    private class SecondAdapter extends RecyclerView.Adapter<SecondViewHolder>{
        @NonNull
        @Override
        public SecondViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(MainActivity.this, R.layout.layout_info_link_item,null);
            SecondViewHolder secondViewHolder = new SecondViewHolder(view);
            return secondViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SecondViewHolder holder, int position) {
            InfoLink infoLink = infoLinkList.get(position);
            holder.infoLinkButton.setText(infoLink.getTitle());
        }

        @Override
        public int getItemCount() {
            return infoLinkList.size();
        }
    }

    private class SecondViewHolder extends RecyclerView.ViewHolder{
        Button infoLinkButton;

        public SecondViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoLinkButton = itemView.findViewById(R.id.info_link_button);
        }
    }

}