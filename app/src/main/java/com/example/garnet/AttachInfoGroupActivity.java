package com.example.garnet;

import android.content.Intent;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AttachInfoGroupActivity extends AppCompatActivity {
    private List<InfoGroup> mainList;
    private List<Boolean> isAttachedList;
    private GarnetDatabaseHelper mDatabaseHelper;
    public static final String TODO_ITEM = "TodoItem";
    private TodoItem mTodoItem;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_info_group);

        mDatabaseHelper = new GarnetDatabaseHelper(AttachInfoGroupActivity.this);

        rv = findViewById(R.id.attach_info_group_rv);
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(AttachInfoGroupActivity.this));

        mTodoItem = (TodoItem) getIntent().getSerializableExtra(TODO_ITEM);

        TextView tv = findViewById(R.id.attach_info_group_tv);
        tv.setText(mTodoItem.getTask());
        mainList = mDatabaseHelper.loadInfoGroup();
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
        private final Button button;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.attach_info_group_name_tv);
            numTv = itemView.findViewById(R.id.attach_info_group_num_tv);
            button = itemView.findViewById(R.id.attach_info_group_btn_view);
        }
        public void initViews(InfoGroup infoGroup){
            nameTv.setText(infoGroup.getName());
            numTv.setText(mDatabaseHelper.countAttached(mTodoItem.getId(), infoGroup.getId()));
            button.setOnClickListener(v -> {
                Intent intent = new Intent();
                intent.setClass(AttachInfoGroupActivity.this, AttachInfoItemActivity.class);
                intent.putExtra(AttachInfoItemActivity.TODO_ITEM, mTodoItem);
                int position = getAdapterPosition();
                intent.putExtra(AttachInfoItemActivity.INFO_GROUP, (Serializable) mainList.get(position));
                startActivityForResult(intent,1);
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(this, "关联信息已保存！", Toast.LENGTH_SHORT).show();
        recreate();
    }
}