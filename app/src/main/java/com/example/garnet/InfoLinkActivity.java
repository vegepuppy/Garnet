package com.example.garnet;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InfoLinkActivity extends AppCompatActivity {
    public static final String INFO_POS = "info_pos";
    public static final String INFO_LIST = "info_list";
    private int linkPosition;
    private RecyclerView secondRecyclerView;
    private MyAdapter myAdapter;
    private DataBase db = new DataBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_link);
        Intent intent = getIntent();
        linkPosition = intent.getIntExtra(INFO_POS, -1);

        //secondRecyclerView部分
        secondRecyclerView = findViewById(R.id.second_recyclerview);
        myAdapter = new MyAdapter();
        secondRecyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(InfoLinkActivity.this);
        secondRecyclerView.setLayoutManager(layoutManager);

        //FAB部分
        MyClickListener myClickListener = new MyClickListener();
        FloatingActionButton floatingActionButton= findViewById(R.id.second_fab_add);
        floatingActionButton.setOnClickListener(myClickListener);

    }

    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.second_fab_add) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoLinkActivity.this);
                builder.setTitle("创建");
                View addWindow = InfoLinkActivity.this.getLayoutInflater().inflate(R.layout.adding_link_alartdialog, null);
                builder.setView(addWindow);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Toast toast = Toast.makeText(MainActivity.this,"点击了确定" , Toast.LENGTH_LONG);
//                        toast.show();
                        EditText editText = addWindow.findViewById(R.id.link_edit_text);
                        db.getInfoItemList().get(linkPosition).getUrlList().add(editText.getText().toString());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }



    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        //此处可能不安全
        private View view;
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //view = View.inflate(MainActivity.this,R.layout.layout_info_list_item,null);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_link_item, parent, false);
            // 我不理解，不要动这里

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String url = db.getInfoItemList().get(linkPosition).getUrlList().get(position);
            holder.infoLinkTitleTextView.setText(url);
        }

        @Override
        public int getItemCount() {
            return db.getInfoItemList().get(linkPosition).getUrlList().size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView infoLinkTitleTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoLinkTitleTextView = itemView.findViewById(R.id.info_link_textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        InfoItem infoItem = db.getInfoItemList().get(position);
                        Toast toast = Toast.makeText(InfoLinkActivity.this, "Clicked!!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }
    }
}