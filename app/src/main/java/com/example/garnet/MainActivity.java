package com.example.garnet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // 整个应用的主活动，包括导航栏和三个fragment的
    private HomeFragment homeFragment;
    private TodoFragment todoFragment;
    private InfoFragment infoFragment;
    private SettingFragment settingFragment;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottom_nv = findViewById(R.id.main_bottom_nv);
        bottom_nv.setOnItemSelectedListener(menuItem -> {
            // 在导航栏中的控件被用户选中时，执行此方法
            int id = menuItem.getItemId();
            if (id == R.id.bottom_home) {
                replaceFragment(0);
            } else if (id == R.id.bottom_info) {
                replaceFragment(1);
            } else if (id == R.id.bottom_todo) {
                replaceFragment(2);
            } else if (id == R.id.bottom_settings) {
                replaceFragment(3);
            }
            return true;
        });
        replaceFragment(0);// 默认进入主页
    }

    public void replaceFragment(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (position){
            case 0:
                transaction.replace(R.id.content, new HomeFragment());
                break;
            case 1:
                transaction.replace(R.id.content, new InfoFragment());
                break;
            case 2:
                transaction.replace(R.id.content, new TodoFragment());
                break;
            case 3:
                transaction.replace(R.id.content, new SettingFragment());
                break;
            default:
                throw new IllegalArgumentException("invalid navigation position");
        }
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);// 设置过渡动画
        transaction.commit();
    }
}