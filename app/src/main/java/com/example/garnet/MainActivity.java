package com.example.garnet;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    // 整个应用的主活动，包括导航栏和三个fragment的
    private HomeFragment homeFragment;
    private TodoFragment todoFragment;
    private InfoFragment infoFragment;
    private BottomNavigationView bottom_nv;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataBaseAction.closeDataBase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataBaseAction.init(MainActivity.this);

        bottom_nv = findViewById(R.id.main_bottom_nv);
        bottom_nv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // 在导航栏中的控件被用户选中时，执行此方法
                int id = menuItem.getItemId();
                if (id == R.id.bottom_home) {
                    selectedFragment(0);
                } else if (id == R.id.bottom_info) {
                    selectedFragment(1);
                } else if (id == R.id.bottom_todo) {
                    selectedFragment(2);
                }
                return true;
            }
        });
        selectedFragment(0);// 默认进入主页
    }

    public void selectedFragment (int position){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);

        if(position == 0){
            if(homeFragment == null){
                homeFragment = new HomeFragment();
                transaction.add(R.id.content, homeFragment);
            }else{
                transaction.show(homeFragment);
            }
        } else if (position == 1) {
            if(infoFragment == null){
                infoFragment = new InfoFragment();
                transaction.add(R.id.content, infoFragment);
            }else{
                transaction.show(infoFragment);
            }
        } else if (position == 2) {
            if(todoFragment == null){
                todoFragment = new TodoFragment();
                transaction.add(R.id.content, todoFragment);
            }else{
                transaction.show(todoFragment);
            }
        }
        transaction.commit();
    }

    private void hideFragment(FragmentTransaction transaction) {
        if(homeFragment != null){
            transaction.hide(homeFragment);
        }
        if(infoFragment != null){
            transaction.hide(infoFragment);
        }
        if(todoFragment != null){
            transaction.hide(todoFragment);
        }
    }
}