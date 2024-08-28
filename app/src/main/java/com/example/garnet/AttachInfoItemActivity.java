package com.example.garnet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttachInfoItemActivity extends AppCompatActivity {
    private List<InfoItem> mainList;
    private List<Boolean> isAttachedList;
    private GarnetDatabaseHelper mDatabaseHelper;
    public static final String TODO_ITEM = "TodoItem";
    public static final String INFO_GROUP = "InfoGroup";
    private TodoItem mTodoItem;
    private InfoGroup mInfoGroup;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item_select);

        mDatabaseHelper = new GarnetDatabaseHelper(AttachInfoItemActivity.this);

        rv = findViewById(R.id.select_info_item_rv);
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(AttachInfoItemActivity.this));

        mTodoItem = (TodoItem) getIntent().getSerializableExtra(TODO_ITEM);
        mInfoGroup = (InfoGroup) getIntent().getSerializableExtra(INFO_GROUP);

        mainList = mDatabaseHelper.loadInfo(mInfoGroup.getId());//读取所有的InfoItem，所有的InfoItem都显示出来
        isAttachedList = mDatabaseHelper.checkAttached(mTodoItem.getId(), mainList);//读取所有和这个TodoItem相关的

        TextView textView = findViewById(R.id.select_info_item_title_tv);
        textView.setText(mTodoItem.getTask());
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.selectable_info_item_card,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoItem infoItem = mainList.get(position);
            holder.checkBox.setText(infoItem.getDisplayString());
            holder.checkBox.setChecked(isAttachedList.get(position));
        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        private final CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.select_info_item_cb);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(!buttonView.isPressed()){
                    return;
                }//必须是点击触发，否则会触发多次插入，导致冗余

                int position = getAdapterPosition();
                InfoItem infoItem = mainList.get(position);
                if(isChecked){
                    mDatabaseHelper.insertAttachment(mTodoItem, infoItem);
                }else{
                    mDatabaseHelper.deleteAttachment(mTodoItem, infoItem);
                }
            });
        }
    }

}
