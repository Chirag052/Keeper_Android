package com.techone.keeper.activity;

import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.techone.keeper.adapter.AboutUsSliderAdapter;
import com.techone.keeper.R;

public class About_us extends AppCompatActivity {
    public static ViewPager viewPager;
    AboutUsSliderAdapter aboutUsSliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorwhite)); // Navigation bar the soft bottom of some phones like nexus and some Samsung note series
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary)); //status bar or the time bar at the top
        }


        viewPager=findViewById(R.id.viewpager);
        aboutUsSliderAdapter=new AboutUsSliderAdapter(this);
        viewPager.setAdapter(aboutUsSliderAdapter);

    }



}