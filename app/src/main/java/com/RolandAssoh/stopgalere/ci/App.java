package com.RolandAssoh.stopgalere.ci;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.ironsource.mediationsdk.IronSource;
import com.onesignal.OneSignal;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.util.Constant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import model.CV;
import model.Emploi;
import model.LettreMotivation;


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

    public static ArrayList<LettreMotivation> leMoJsonToList(@NonNull final JSONArray jsonArray){
        ArrayList<LettreMotivation> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = null;
                objJson = jsonArray.getJSONObject(i);

                LettreMotivation lm = new LettreMotivation();
                Log.d("server", "onPostExecute: " + jsonArray);

                if (lm.fillFromJSON(objJson)){
                    // Remplissage Succes
                    list.add(lm);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }catch (Error er){
            er.printStackTrace();
        }
        return list;
    }

    public static ArrayList<CV> cVJsonToList(@NonNull final JSONArray jsonArray){
        ArrayList<CV> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = null;
                objJson = jsonArray.getJSONObject(i);

                CV mCv = new CV();
                Log.d("server", "onPostExecute: " + jsonArray);

                if (mCv.fillFromJSON(objJson)){
                    // Remplissage Succes
                    list.add(mCv);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }catch (Error er){
            er.printStackTrace();
        }
        return list;
    }

    public static ArrayList<Emploi> emploisJsonToList(@NonNull final JSONArray jsonArray){
        ArrayList<Emploi> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = null;
                objJson = jsonArray.getJSONObject(i);

                Emploi emploi = new Emploi();
                Log.d("server", "onPostExecute: " + jsonArray);

                if (emploi.fillFromJSON(objJson)){
                    // Remplissage Succes
                    list.add(emploi);
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }catch (Error er){
            er.printStackTrace();
        }
        return list;
    }

    public  static int getMonetizerId() {
        return monetizerId;
    }

    public  static void setMonetizerId(int monetizerId) {
        App.monetizerId = monetizerId;
    }
}
