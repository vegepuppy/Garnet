package com.example.garnet;

import static com.example.garnet.InfoGroup.LACK_ID;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class InfoFragment extends Fragment {
    private RecyclerView infoItemListRecyclerView;
    private MyAdapter myAdapter;
    private List<InfoGroup> mainList;// 所有资料库的标题
    private GarnetDatabaseHelper mDatabaseHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        mDatabaseHelper = new GarnetDatabaseHelper(getActivity());

        //infoItem部分
        infoItemListRecyclerView = view.findViewById(R.id.info_item_recyclerview);
        myAdapter = new MyAdapter();
        infoItemListRecyclerView.setAdapter(myAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        infoItemListRecyclerView.setLayoutManager(layoutManager);

        //FAB部分
        MyClickListener myClickListener = new MyClickListener();
        FloatingActionButton floatingActionButton = view.findViewById(R.id.fab_add);
        floatingActionButton.setOnClickListener(myClickListener);

        ///SQLite数据库部分
        mainList = mDatabaseHelper.loadInfoGroup();

        return view;
    }


    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            final View addWindow = InfoFragment.this.getLayoutInflater().inflate(R.layout.add_info_alartdialog, null);
            final TextView tv = addWindow.findViewById(R.id.text_count);
            final EditText et = addWindow.findViewById(R.id.title_edit_text);

            if (v.getId() == R.id.fab_add) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("创建");

                // 设置对应的xml为窗口布局
                builder.setView(addWindow);

                // 创建一个监听器用于显示已输入字数
                et.addTextChangedListener(new TextLengthLimiter(tv));

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

                        // 判断标题不能为空
                        if (title.trim().isEmpty()) {
                            Toast.makeText(getActivity(), "标题不能为空!", Toast.LENGTH_SHORT).show();
                        }
                        //判断标题不能重复
                        else if (isNotRepeated(title)) {

                            InfoGroup groupWithId = mDatabaseHelper.insertInfoGroup(new InfoGroup(title, LACK_ID));
                            mainList.add(groupWithId);

                            alertDialog.dismiss();
                        }
                    }
                });
            }
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        //此处可能不安全
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //view = View.inflate(MainActivity.this,R.layout.layout_info_list_item,null);
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_list_item, parent, false);//生成实例
            // 我不理解，不要动这里

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            InfoGroup infoGroup = mainList.get(position);
            String title = infoGroup.getName(); // TODO: 2024-07-19 这里发现之前用的title，现在用的name，得规范一下
            holder.infoListTitleTextView.setText(title);
        }

        @Override
        public int getItemCount() {
            return mainList.size();
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
                        InfoGroup infoGroup = mainList.get(position);

                        Intent intent = new Intent();
                        intent.setClass(requireActivity(), InfoItemDisplayActivity.class);
                        intent.putExtra(InfoItemDisplayActivity.INFO_GROUP_ID, infoGroup.getId());
                        intent.putExtra(InfoItemDisplayActivity.INFO_GROUP_NAME, infoGroup.getName());
                        startActivity(intent);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        showPopupMenu(v, position);
                    }
                    return true;
                }
            });
        }


    }

    private void showPopupMenu(View view, int position) {
        // 长按弹出菜单
        PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_popupmenu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.delete) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("确定删除吗？");
                    builder.setPositiveButton("确定", (dialog, which) -> deleteInfoGroup(position));
                    builder.setNegativeButton("取消",null);
                    builder.show();

                } else if (itemId == R.id.update) {
                    //展示一个alertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("修改");
                    View addWindow = InfoFragment.this.getLayoutInflater().inflate(R.layout.add_info_alartdialog, null);
                    builder.setView(addWindow);

                    EditText editText = addWindow.findViewById(R.id.title_edit_text);

                    InfoGroup infoGroup = mainList.get(position);
                    String oldTitle = infoGroup.getName();
                    editText.getText().append(oldTitle);

                    int length = oldTitle.length();
                    editText.addTextChangedListener(new TextLengthLimiter(addWindow.findViewById(R.id.text_count), length));

                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                        Toast toast = Toast.makeText(MainActivity.this,"点击了确定" , Toast.LENGTH_LONG);
//                        toast.show();
                            InfoGroup infoGroup = mainList.get(position);
                            String newTitle = editText.getText().toString();
                            InfoGroup groupModified = mDatabaseHelper.updateInfoGroup(infoGroup, newTitle);
                            mainList.set(position, groupModified);
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

    private void deleteInfoGroup(int position) {
        // 在数据库中删除
        InfoGroup infoGroup = mainList.get(position);
        mDatabaseHelper.deleteInfoGroup(infoGroup);
        // 在List中删除
        mainList.remove(position);
        myAdapter.notifyItemRemoved(position);
    }

    private boolean isNotRepeated(String title) {
        // 判断标题是否重复，如果重复就输出提示信息并返回false，不重复返回true
        List<String> titleList = new ArrayList<String>() {{
            for (InfoGroup infoGroup : mainList) {
                add(infoGroup.getName());
            }
        }}; // TODO: 2024-07-19 为了实现判重，这里用了很愚蠢的方法，得改

        if (titleList.contains(title)) {
            Toast.makeText(getActivity(), "标题不能重复！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //由于不用关闭数据库，所以不需要再onDestroy中做任何事情
    }
}