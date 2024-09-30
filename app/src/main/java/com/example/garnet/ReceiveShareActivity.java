package com.example.garnet;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.garnet.utils.LogUtils;


public class ReceiveShareActivity extends AppCompatActivity{
    private GarnetDatabaseHelper helper;
    public static final String CONTENT = "CONTENT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_share);
        helper = new GarnetDatabaseHelper(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        InfoFragment infoFragment = new InfoFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CONTENT, getSharedUri());
        infoFragment.setArguments(bundle);

        transaction.add(R.id.receive_content, infoFragment);
        transaction.show(infoFragment);
        transaction.commit();
    }

    // 获得从其他app中分享的Uri字符串
    private String getSharedUri() throws NullPointerException{
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String content = null;

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                content = intent.getStringExtra(Intent.EXTRA_TEXT);
                if (content == null){
                    throw new NullPointerException("null share intent content");
                }
            }
        }
        return content;
    }
}