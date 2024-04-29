package com.example.garnet;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
    public static final String VAR_NAME_IN_INTENT = "CORR_TITLE";
    private String corrTitle;
    private List<String> uriList = new ArrayList<>();
    private RecyclerView secondRecyclerView;
    private MyAdapter myAdapter;
    private SQLiteDatabase db;
    private Cursor cursor;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cursor.close();
        db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_link);
        Intent intent = getIntent();

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

        // 获得点击的标题名字
        corrTitle = this.getIntent().getStringExtra(VAR_NAME_IN_INTENT);

        // 数据库部分
        try{
            SQLiteOpenHelper sqLiteOpenHelper = new GarnetDatabaseHelper(this);
            db = sqLiteOpenHelper.getWritableDatabase();
            cursor = db.query("LINK",
                    new String[]{"_id","URI","BELONG"},
                    "BELONG = ?",new String[]{corrTitle},null,null,null);

            //把数据库中的信息全读取到List中去
            if(cursor.moveToFirst()){
                do{
                    //符合BELONG的话，存进来
                    String titleFound = cursor.getString(1);
                    uriList.add(titleFound);
                }while(cursor.moveToNext());
            }
        }catch(SQLException e){
            Toast.makeText(InfoLinkActivity.this, "数据库不可用",Toast.LENGTH_SHORT);
        }
    }

    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.second_fab_add) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoLinkActivity.this);
                builder.setTitle("创建");
                View addWindow = InfoLinkActivity.this.getLayoutInflater().inflate(R.layout.adding_link_alartdialog, null);

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
                            uriList.add(uriInput.trim());

                            ContentValues contentValues = new ContentValues();
                            contentValues.put("URI",uriInput);
                            contentValues.put("BELONG",corrTitle);
                            db.insert("LINK",null,contentValues);

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_link_item, parent, false);
            // 我不理解，不要动这里

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            String url = uriList.get(position);
            holder.infoLinkTitleTextView.setText(url);
        }

        @Override
        public int getItemCount() {
            return uriList.size();
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
                        Uri uri = Uri.parse(uriList.get(position));
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
                    db.execSQL("DELETE FROM LINK WHERE URI = ?",new String[]{uriList.get(position)});
                    // 在List中删除
                    uriList.remove(position);

                    myAdapter.notifyItemRemoved(position);
                } else if (itemId == R.id.modify) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoLinkActivity.this);
                    builder.setTitle("修改");
                    View addWindow = InfoLinkActivity.this.getLayoutInflater().inflate(R.layout.adding_link_alartdialog, null);
                    builder.setView(addWindow);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            EditText editText = addWindow.findViewById(R.id.link_edit_text);
                            String oldUri = uriList.get(position);
                            String newUri = editText.getText().toString();

                            db.execSQL("UPDATE TITLE SET NAME = ? WHERE NAME = ?",
                                    new String[]{newUri,oldUri});
                            uriList.set(position, newUri);
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