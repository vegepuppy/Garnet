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

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InfoLinkActivity extends AppCompatActivity {
    public static final String INFO_POS = "info_pos";
    public static final String INFO_GROUP_NAME = "InfoGroupName";
    private String infoGroupName;
    private List<InfoItem> mainList = new ArrayList<>();
    // TODO: 2024-05-27 这里的link要改成一个类，要包含这个link 对应的名称，而不是显示一个连接

    private RecyclerView secondRecyclerView;
    private MyAdapter myAdapter;
//    private SQLiteDatabase db;
//    private Cursor cursor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBaseAction.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_link);

        //secondRecyclerView部分
        secondRecyclerView = findViewById(R.id.second_recyclerview);
        myAdapter = new MyAdapter();
        secondRecyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(InfoLinkActivity.this);
        secondRecyclerView.setLayoutManager(layoutManager);

        //FAB部分
        FABClickListener FABClickListener = new FABClickListener();
        FloatingActionButton floatingActionButton= findViewById(R.id.second_fab_add);
        floatingActionButton.setOnClickListener(FABClickListener);

        // 获得点击的标题名字
        DataBaseAction.init(InfoLinkActivity.this);
        infoGroupName = this.getIntent().getStringExtra(INFO_GROUP_NAME);
        mainList = DataBaseAction.Load.loadInfo(infoGroupName);
    }

    private class FABClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.second_fab_add) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoLinkActivity.this);
                builder.setTitle("创建");
                View addWindow = InfoLinkActivity.this.getLayoutInflater().inflate(R.layout.add_link_alartdialog, null);

                builder.setView(addWindow);
                builder.setPositiveButton("确定", null);
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = addWindow.findViewById(R.id.link_edit_text);
                        String uriInput = editText.getText().toString();

                        //判断标题不能为空
                        if(uriInput.trim().isEmpty()){
                            Toast.makeText(InfoLinkActivity.this,"链接不能为空",Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else{
                            InfoItem item = DataBaseAction.Insert.insertInfo(new InfoItem(uriInput.trim(), infoGroupName, InfoItem.LACK_ID));
                            mainList.add(item);

                            alertDialog.dismiss();
                        }
                    }
                });
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_card, parent, false);
            // 我不理解，不要动这里

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String url = mainList.get(position).getUri();
            holder.infoLinkTitleTextView.setText(url);
        }

        @Override
        public int getItemCount() {
            return mainList.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView infoLinkTitleTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoLinkTitleTextView = itemView.findViewById(R.id.info_item_string_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Uri uri = Uri.parse(mainList.get(position).getUri());
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

                    // 在数据库中删除
                    InfoItem itemToDelete = mainList.get(position);
                    DataBaseAction.Delete.deleteInfo(itemToDelete);
                    // 在List中删除
                    mainList.remove(position);

                    myAdapter.notifyItemRemoved(position);
                } else if (itemId == R.id.update) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoLinkActivity.this);
                    builder.setTitle("修改");
                    View addWindow = InfoLinkActivity.this.getLayoutInflater().inflate(R.layout.add_link_alartdialog, null);
                    builder.setView(addWindow);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = addWindow.findViewById(R.id.link_edit_text);

                            String newUri = editText.getText().toString();
                            InfoItem updatedItem = DataBaseAction.Update.updateInfoURI(mainList.get(position), newUri);
                            mainList.set(position, updatedItem);
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
                return true;
            }
        });
    }
}