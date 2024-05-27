package com.example.garnet;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private HomeFragment homeFragment;
    private TodoFragment todoFragment;
    private InfoFragment infoFragment;
    private BottomNavigationView bottom_nv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottom_nv = findViewById(R.id.main_bottom_nv);
        bottom_nv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
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
        selectedFragment(0);
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