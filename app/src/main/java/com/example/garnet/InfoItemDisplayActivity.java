package com.example.garnet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InfoItemDisplayActivity extends AppCompatActivity {
    public static final String INFO_GROUP_NAME = "InfoGroupName";
    public static final String INFO_GROUP_ID = "InfoGroupId";
    private List<InfoItem> mainList;
    private MyAdapter myAdapter;
    private long infoGroupId;
    private GarnetDatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item_display);

        mDatabaseHelper = new GarnetDatabaseHelper(InfoItemDisplayActivity.this);

        Intent intent = getIntent();
        String infoGroupName = intent.getStringExtra(INFO_GROUP_NAME);
        this.infoGroupId = intent.getLongExtra(INFO_GROUP_ID,-1);
        //这里需要一个defaultValue，故设置为-1

        FloatingActionButton fab = findViewById(R.id.add_info_item_fab);
        fab.setOnClickListener(new AddInfoItemFabOnClickListener());

        TextView infoGroupNameTextView = findViewById(R.id.info_item_group_name_tv);
        infoGroupNameTextView.setText(infoGroupName);

        RecyclerView rv = findViewById(R.id.info_item_display_rv);
        myAdapter = new MyAdapter();
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(InfoItemDisplayActivity.this));

        mainList = mDatabaseHelper.loadInfo(infoGroupId);
    }

    private class AddInfoItemFabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final AddInfoDialogFragment df = new AddInfoDialogFragment();
            df.setStateListener(new AddInfoDialogFragment.StateListener() {
                @Override
                public void onConfirmed(String uri) {
                    InfoItem infoItem = new InfoItem(uri,infoGroupId,InfoItem.LACK_ID);
                    InfoItem itemWithId = mDatabaseHelper.insertInfo(infoItem);
                    updateMainList(itemWithId);
                }
            });
            df.show(InfoItemDisplayActivity.this.getSupportFragmentManager(), "TAG"); //这个tag是乱取的，小心
        }
    }

    private void updateMainList(InfoItem infoItem){
        mainList.add(infoItem);
        myAdapter.notifyItemInserted(mainList.size()-1); //这里是照搬原来的通知方法，不知道为什么要减一
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_card, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoItem infoItem = mainList.get(position);
            holder.infoItemStringTextView.setText(infoItem.getUri());
        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        public final TextView infoItemStringTextView;
        public final CardView infoItemCardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            infoItemStringTextView = itemView.findViewById(R.id.info_item_string_tv);
            infoItemCardView = itemView.findViewById(R.id.info_item_cardview);
            //设置短按跳转连接
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    InfoItem infoItem = mainList.get(position);
                    String uriString = infoItem.getUri();
                    // TODO: 2024-07-21 这里用了多线程打开，是否需要封装
                    Handler titleFetchHandler = new Handler(Looper.getMainLooper()){
                        public void handleMessage(Message msg){
                            switch (msg.what){
                                case 1:
                                    String response = (String)msg.obj;
                                    infoItemStringTextView.setText(response);
                            }
                        }
                    };
                    
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String titleFound = LinkTitleFetcher.biliTitleFetcher(uriString,
                                    InfoItemDisplayActivity.this);
                            
                            Message msg = new Message();
                            msg.what = 1;  // TODO: 2024-07-21 因为这里只有一个thread，所以设为1无妨，正常应该是常量 
                            msg.obj = titleFound;
                            titleFetchHandler.sendMessage(msg);
                        }
                    }).start();
                    

//                    Uri webpage = Uri.parse(uriString);
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//                    try {
//                        startActivity(intent);
//                    }catch (android.content.ActivityNotFoundException e){
//                        Toast.makeText(InfoItemDisplayActivity.this, "找不到网页", Toast.LENGTH_SHORT).show();
//                    }

                }
            });
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();

                mDatabaseHelper.deleteInfo(mainList.get(position));//这个函数有问题
                mainList.remove(position);//没问题
                myAdapter.notifyItemRemoved(position);//没问题

                return true;
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}