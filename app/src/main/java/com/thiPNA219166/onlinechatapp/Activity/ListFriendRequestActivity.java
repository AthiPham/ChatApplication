package com.thiPNA219166.onlinechatapp.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.thiPNA219166.onlinechatapp.Adapter.FriendRequestFragmentAdapter;
import com.thiPNA219166.onlinechatapp.R;

import java.util.Objects;

public class ListFriendRequestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friend_request);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        FriendRequestFragmentAdapter fragmentAdapter= new FriendRequestFragmentAdapter(getSupportFragmentManager(),getLifecycle());
        ViewPager2 viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(fragmentAdapter);

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Đã gửi"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã nhận"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  // back to login in activity
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}