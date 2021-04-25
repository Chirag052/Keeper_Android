package com.techone.keeper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.techone.keeper.activity.LogIn;

public class SplashScreen extends AppCompatActivity {
    TextView appName;
    PrefManager prefrenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        prefrenceManager = new PrefManager(this);
        appName=(TextView)findViewById(R.id.appName);

        nameAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {

                    Intent intent= new Intent(SplashScreen.this, Dashboard.class);
                    startActivity(intent);
                    finish();
                } else {
                    if(prefrenceManager.isFirstTimeLaunch()) {


                        Intent intent= new Intent(SplashScreen.this, WelcomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent= new Intent(SplashScreen.this, LogIn.class);
                        startActivity(intent);
                        finish();
                    }
                }

            }
        },3000);
    }

    void nameAnimation() {
        for(int i=0;i<Constants.appName.length();i++) {
            final int x = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String currentText = appName.getText().toString();
                    appName.setText(currentText + Constants.appName.charAt(x));
                }
            },500 + 200*x);
        }
    }
}