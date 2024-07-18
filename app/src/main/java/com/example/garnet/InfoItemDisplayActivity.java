package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class InfoItemDisplayActivity extends AppCompatActivity {
    public static final String INFO_GROUP_NAME = "InfoGroupName";
    private List<InfoItem> mainList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item_display);

        Intent intent = getIntent();
        String infoGroupName = intent.getStringExtra(INFO_GROUP_NAME);

        FloatingActionButton fab = findViewById(R.id.add_info_item_fab);
        fab.setOnClickListener(new AddInfoItemFabOnClickListener());

        TextView infoGroupNameTextView = findViewById(R.id.info_item_group_name_tv);
        infoGroupNameTextView.setText(infoGroupName);

        RecyclerView rv = findViewById(R.id.info_item_display_rv);
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(InfoItemDisplayActivity.this));

        mainList = DataBaseAction.Load.loadInfo(infoGroupName);
    }
    // TODO: 2024-07-18 这个对应的召唤出DialogFragment部分你补充下，谢谢。我估计xml文件也要重写.
    private class AddInfoItemFabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(InfoItemDisplayActivity.this, "fab Clicked", Toast.LENGTH_SHORT).show();
        }
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

    // TODO: 2024-07-18 加上长按或者左滑菜单
    private class MyViewHolder extends RecyclerView.ViewHolder{
        public final TextView infoItemStringTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            infoItemStringTextView = itemView.findViewById(R.id.info_item_string_tv);
        }
    }
}