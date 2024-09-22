package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.activity.result.ActivityResultLauncher;

import java.io.Serializable;
import java.util.List;

public class AttachInfoGroupActivity extends AppCompatActivity {
    private List<InfoGroup> mainList;
    private List<Boolean> isAttachedList;
    private MyAdapter myAdapter;
    private GarnetDatabaseHelper mDatabaseHelper;
    public static final String TODO_ITEM = "TodoItem";
    private TodoItem mTodoItem;
    private RecyclerView rv;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_info_group);

        mDatabaseHelper = new GarnetDatabaseHelper(AttachInfoGroupActivity.this);

        rv = findViewById(R.id.attach_info_group_rv);
        myAdapter= new MyAdapter();
        rv.setAdapter(myAdapter);
        rv.setLayoutManager(new LinearLayoutManager(AttachInfoGroupActivity.this));

        mTodoItem = (TodoItem) getIntent().getSerializableExtra(TODO_ITEM);

        TextView tv = findViewById(R.id.attach_info_group_tv);
        tv.setText(mTodoItem.getTask());
        mainList = mDatabaseHelper.loadInfoGroup();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> myAdapter.notifyDataSetChanged()); // TODO: 2024-09-22 不应该全部通知
    }
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.attach_info_group_card,parent,false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoGroup infoGroup = mainList.get(position);
            holder.initViews(infoGroup);

        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        private final TextView nameTv;
        private final TextView numTv;
        private final View itemView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.attach_info_group_name_tv);
            numTv = itemView.findViewById(R.id.attach_info_group_num_tv);
            this.itemView = itemView;
        }
        public void initViews(InfoGroup infoGroup){
            nameTv.setText(infoGroup.getName());
            numTv.setText(mDatabaseHelper.countAttached(mTodoItem.getId(), infoGroup.getId()));
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(AttachInfoGroupActivity.this, AttachInfoItemActivity.class);
                intent.putExtra(AttachInfoItemActivity.TODO_ITEM, mTodoItem);
                int position = getAdapterPosition();
                intent.putExtra(AttachInfoItemActivity.INFO_GROUP, (Serializable) mainList.get(position));
                activityResultLauncher.launch(intent);
            });
        }
    }
}