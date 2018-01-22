package com.RolandAssoh.stopgalere.ci;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.util.Constant;
import com.util.JsonUtils;
import com.util.Prefs;

public class AboutActivity extends AppCompatActivity {
    private ImageView imgLogo;
    private TextView appName;
    private TextView appVersion;
    private TextView appSlogan;
    private TextView appGGU;
    private CheckBox mNotifPrefs;
    private WebView cguWebSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }

        mNotifPrefs = (CheckBox) findViewById(R.id.abtRNotifCBoxId);
        cguWebSite = (WebView) findViewById(R.id.abtCGUWSiteId) ;

        //Show notification choice
        loadPreferences();

        mNotifPrefs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs notifPrefs = new Prefs(AboutActivity.this);
                notifPrefs.setReceiveNotif(mNotifPrefs.isChecked());

                mNotifPrefs.setChecked(notifPrefs.getReceiveNotif());

            }
        });

        if (JsonUtils.isNetworkAvailable(this)) {
            //Display CGU
            cguWebSite.loadUrl(Constant.APP_CGU_URL);
            cguWebSite.getSettings().setJavaScriptEnabled(true);
            cguWebSite.setWebViewClient(new WebViewClient());

        }




    }
    public void loadPreferences()
    {
        Prefs prefsLoaded = new Prefs(this);
        mNotifPrefs.setChecked(prefsLoaded.getReceiveNotif());

    }

    @Override
    public void onBackPressed() {
        AboutActivity.this.finish();
        super.onBackPressed();
    }
}
