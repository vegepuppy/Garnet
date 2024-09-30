package com.example.garnet;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * 用于展示一系列的InfoItem
 * */
@Deprecated
public class InfoItemDisplayFragment extends Fragment {
    private List<WebInfoItem> mainList;
    private View rootView;//整个fragment的UI
    private String infoGroupName = "SAMPLE TITLE #1";// TODO: 2024-07-18 改成与infoFragment通信

    public InfoItemDisplayFragment(String infoGroupName) {
        this.infoGroupName = infoGroupName;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_info_item_display, container, false);
        FloatingActionButton fab = rootView.findViewById(R.id.add_info_item_fab);
        fab.setOnClickListener(new AddInfoItemFabOnClickListener());

        RecyclerView rv = rootView.findViewById(R.id.info_item_display_rv);
        rv.setAdapter(new MyAdapter());
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));

//        mainList = DataBaseAction.Load.loadInfo(infoGroupName);
        return rootView;
    }

    // TODO: 2024-07-18 这个对应的召唤出DialogFragment部分你补充下，谢谢。我估计xml文件也要重写.
    private class AddInfoItemFabOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(requireActivity(), "fab Clicked", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // TODO: 2024-07-18 检查下这里，我记得用这个方法会导致问题
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item_card, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            WebInfoItem webInfoItem = mainList.get(position);
            holder.infoItemStringTextView.setText(webInfoItem.getContent());
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