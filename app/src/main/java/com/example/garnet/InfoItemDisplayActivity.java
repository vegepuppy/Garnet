package com.example.garnet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class InfoItemDisplayActivity extends AppCompatActivity {
    public static final String INFO_GROUP_NAME = "InfoGroupName";
    public static final String INFO_GROUP_ID = "InfoGroupId";
    private List<LinkInfoItem> mainList;
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
        reFetchLinkTitle();
    }

    private void reFetchLinkTitle(){
        for(LinkInfoItem linkInfoItem : mainList){
            //如果url和display相等，那么就再获取一次
            if(!linkInfoItem.isLinkFetched()){
                Handler titleFetchHandler = new Handler(Looper.getMainLooper()){
                    public void handleMessage(Message msg){
                        if (msg.what == 1) {//用if替换掉原来的switch减少一个warning，兼顾安全性
                            FetchLinkMessageObject response = (FetchLinkMessageObject) msg.obj;
                            Log.d("TAG", "handleMessage...");
                            // 改成在这里Toast就可以了
                            if (!response.isValid) {
//                                Toast.makeText(InfoItemDisplayActivity.this,
//                                        linkInfoItem.getContent()+":无效链接！", Toast.LENGTH_SHORT).show();
                                linkInfoItem.setDisplayString("无效链接:" + linkInfoItem.getContent());
                                mDatabaseHelper.updateInfoItem(linkInfoItem, "无效链接:" + linkInfoItem.getContent());
                                myAdapter.notifyItemChanged(mainList.indexOf(linkInfoItem));
                                linkInfoItem.setLinkFetched(true);
                            } else if (!response.isSuccess) {
                                Toast.makeText(InfoItemDisplayActivity.this,
                                        linkInfoItem.getContent()+"获取网页信息超时失败！", Toast.LENGTH_SHORT).show();
                                linkInfoItem.setDisplayString(linkInfoItem.getContent());
                                linkInfoItem.setLinkFetched(false);
                            } else {
                                mDatabaseHelper.updateInfoItem(linkInfoItem, response.linkTitle);
                                linkInfoItem.setDisplayString(response.linkTitle);
                                Log.d("TAG", "linkInfoItem displayString set");
                            }
                        }
                    }
                };

                new Thread(new FetchLinkRunnable(titleFetchHandler, linkInfoItem.getContent())).start();
            }
        }
    }

    private class AddInfoItemFabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final AddInfoDialogFragment addInfoDialogFragment = new AddInfoDialogFragment();
            addInfoDialogFragment.setStateListener(new AddInfoDialogFragment.StateListener() {
                @Override
                public void onConfirmed(String uri) {
                    LinkInfoItem linkInfoItem = new LinkInfoItem(null,uri,infoGroupId, LinkInfoItem.LACK_ID);
                    // TODO: 2024-07-22 handler可能要封装 
                    Handler titleFetchHandler = new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg){
                            if (msg.what == 1) {//用if替换掉原来的switch减少一个warning，兼顾安全性
                                FetchLinkMessageObject response = (FetchLinkMessageObject) msg.obj;
                                Log.d("TAG","handleMessage...");
                                // 改成在这里Toast就可以了
                                if (!response.isValid) {
                                    Toast.makeText(InfoItemDisplayActivity.this, "无效链接！", Toast.LENGTH_SHORT).show();
                                    linkInfoItem.setDisplayString("无效链接:"+ linkInfoItem.getContent());
                                } else if (!response.isSuccess) {
                                    Toast.makeText(InfoItemDisplayActivity.this, "获取网页超时信息失败！", Toast.LENGTH_SHORT).show();
                                    linkInfoItem.setDisplayString(uri);
                                } else {
                                    linkInfoItem.setDisplayString(response.linkTitle);
                                    Log.d("TAG","linkInfoItem displayString set");
                                }
                                LinkInfoItem itemWithId = mDatabaseHelper.insertInfo(linkInfoItem);
                                updateMainList(itemWithId);
                            }
                        }
                    };
                    new Thread(new FetchLinkRunnable(titleFetchHandler,uri)).start();
                }
            });
            addInfoDialogFragment.show(InfoItemDisplayActivity.this.getSupportFragmentManager(), "TAG"); //这个tag是乱取的，小心
        }
    }

    private void updateMainList(LinkInfoItem linkInfoItem){
        mainList.add(linkInfoItem);
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
            LinkInfoItem linkInfoItem = mainList.get(position);
            holder.infoItemStringTextView.setText(linkInfoItem.getDisplayString());
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
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                LinkInfoItem linkInfoItem = mainList.get(position);
                String uriString = linkInfoItem.getContent();

                Uri webpage = Uri.parse(uriString);

                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    try {
                        startActivity(intent);
                    }catch (android.content.ActivityNotFoundException e){
                        Toast.makeText(InfoItemDisplayActivity.this, "无效链接！", Toast.LENGTH_SHORT).show();
                    }



            });
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                LinkInfoItem linkInfoItem = mainList.get(position);

                mainList.remove(position);
                myAdapter.notifyItemRemoved(position);

                Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "已删除："+ linkInfoItem.getDisplayString(),Snackbar.LENGTH_LONG);

                snackbar.setAction("撤销", v1 -> {
                    mainList.add(position, linkInfoItem);
                    myAdapter.notifyItemInserted(position);
                });

                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (event != DISMISS_EVENT_ACTION){//只要不是点击了撤销
                            mDatabaseHelper.deleteInfo(linkInfoItem);
                        }
                    }
                });
                snackbar.show();
                return true;
            });
        }
    }
}