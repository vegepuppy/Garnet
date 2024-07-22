package com.example.garnet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InfoItemSelectActivity extends AppCompatActivity {
    private List<InfoItem> mainList;
    private GarnetDatabaseHelper mDatabaseHelper;
    public static final String TODO_ITEM_ID = "TodoItemId";
    private long mTodoItemId;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_item_select);

        mDatabaseHelper = new GarnetDatabaseHelper(InfoItemSelectActivity.this);

        FloatingActionButton fab = findViewById(R.id.confirm_item_select_fab);
        fab.setOnClickListener(new ConfirmListener());

        rv = findViewById(R.id.select_info_item_rv);
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(InfoItemSelectActivity.this));

        mTodoItemId = getIntent().getLongExtra(TODO_ITEM_ID,-1);

        mainList = mDatabaseHelper.loadInfo();
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
        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder{
        public final CheckBox checkBox;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.select_info_item_cb);
        }
    }

    private class ConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            List<Long> selectedItemIdList = new ArrayList<>();
            for (int i = 0; i < Objects.requireNonNull(rv.getAdapter()).getItemCount(); i++) {
                CheckBox itemCheckbox = (CheckBox) rv.getChildAt(i);
                if (itemCheckbox.isChecked()){
                    selectedItemIdList.add(mainList.get(i).getId());
                    Log.d("TAG",mainList.get(i).getDisplayString()+" selected by user.");
                }
            }
            Log.d("TAG","preparing to save the above to database");
            mDatabaseHelper.updateAttachment(mTodoItemId, selectedItemIdList);
        }
    }
}
