package com.RolandAssoh.stopgalere.ci;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.ironsource.mediationsdk.IronSource;
import com.onesignal.OneSignal;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.util.Constant;


/**
 * Created by Obrina.KIMI on 9/22/2017.
 */

public class App extends Application {
    private static int monetizerId = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(this,
                getString(R.string.admob_app_id));
    }

    public  static int getMonetizerId() {
        return monetizerId;
    }

    public  static void setMonetizerId(int monetizerId) {
        App.monetizerId = monetizerId;
    }
}
