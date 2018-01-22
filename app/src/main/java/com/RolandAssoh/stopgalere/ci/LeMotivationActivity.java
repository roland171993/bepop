package com.RolandAssoh.stopgalere.ci;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.InterstitialPlacement;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.util.Constant;
import com.util.DialogBuilder;
import com.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import data.LeMotisCompleteAdapter;
import data.LeMotisListViewAdapter;
import model.LettreMotivation;

import static com.util.Constant.ACTIVITY_NOT_FINISH;

public class LeMotivationActivity extends AppCompatActivity{

    private DialogBuilder requestAlert;
    private LeMotisListViewAdapter leMotisAdapter;
    private LeMotisCompleteAdapter leMotisCompAdapter;
    private ArrayList<LettreMotivation> arrayFastLeMotis = new ArrayList<>();
    private ArrayList<LettreMotivation> arrayAllLeMotis = new ArrayList<>();
    public Boolean part2Error , isFirst = true;
    public boolean isLoading = false;
    public View ftView;
    private ListView listView;
    private static int currentPosition;
    private Toolbar toolbar;
    public Handler leMotisHandler;
    private static int monetizerCode = 1;
    //Ad Admob
    private InterstitialAd leMotisInterstitial;
    private Handler adHandler;
    // IronSource
    private final static int REQUEST_READ_PHONE_STATE = 1;
    private static boolean isRwVideoWatched = false;
    //StartApp
    private StartAppAd startAppAd = new StartAppAd(this);
    public int count =0;
    private Boolean networkErr = false;
    public AsyncTask<String, Void, String> leMotisAsynFast, leMotisAsynComp;
    ProgressBar pbarLeMotis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_le_motivation);
        toolbar = (Toolbar) findViewById(R.id.leMotisToolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }
        getSupportActionBar().setTitle("Lettre de motivation");

        listView = (ListView) findViewById(R.id.listLeMotivationId);
        pbarLeMotis = (ProgressBar) findViewById(R.id.leMotisProgressBar);
        LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ftView = li.inflate(R.layout.footer_view, null);
        leMotisHandler = new MyHandler();
        leMotisAdapter = new LeMotisListViewAdapter(LeMotivationActivity.this, R.layout.lemotivation_row, arrayFastLeMotis);
        listView.setAdapter(leMotisAdapter);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Check when scroll to last item in listview, in this tut, init data in listview = 10 item
                if(view.getLastVisiblePosition() == totalItemCount-1 && listView.getCount() >= (arrayFastLeMotis.size()- 1) && isLoading == false) {
                    isLoading = true;

                    if (count < 2)
                    {
                        Thread thread = new ThreadGetMoreData();
                        //Start thread
                        thread.start();
                    }
//                    Log: count event
                    count++;
//                    Log.v("On scroll counter", String.valueOf(count));

                }

            }
        });

//                get All Lettres Motivations
        if (JsonUtils.isNetworkAvailable(LeMotivationActivity.this)) {
            leMotisAsynFast = new fastTask();
            leMotisAsynFast.execute(Constant.LETTRE_MOTIVATION_PART1_URL);
        } else {
            Toast.makeText(getApplicationContext(), "Pas de connection au Reseau!!!", Toast.LENGTH_LONG).show();
        }
        //Setup Advertise
        if (savedInstanceState == null){
            monetizerCode = App.getMonetizerId();
        }
        //load Advertise
        iniInterstitial();

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

    private	class completeTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            if (null == result || result.length() == 0) {
                //Can't contact server
                Toast.makeText(LeMotivationActivity.this,"Erreur de connection au serveur", Toast.LENGTH_LONG).show();
                networkErr = true;

            } else {

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        LettreMotivation mLeMotis= new LettreMotivation();

                        if(isCancelled())
                            break;

                        mLeMotis.setId(objJson.getString(Constant.LEMOTIVATION_ITEM_ID.trim()));
                        mLeMotis.setTitle(objJson.getString(Constant.LEMOTIVATION_ITEM_TITLE).trim());
                        mLeMotis.setContent(objJson.getString(Constant.LEMOTIVATION_ITEM_CONTENT));

                        arrayAllLeMotis.add(mLeMotis);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
//            Build ListView
        }

    }

    private void updateListView() {
        // clear old adapter
        leMotisAdapter = null;
        // change adapter
        leMotisCompAdapter = new LeMotisCompleteAdapter(LeMotivationActivity.this, R.layout.lemotivation_row, arrayAllLeMotis);
        listView.setAdapter(leMotisCompAdapter);
        leMotisCompAdapter.notifyDataSetChanged();
        if (listView.getFirstVisiblePosition() > currentPosition || listView.getLastVisiblePosition() < currentPosition)
            listView.setSelection(currentPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lemotivation, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchVleMotis = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchVleMotis.setQueryHint("Rechercher");
        searchVleMotis.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                String searchTxtLeMotis = newText.trim();
                ArrayList<LettreMotivation> searchedArLeMotis = new ArrayList<LettreMotivation>();
                if (leMotisAdapter != null)
                {
                    for (LettreMotivation dm : arrayFastLeMotis) {
                        if (dm.getTitle().toLowerCase()
                                .contains(newText.toLowerCase())) {
                            searchedArLeMotis.add(dm);
                        }
                    }
                    if (searchTxtLeMotis.isEmpty()) {
//                        show all emploi
                        listView.setAdapter(leMotisAdapter);
                    } else {
//                        display what user want
                        listView.setAdapter(new LeMotisListViewAdapter(LeMotivationActivity.this, R.layout.lemotivation_row, searchedArLeMotis));

                    }

                }else
                {
                    for (LettreMotivation dm : arrayAllLeMotis) {
                        if (dm.getTitle().toLowerCase()
                                .contains(newText.toLowerCase())) {
                            searchedArLeMotis.add(dm);
                        }
                    }
                    if (searchTxtLeMotis.isEmpty()) {
//                        show all emploi
                        listView.setAdapter(leMotisCompAdapter);
                    } else {
//                        display what user want
                        listView.setAdapter(new LeMotisCompleteAdapter(LeMotivationActivity.this, R.layout.lemotivation_row, searchedArLeMotis));

                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {

                //Add footer view after get data
                leMotisHandler.sendEmptyMessage(0);
                //Search more data
                ArrayList<LettreMotivation> lstResult = new ArrayList<>() ;
                //Delay time to show loading footer when debug, remove it when release
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Send the result to Handle
                Message msg = leMotisHandler.obtainMessage(1, lstResult);
            leMotisHandler.sendMessage(msg);

        }
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Add loading view during search processing
                    listView.addFooterView(ftView);
                    break;
                case 1:
                    //Update data adapter and UI
                    if (isFirst)
                    {
                        isFirst = false;
                    }else
                    {
//                        leMotisAdapter.addListItemToAdapter((ArrayList<LettreMotivation>)msg.obj);
                        // Build complete LeMotis ListView
                        if(!networkErr)
                        {
                            updateListView();
                        }
                    }
                    //Remove loading view after update listview
                    listView.removeFooterView(ftView);
                    isLoading=false;
                    break;
                default:
                    break;
            }
        }
    }


    private	class fastTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbarLeMotis.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
            pbarLeMotis.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pbarLeMotis.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                //Can't contact server
                requestAlert = new DialogBuilder(LeMotivationActivity.this, ACTIVITY_NOT_FINISH,
                        "Erreur de connection au serveur", "le serveur est peut être encours de maintenance ou Réseau faible. "
                         + getString(R.string.connection_tips));
                requestAlert.showDialog();
            } else {

                try {

                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        LettreMotivation lMotivation = new LettreMotivation();

                        if(isCancelled())
                            break;

                        lMotivation.setId(objJson.getString(Constant.LEMOTIVATION_ITEM_ID).trim());
                        lMotivation.setTitle(objJson.getString(Constant.LEMOTIVATION_ITEM_TITLE).trim());

                        arrayFastLeMotis.add(lMotivation);
//                            Log.v("All News Fragements", objJson.getString(Constant.CATEGORY_NAME));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Build
                leMotisAdapter.notifyDataSetChanged();
                currentPosition = arrayFastLeMotis.size() - 1;
//                get Lettre motivation part 2
                if (JsonUtils.isNetworkAvailable(LeMotivationActivity.this))
                {
                    if(!arrayFastLeMotis.isEmpty())
                    {
//                        log Request
//                        Log.v("LeMotis part2 Req", Constant.LETTRE_MOTIVATION_PART2_URL + arrayLeMotisPart1.get(arrayLeMotisPart1.size()- 1).getId());
                        leMotisAsynComp = new completeTask();
                        leMotisAsynComp.execute(Constant.LETTRE_MOTIVATION_COMP_URL);
                    }
                }
            }

        }
    }

    private void initAdmobInterstitial() {
        leMotisInterstitial = new InterstitialAd(this);
        leMotisInterstitial.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        leMotisInterstitial.loadAd(new AdRequest.Builder().build());

        leMotisInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // TODO Auto-generated method stub
                super.onAdLoaded();
                if (leMotisInterstitial.isLoaded()) {
                    leMotisInterstitial.show();
                }
            }
        });
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

    @Override
    public void onBackPressed() {
        LeMotivationActivity.this.finish();
        killAllAsyn();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        killAllAsyn();
        super.onDestroy();
    }

    private void killAllAsyn() {
//        kill asynTask
        if(!(null == leMotisAsynFast))
        {
            leMotisAsynFast.cancel(true);
        }
        if(!(null == leMotisAsynComp))
        {
            leMotisAsynComp.cancel(true);
        }
    }

}
