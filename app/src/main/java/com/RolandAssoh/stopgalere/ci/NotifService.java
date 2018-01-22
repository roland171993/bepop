package com.RolandAssoh.stopgalere.ci;


import android.graphics.Color;
import android.support.v4.app.NotificationCompat;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;
import com.util.Constant;
import com.util.Prefs;

import java.math.BigInteger;

/**
 * Created by Obrina.KIMI on 9/1/2017.
 */

public class NotifService extends NotificationExtenderService {

    public NotifService() {
        super();
    }

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        // Read properties from result.
        OverrideSettings overrideSettings = new OverrideSettings();
        overrideSettings.extender = new NotificationCompat.Extender() {
            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                // Sets the background notification color to Green on Android 5.0+ devices.
                return builder.setColor(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR));
            }
        };

        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);

        // Return true to stop the notification from displaying.
        return true;


    }
}

