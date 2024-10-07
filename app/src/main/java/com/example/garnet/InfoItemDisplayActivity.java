package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private List<InfoItem> mainList;
    private MyAdapter myAdapter;
    private long infoGroupId;
    private GarnetDatabaseHelper mDatabaseHelper;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private TranslateAnimation showFabAnimation, hideFabAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item_display);

        initAnimation();//初始化动画

        mDatabaseHelper = new GarnetDatabaseHelper(InfoItemDisplayActivity.this);

        Intent intent = getIntent();
        String infoGroupName = intent.getStringExtra(INFO_GROUP_NAME);
        this.infoGroupId = intent.getLongExtra(INFO_GROUP_ID,-1);
        //这里需要一个defaultValue，故设置为-1

        //注册ActivityResultLauncher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Toast.makeText(InfoItemDisplayActivity.this,"返回码" + result.getResultCode(), Toast.LENGTH_SHORT).show();
                    if (result.getResultCode() == NoteActivity.RESULT_NOTE_INSERT_CODE) {
                        mainList = mDatabaseHelper.loadInfo(infoGroupId);
                        myAdapter.notifyItemInserted(mainList.size());
                    } else if(result.getResultCode() == NoteActivity.RESULT_NOTE_UPDATE_CODE){
                        mainList = mDatabaseHelper.loadInfo(infoGroupId);
                        myAdapter.notifyDataSetChanged(); // TODO: 2024-09-22 如果不全局通知，就需要回传noteItem
                    }
                });


        FloatingActionButton addWebInfoItemFab = findViewById(R.id.add_link_fab);
        FloatingActionButton addInfoFab = findViewById(R.id.add_info_item_fab);
        FloatingActionButton addNoteInfoItemFab = findViewById(R.id.add_note_fab);
        FloatingActionButton clearFab = findViewById(R.id.clear_fab);

        addWebInfoItemFab.setOnClickListener(new AddWebInfoItemFabOnClickListener());

        addNoteInfoItemFab.setOnClickListener(v -> {
            NoteInfoItem noteInfoItem = new NoteInfoItem(null,null,infoGroupId,InfoItem.LACK_ID);
            noteInfoItem.show(InfoItemDisplayActivity.this, activityResultLauncher);//此处不保存到数据库，非空才保存
        });

        clearFab.setOnClickListener(v -> {
            addWebInfoItemFab.startAnimation(hideFabAnimation);
            clearFab.setVisibility(View.GONE);
            addWebInfoItemFab.setVisibility(View.GONE);

            addNoteInfoItemFab.startAnimation(hideFabAnimation);
            addNoteInfoItemFab.setVisibility(View.GONE);
            addInfoFab.setVisibility(View.VISIBLE);

        });

        addInfoFab.setOnClickListener(v -> {
            clearFab.setVisibility(View.VISIBLE);
            addWebInfoItemFab.setVisibility(View.VISIBLE);
            addWebInfoItemFab.startAnimation(showFabAnimation);

            addNoteInfoItemFab.setVisibility(View.VISIBLE);
            addNoteInfoItemFab.startAnimation(showFabAnimation);

            addInfoFab.setVisibility(View.GONE);
        });

        TextView infoGroupNameTextView = findViewById(R.id.info_item_group_name_tv);
        infoGroupNameTextView.setText(infoGroupName);

        RecyclerView rv = findViewById(R.id.info_item_display_rv);
        myAdapter = new MyAdapter();
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(InfoItemDisplayActivity.this));

        mainList = mDatabaseHelper.loadInfo(infoGroupId);
        reFetchLinkTitle();
    }

    private void initAnimation() {
        showFabAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                2,//fromXValue表示开始的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示结束的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示开始的Y轴位置
                Animation.RELATIVE_TO_SELF,
                0);//fromXValue表示结束的Y轴位置
        showFabAnimation.setRepeatMode(Animation.REVERSE);
        showFabAnimation.setDuration(300);

        hideFabAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                0,//fromXValue表示开始的X轴位置
                Animation.RELATIVE_TO_SELF,
                2,//fromXValue表示结束的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示开始的Y轴位置
                Animation.RELATIVE_TO_SELF,
                0);//fromXValue表示结束的Y轴位置
        hideFabAnimation.setRepeatMode(Animation.REVERSE);
        hideFabAnimation.setDuration(300);
    }

    private void reFetchLinkTitle(){
        for(InfoItem infoItem : mainList){
            if (infoItem instanceof WebInfoItem) {
                WebInfoItem webInfoItem = (WebInfoItem) infoItem;
                if(!webInfoItem.isLinkFetched()){
                    Handler titleFetchHandler = new Handler(Looper.getMainLooper()){
                        public void handleMessage(Message msg){
                            if (msg.what == 1) {//用if替换掉原来的switch减少一个warning，兼顾安全性
                                FetchLinkMessageObject response = (FetchLinkMessageObject) msg.obj;
                                Log.d("TAG", "handleMessage...");
                                // 改成在这里Toast就可以了
                                if (!response.isValid) {
    //                                Toast.makeText(InfoItemDisplayActivity.this,
    //                                        linkInfoItem.getContent()+":无效链接！", Toast.LENGTH_SHORT).show();
                                    webInfoItem.setDisplayString("无效链接:" + webInfoItem.getContent());
                                    mDatabaseHelper.updateInfoItem(webInfoItem, "无效链接:" + webInfoItem.getContent());
                                    myAdapter.notifyItemChanged(mainList.indexOf(webInfoItem));
                                    webInfoItem.setLinkFetched(true);
                                } else if (!response.isSuccess) {
                                    Toast.makeText(InfoItemDisplayActivity.this,
                                            webInfoItem.getContent()+"获取网页信息超时失败！", Toast.LENGTH_SHORT).show();
                                    webInfoItem.setDisplayString(webInfoItem.getContent());
                                    webInfoItem.setLinkFetched(false);
                                } else {
                                    mDatabaseHelper.updateInfoItem(webInfoItem, response.linkTitle);
                                    webInfoItem.setDisplayString(response.linkTitle);
                                    Log.d("TAG", "linkInfoItem displayString set");
                                }
                            }
                        }
                    };
                    new Thread(new FetchLinkRunnable(titleFetchHandler, webInfoItem.getContent())).start();
                }
            }
        }
    }

    private class AddWebInfoItemFabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final AddInfoDialogFragment addInfoDialogFragment = new AddInfoDialogFragment();
            addInfoDialogFragment.setStateListener(new AddInfoDialogFragment.StateListener() {
                @Override
                public void onConfirmed(String uri) {
                    WebInfoItem webInfoItem = new WebInfoItem(null,uri,infoGroupId, WebInfoItem.LACK_ID);
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
                                    webInfoItem.setDisplayString("无效链接:"+ webInfoItem.getContent());
                                } else if (!response.isSuccess) {
                                    Toast.makeText(InfoItemDisplayActivity.this, "获取网页超时信息失败！", Toast.LENGTH_SHORT).show();
                                    webInfoItem.setDisplayString(uri);
                                } else {
                                    webInfoItem.setDisplayString(response.linkTitle);
                                    Log.d("TAG","linkInfoItem displayString set");
                                }
                                InfoItem itemWithId = mDatabaseHelper.insertInfoItem(webInfoItem);
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
            holder.infoItemStringTextView.setText(infoItem.getDisplayString());
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
                InfoItem infoItem = mainList.get(position);
                infoItem.show(InfoItemDisplayActivity.this, activityResultLauncher);
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                InfoItem infoItem = mainList.get(position);

                mainList.remove(position);
                myAdapter.notifyItemRemoved(position);

                Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), "已删除："+ infoItem.getDisplayString(),Snackbar.LENGTH_LONG);

                snackbar.setAction("撤销", v1 -> {
                    mainList.add(position, infoItem);
                    myAdapter.notifyItemInserted(position);
                });

                snackbar.addCallback(new Snackbar.Callback(){
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);

                        if (event != DISMISS_EVENT_ACTION){//只要不是点击了撤销
                            mDatabaseHelper.deleteInfo(infoItem);
                        }
                    }
                });
                snackbar.show();
                return true;
            });
        }
    }

}