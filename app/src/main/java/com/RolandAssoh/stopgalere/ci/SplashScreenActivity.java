package com.RolandAssoh.stopgalere.ci;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.WindowManager;

import com.util.Constant;
import com.util.PermissionUtil;
import com.util.Prefs;

import java.util.concurrent.TimeUnit;


public class SplashScreenActivity extends Activity {
    Boolean isdFirstTime;
    Prefs settings;
    private static boolean isAllPermissionGranted = false;
    private static boolean showPermissionDialog;
    private static boolean isPFirstTime = true;
    private PermissionUtil pUtil;

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
        isdFirstTime = settings.getFirstLaunch();

        //check permission
        pUtil = new PermissionUtil(this);
        isAllPermissionGranted = pUtil.isAllPermissionsGranded();
        if(!isAllPermissionGranted){
            if(isPFirstTime){
                showPermissionDialog = false;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onRequestAllPermissions();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, TimeUnit.SECONDS.toMillis(1));
                isPFirstTime = false;
            }
        }else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startSession();
                }
            }, 2000);
        }



    }

    public void onRequestAllPermissions(){
        //request Permission
        pUtil.requestAllPermissions();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(showPermissionDialog)
            pUtil.requestAllPermissions();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.REQUEST_GROUP_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                    showPermissionDialog = false;
                    isAllPermissionGranted = true;

                    startSession();


                } else {
                    showPermissionDialog = true;
                    // Display on Next launch
                    isPFirstTime = true;
                    try{
                        pUtil.onAllPermissionError(pUtil.getPermissionsDenied());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startSession() {
        if(!isdFirstTime)
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


}
