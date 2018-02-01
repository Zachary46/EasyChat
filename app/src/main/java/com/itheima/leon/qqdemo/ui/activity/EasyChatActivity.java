package com.itheima.leon.qqdemo.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.itheima.leon.qqdemo.R;
import com.itheima.leon.qqdemo.ui.fragment.MyChatFragment;

public class EasyChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy_chat);
        MyChatFragment chatFragment=new MyChatFragment();
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.fg,chatFragment).commit();
    }
}
