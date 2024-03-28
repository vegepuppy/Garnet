package com.example.garnet;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import java.net.URL;
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
                        Uri uri = Uri.parse(db.getInfoItemList().get(linkPosition).getUrlList().get(position));
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        try{
                            startActivity(intent);
                        }catch (android.content.ActivityNotFoundException e){
                            Toast.makeText(InfoLinkActivity.this, "找不到网页", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        showPopupMenu(v,position);
                    }
                    return true;  //当返回true时，表示已经完整地处理了这个事件，并不希望其他的回调方法再次进行处理；当返回false时，表示并没有完全处理完该事件，更希望其他方法继续对其进行处理。
                }
            });
        }
    }
    private void showPopupMenu(View view, int position){
        PopupMenu popupMenu = new PopupMenu(InfoLinkActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popupmenu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.delete) {
                    Toast.makeText(InfoLinkActivity.this, "点击了删除", Toast.LENGTH_LONG).show();
                    db.getInfoItemList().get(linkPosition).getUrlList().remove(position);
                    myAdapter.notifyItemRemoved(position);
                } else if (itemId == R.id.modify) {
                    Toast.makeText(InfoLinkActivity.this, "点击了修改", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoLinkActivity.this);
                    builder.setTitle("修改");
                    View addWindow = InfoLinkActivity.this.getLayoutInflater().inflate(R.layout.adding_link_alartdialog, null);
                    builder.setView(addWindow);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        Toast toast = Toast.makeText(MainActivity.this,"点击了确定" , Toast.LENGTH_LONG);
//                        toast.show();
                            EditText editText = addWindow.findViewById(R.id.link_edit_text);
                            db.getInfoItemList().get(linkPosition).getUrlList().set(position,editText.getText().toString());
                            myAdapter.notifyItemChanged(position);
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
                // 杨峻越在这里写了一个没用的注释，用于试验
                return true;
            }
        });
    }

}