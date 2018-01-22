package com.RolandAssoh.stopgalere.ci;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.util.Constant;
import com.util.DialogBuilder;
import com.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import model.LettreMotivation;


import static com.util.Constant.ACTIVITY_FINISH;

public class LeMotisDetailActivity extends AppCompatActivity {

    private LettreMotivation leMotivation;
    private DialogBuilder connectionAlert;
    private Toolbar toolbar;
    private TextView content, title;
    private InterstitialAd detsInterstitial;
    public static int leMotisAdCounter = 0;
    private static int monetizerCode = 1;
    //Admob variable
    private InterstitialAd mInterstitialAd;
    //StartApp
    private StartAppAd startAppAd = new StartAppAd(this);
    //IrSource
    private Handler adHandler;
    ProgressBar pbarLeMotisDets;
    private ScrollView mScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_motis_detail);
        toolbar = (Toolbar) findViewById(R.id.leMotisDetsToolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }
        getSupportActionBar().setTitle("Lettre de motivation");
        title = (TextView) findViewById(R.id.leMotisDetsTitleId);
        pbarLeMotisDets = (ProgressBar) findViewById(R.id.leMotisDetsPrBar);
        mScrollView = (ScrollView) findViewById(R.id.leMotisDetsSrViewId);

//        get Serializable from Intent
        leMotivation = (LettreMotivation) getIntent().getSerializableExtra("leMotisObj");

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            String preview = bundle.getString("lmDlOk");
            if(preview != null && preview.equals("1"))
            {
//                All lettre de motivation downloaded is complete
                content = (TextView) findViewById(R.id.leMotisContentId);
                content.setText(leMotivation.getContent());

            }
            else
            {
                //        get  Lettre motivations Details
                new getDescriptionTask().execute(Constant.LETTRE_MOTIVATION_DESC_URL + leMotivation.getId());
            }
        }
        title.setText(leMotivation.getTitle());
        //Setup Advertise
        if (savedInstanceState == null){
            monetizerCode = App.getMonetizerId();
        }
        if(leMotisAdCounter == 3)
        {
            //load Advertise
            iniInterstitial();
        }

    }

    private void iniInterstitial() {
        if (monetizerCode == Constant.AD_NETWORK_STARTAPP)
        {
            initStAppInterstitial();
        }
        else if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE)
        {
            initISInterstitial();
        }
        else
        {
            initAdmobInterstitial();
        }
    }

    private void initAdmobInterstitial() {
        //Show Interstitial
        detsInterstitial = new InterstitialAd(this);
        detsInterstitial.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        detsInterstitial.loadAd(new AdRequest.Builder().build());

        detsInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                if (detsInterstitial.isLoaded()) {
                    detsInterstitial.show();
                }
            }
        });
    }

    private void clearIntent() {
        //        clearn intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String content = bundle.getString("lmDlOk");
            if (content != null) {
                getIntent().removeExtra("lmDlOk");
            }
        }

    }

    @Override
    public void onBackPressed() {
        // kill activity
        leMotisAdCounter ++;
        clearIntent();
        LeMotisDetailActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onRestart() {
        //Show Interstitial
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        clearIntent();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE){
            IronSource.onPause(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE){
            IronSource.onResume(this);
        }
    }

    private class getDescriptionTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbarLeMotisDets.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
            pbarLeMotisDets.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pbarLeMotisDets.setVisibility(View.INVISIBLE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                connectionAlert = new DialogBuilder(LeMotisDetailActivity.this, ACTIVITY_FINISH,
                        "Erreur de connection au serveur", getString(R.string.connection_tips));
                connectionAlert.showDialog();
            }
            else
            {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objson = null;
                    content = (TextView) findViewById(R.id.leMotisContentId);


                    objson = jsonArray.getJSONObject(0);

                    content.setText(objson.getString(Constant.LEMOTIVATION_ITEM_CONTENT));

//                    Log.v("Description", objson.getString(Constant.EMPLOI_ITEM_DESCRI));



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }
    }

    private void initISInterstitial(){
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
                adHandler = new Handler();
                adHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        IronSource.showInterstitial(getString(R.string.irsource_interstitial_placement));
                    }
                }, 1000);
            }
            /**
             invoked when there is no Interstitial Ad available after calling load function.
             */
            @Override
            public void onInterstitialAdLoadFailed(IronSourceError error) {
            }
            /**
             Invoked when the Interstitial Ad Unit is opened
             */
            @Override
            public void onInterstitialAdOpened() {
            }
            /*
             * Invoked when the ad is closed and the user is about to return to the application.
             */
            @Override
            public void onInterstitialAdClosed() {
            }
            /*
             * Invoked when the ad was opened and shown successfully.
             */
            @Override
            public void onInterstitialAdShowSucceeded() {
            }
            /**
             * Invoked when Interstitial ad failed to show.
             // @param error - An object which represents the reason of showInterstitial failure.
             */
            @Override
            public void onInterstitialAdShowFailed(IronSourceError error) {
            }
            /*
             * Invoked when the end user clicked on the interstitial ad.
             */
            @Override
            public void onInterstitialAdClicked() {
            }
        });

        IronSource.loadInterstitial();
    }

    private void initStAppInterstitial() {
        startAppAd.showAd(new AdDisplayListener() {
            @Override
            public void adHidden(Ad ad) {

            }

            @Override
            public void adDisplayed(Ad ad) {

            }

            @Override
            public void adClicked(Ad ad) {

            }

            @Override
            public void adNotDisplayed(Ad ad) {
            }
        });
    }

}
