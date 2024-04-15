package com.example.garnet;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InfoListActivity extends AppCompatActivity  {
    private RecyclerView infoItemListRecyclerView;
    private myAdapter myAdapter;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //infoItem部分
        infoItemListRecyclerView = findViewById(R.id.info_item_recyclerview);
        myAdapter = new myAdapter();
        infoItemListRecyclerView.setAdapter(myAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(InfoListActivity.this);
        infoItemListRecyclerView.setLayoutManager(layoutManager);

        //FAB部分
        MyClickListener myClickListener = new MyClickListener();
        FloatingActionButton floatingActionButton= findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(myClickListener);

    }


    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            final View addWindow = InfoListActivity.this.getLayoutInflater().inflate(R.layout.adding_info_alartdialog,null);
            final int MAX_LENGTH = 20;
            final TextView tv = addWindow.findViewById(R.id.text_count);
            final EditText et = addWindow.findViewById(R.id.title_edit_text);


            if (v.getId() == R.id.fab_add) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoListActivity.this);

                builder.setTitle("创建");

                // 设置对应的xml为窗口布局
                builder.setView(addWindow);

                // 创建一个监听器用于显示已输入字数
                et.addTextChangedListener(new TextWatcher() {
                    private CharSequence wordNum;//记录输入的字数
                    private int selectionStart;
                    private int selectionEnd;
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        wordNum= s;//实时记录输入的字数
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        tv.setText(s.length() + "/" + MAX_LENGTH);
                    }
                });

                // 设置确定按钮，因为需要点击之后不消失，所以需要另外写Listener
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
                        String title = et.getText().toString();

                        //判断标题不能为空
                        if(title.trim().isEmpty()){
                            Toast.makeText(InfoListActivity.this,"标题不能为空",Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else{
                            InfoItem newInfoItem = new InfoItem(et.getText().toString());
                            db.getInfoItemList().add(newInfoItem);
                            alertDialog.dismiss();
                        }
                    }
                });


            }
        }
    }

    private class myAdapter extends RecyclerView.Adapter<MyViewHolder> {
        //此处可能不安全
        private View view;
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //view = View.inflate(MainActivity.this,R.layout.layout_info_list_item,null);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_list_item, parent, false);
            // 我不理解，不要动这里

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoItem infoItem = db.getInfoItemList().get(position);
            holder.infoListTitleTextView.setText(infoItem.getTitle());
        }

        @Override
        public int getItemCount() {
            return db.getInfoItemList().size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView infoListTitleTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.infoListTitleTextView = itemView.findViewById(R.id.text_in_card);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        InfoItem infoItem = db.getInfoItemList().get(position);
                        Toast toast = Toast.makeText(InfoListActivity.this, infoItem.getTitle(), Toast.LENGTH_LONG);
                        toast.show();
                        Intent intent = new Intent(InfoListActivity.this,InfoLinkActivity.class);

                        // 生成一个intent
                        intent.putExtra(InfoLinkActivity.INFO_POS, position);
                        startActivity(intent);
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
                    return true;
                }
            });
        }
    }
    private void showPopupMenu(View view, int position){
        PopupMenu popupMenu = new PopupMenu(InfoListActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popupmenu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.delete) {
                    Toast.makeText(InfoListActivity.this, "点击了删除", Toast.LENGTH_LONG).show();
                    db.getInfoItemList().remove(position);
                    myAdapter.notifyItemRemoved(position);
                } else if (itemId == R.id.modify) {
                    Toast.makeText(InfoListActivity.this, "点击了修改", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(InfoListActivity.this);
                    builder.setTitle("修改");
                    View addWindow = InfoListActivity.this.getLayoutInflater().inflate(R.layout.adding_info_alartdialog, null);
                    builder.setView(addWindow);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        Toast toast = Toast.makeText(MainActivity.this,"点击了确定" , Toast.LENGTH_LONG);
//                        toast.show();
                            EditText editText = addWindow.findViewById(R.id.title_edit_text);
                            db.getInfoItemList().get(position).setTitle(editText.getText().toString());
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
