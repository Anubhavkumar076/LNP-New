package com.lnp.project.activity;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.lnp.project.LoginActivity;
import com.lnp.project.databinding.ActivitySplashScreenBinding;
import com.lnp.project.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreenActivity extends AppCompatActivity {
    Handler handler;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        sp = getSharedPreferences("login",MODE_PRIVATE);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                sendToLoginActivity();
                if(sp.getBoolean("logged",false) && sp.getBoolean("userInfoAvail",true)){
                    sendToMainActivity();
                } else if (!sp.getBoolean("userInfoAvail",true)) {
                    sendToUserInformationActivity();
                } else {
                    sendToLoginActivity();
                }

            }
        },2000);

    }

    private void sendToUserInformationActivity() {
        Intent i=new Intent(SplashScreenActivity.this, UserInformationActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void sendToMainActivity() {
        Intent i=new Intent(SplashScreenActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    private void sendToLoginActivity() {
        Intent intent=new Intent(SplashScreenActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}