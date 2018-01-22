package com.RolandAssoh.stopgalere.ci;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.util.Constant;
import com.util.Prefs;


public class SplashScreenActivity extends Activity {
    Boolean isFirstTime;
    Prefs settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }

        settings = new Prefs(SplashScreenActivity.this);
        isFirstTime = settings.getFirstLaunch();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!isFirstTime)
                {
                    startActivity(new Intent(SplashScreenActivity.this, WizardShopActivity.class));
                    Prefs memory = new Prefs(SplashScreenActivity.this);
                    memory.setFirstLaunch(true);
                }
                else
                {
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
                finish();
            }
        }, 2000);



    }

}
