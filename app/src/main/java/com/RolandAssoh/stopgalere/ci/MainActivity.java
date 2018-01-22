package com.RolandAssoh.stopgalere.ci;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.ironsource.mediationsdk.EBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.model.Placement;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.ironsource.mediationsdk.sdk.RewardedVideoListener;
import com.startapp.android.publish.ads.banner.Banner;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.VideoListener;
import com.startapp.android.publish.adsCommon.adListeners.AdEventListener;
import com.util.Constant;
import com.util.DialogBuilder;
import com.util.JsonUtils;
import com.google.android.gms.ads.MobileAds;
import com.util.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import data.EmploisCompleteAdapter;
import data.EmploisLatestAdapter;
import data.DrawerShopAdapter;
import model.Emploi;
import model.MenuModel;
//Admob import

import static com.util.Constant.ACTIVITY_FINISH;
import static com.util.Constant.ACTIVITY_NOT_FINISH;
import static com.util.Constant.MAP_ACCOUNT_URL;


public class MainActivity extends AppCompatActivity {

	private ListView mDrawerList;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
    private FloatingActionButton fab;
    private RewardedVideoAd mRewardedVideoAd;
    public  Boolean adIsBannish;
    private Boolean adWillBug = false;

    private DialogBuilder connectionAlert, requestAlert;
    private DialogBuilder.Update updateAlert;
    private EmploisLatestAdapter emploiLastAdapter;
    private EmploisCompleteAdapter emploiCompAdapter;
    private ArrayList<Emploi> empArrayLast = new ArrayList<>(), empArrayComp = new ArrayList<>(), searchedArray;
    private ListView listView;
    private String searchInput = "";
    public AsyncTask<String, Void, String> asynLast, asynComp, asynUpdate, asynMonetize;
    public AsyncTask<String, Void, String> mapInfo, mapAccount;
    private static int monetizerCode = 1;
    protected static boolean isRwVideoWatched = false, isAdvertiserReqAllow = false;
    protected static int mapCount = 0;
    protected static boolean isMaxUse = false;
    static final int REQUEST_MAIN_MAP = 1;
    private Handler adHandler;
    JsonUtils util;
    ProgressBar lastBar, compBar;
    Handler mHandler;
    Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        toolbar =(Toolbar) findViewById(R.id.mtoolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTitle = mDrawerTitle = getTitle();
		mDrawerList = (ListView) findViewById(R.id.list_view);
        fab = (FloatingActionButton) findViewById(R.id.fabId);
        lastBar = (ProgressBar) findViewById(R.id.pBarMainOne);
//        mDeclaration
        listView = (ListView) findViewById(R.id.listEmploisId);
        emploiLastAdapter = new EmploisLatestAdapter(MainActivity.this, R.layout.emploi_row, empArrayLast);
        listView.setAdapter(emploiLastAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //      go to details activity
                Emploi emploiLast = emploiLastAdapter.getItem(position);
                Intent desIntent = new Intent(MainActivity.this, EmploiDetailActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putSerializable("emploiObj", emploiLast);
                desIntent.putExtra("mainCount", mapCount);
                desIntent.putExtras(mBundle);
                startActivityForResult(desIntent, REQUEST_MAIN_MAP);
            }
        });

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.setAdapter(new DrawerShopAdapter(this, getPackageName(), getDrawerShopDummyList()));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerList
				.setBackgroundResource(R.drawable.gradient_drawer_background_shop);
		mDrawerList.getLayoutParams().width = (int) getResources()
				.getDimension(R.dimen.drawer_width_shop);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
//				getSupportActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
//				getSupportActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.addDrawerListener(mDrawerToggle);

//		hide fab fo 1st launch
        fab.setVisibility(View.INVISIBLE);
//		show fab
        adIsBannish =false;
//		mProcessing
//        get All Emplois
        if (JsonUtils.isNetworkAvailable(this)) {
            asynLast = new latestTask();
            asynLast.execute(Constant.EMPLOIS_LATEST_URL);
        } else {

            connectionAlert = new DialogBuilder(this, ACTIVITY_FINISH, "Connexion Internet","Merci de vous connecter à internet " +
                    "avant d'utiliser cette Appli");
            connectionAlert.showDialog();
        }
        //Check Update
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            //Load Map setting before launch asynchrone : WAIT FOR IDEA
//            if(savedInstanceState == null)
//            {
//                //Load map Setting
//                Prefs mSetting = new Prefs(MainActivity.this);
//                mapCount = mSetting.getMapCount();
//                Log.v("Main Map loadSetting", "mapCount: " + mapCount);
//            }
            asynUpdate = new updateTask();
            asynUpdate.execute(Constant.APP_UPDATE_URL);
            mHandler = new Handler();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //            get All Emplois
                    asynComp = new completeTask();
                    asynComp.execute(Constant.EMPLOIS_COMPLETE_URL);
                }
            }, Constant.REQUEST_EMPLOI_DELAY);

        }

        if (savedInstanceState == null) {
            Prefs memory = new Prefs(MainActivity.this);
            Boolean isFirstRun = memory.getFirstLaunch();
//			mDrawerLayout.openDrawer(mDrawerList);
            mDrawerLayout.closeDrawer(mDrawerList); // dont open
            if(!isFirstRun)
            {
                mDrawerLayout.openDrawer(mDrawerList);
                memory.setFirstLaunch(true);
                adWillBug = true;
            }
            isAdvertiserReqAllow = true;
            monetizerCode = memory.getAdvertiserId();
            //Share with other activity
            App.setMonetizerId(monetizerCode);
        }
        initBanner();
        iniRewardedVideo();

    }


    private	class latestTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            lastBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
            lastBar.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            lastBar.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                //Can't contact server
                requestAlert = new DialogBuilder(MainActivity.this, ACTIVITY_NOT_FINISH, "Erreur de connection au serveur",
                        "le serveur est peut être encours de maintenance ou Réseau faible. "+ getString(R.string.connection_tips));
                requestAlert.showDialog();

            } else {

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        Emploi emploiItem = new Emploi();

                        if(isCancelled())
                            break;
                        emploiItem.setEmploiId(objJson.getString(Constant.EMPLOI_ITEM_ID).trim());
                        emploiItem.setTitle(objJson.getString(Constant.EMPLOI_ITEM_TITLE).trim());
                        emploiItem.setAddDate(objJson.getString(Constant.EMPLOI_ITEM_ADDDATE));
                        emploiItem.setEmail(objJson.getString(Constant.EMPLOI_ITEM_EMAIL).trim());
                        emploiItem.setWebSite(objJson.getString(Constant.EMPLOI_ITEM_WEBSITE).trim());
                        emploiItem.setMobile1(objJson.getString(Constant.EMPLOI_ITEM_MOBILE1).trim());
                        emploiItem.setMobile2(objJson.getString(Constant.EMPLOI_ITEM_MOBILE2).trim());
                        emploiItem.setSalary(objJson.getString(Constant.EMPLOI_ITEM_SALARY).trim());
                        emploiItem.setCity(objJson.getString(Constant.EMPLOI_ITEM_CITY).trim());
                        emploiItem.setEndDate(objJson.getString(Constant.EMPLOI_ITEM_ENDDATE));
                        emploiItem.setLatitude(objJson.getDouble(Constant.EMPLOI_ITEM_LATITUDE));
                        emploiItem.setLongitude(objJson.getDouble(Constant.EMPLOI_ITEM_LONGITUDE));

                        empArrayLast.add(emploiItem);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
//            Build ListView
            emploiLastAdapter.notifyDataSetChanged();
        }

    }


    private	class completeTask extends AsyncTask<String, Void, String> {
        Boolean networkErr = false;

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
                Toast.makeText(MainActivity.this,"Erreur de connection au serveur", Toast.LENGTH_LONG).show();
                networkErr = true;

            } else {

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        Emploi emploi = new Emploi();

                        if(isCancelled())
                            break;

                        emploi.setEmploiId(objJson.getString(Constant.EMPLOI_ITEM_ID).trim());
                        emploi.setTitle(objJson.getString(Constant.EMPLOI_ITEM_TITLE).trim());
                        emploi.setAddDate(objJson.getString(Constant.EMPLOI_ITEM_ADDDATE));
                        emploi.setEmail(objJson.getString(Constant.EMPLOI_ITEM_EMAIL).trim());
                        emploi.setWebSite(objJson.getString(Constant.EMPLOI_ITEM_WEBSITE).trim());
                        emploi.setMobile1(objJson.getString(Constant.EMPLOI_ITEM_MOBILE1).trim());
                        emploi.setMobile2(objJson.getString(Constant.EMPLOI_ITEM_MOBILE2).trim());
                        emploi.setSalary(objJson.getString(Constant.EMPLOI_ITEM_SALARY).trim());
                        emploi.setCity(objJson.getString(Constant.EMPLOI_ITEM_CITY).trim());
                        emploi.setEndDate(objJson.getString(Constant.EMPLOI_ITEM_ENDDATE));
                        emploi.setDescription(objJson.getString(Constant.EMPLOI_ITEM_DESCRI).trim());
                        emploi.setStudyLevel(objJson.getString(Constant.EMPLOI_ITEM_STUDYLEVEL).trim());
                        emploi.setSexe(objJson.getString(Constant.EMPLOI_ITEM_SEXE).trim());
                        emploi.setSociety(objJson.getString(Constant.EMPLOI_ITEM_SOCIETY).trim());
                        emploi.setWorkMode(objJson.getString(Constant.EMPLOI_ITEM_WORKMODE).trim());
                        emploi.setExperience(objJson.getString(Constant.EMPLOI_ITEM_EXP).trim());
                        emploi.setActivitySector(objJson.getString(Constant.EMPLOI_ITEM_SECTOR).trim());
                        emploi.setContratType(objJson.getString(Constant.EMPLOI_ITEM_CONTRAT).trim());
                        emploi.setSocietyPicUrl(objJson.getString(Constant.EMPLOI_ITEM_SOCIETY_IMAGE.trim()));
                        emploi.setLatitude(objJson.getDouble(Constant.EMPLOI_ITEM_LATITUDE));
                        emploi.setLongitude(objJson.getDouble(Constant.EMPLOI_ITEM_LONGITUDE));

                        empArrayComp.add(emploi);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
//            Build ListView
            if(!networkErr)
            {
                updateListView();
            }
        }

    }

    private void updateListView() {
//        show listeView
        emploiLastAdapter = null;
        compBar = (ProgressBar) findViewById(R.id.pBarMainTwo);
        compBar.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
        compBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        // change Adapter
        emploiCompAdapter = new EmploisCompleteAdapter(MainActivity.this, R.layout.emploi_row, empArrayComp);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //      go to details activity
                Emploi emploiComp = emploiCompAdapter.getItem(position);
                Intent desIntent = new Intent(MainActivity.this, EmploiDetailActivity.class);
                Bundle mBundle = new Bundle();

                mBundle.putSerializable("emploiObj", emploiComp);
                desIntent.putExtra("loadingok","1");
                desIntent.putExtra("mainCount", mapCount);
                desIntent.putExtras(mBundle);
                startActivityForResult(desIntent, REQUEST_MAIN_MAP);
            }
        });
        listView.setAdapter(emploiCompAdapter);
        emploiCompAdapter.notifyDataSetChanged();
//        hide Listview
        compBar.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
//        update search array
        if(!searchInput.equals(""))
        {
            searchedArray = new ArrayList<Emploi>();
            for (Emploi dum : empArrayComp) {
                if (dum.getTitle().toLowerCase()
                        .contains(searchInput.toLowerCase())) {
                    searchedArray.add(dum);
                }
            }
            listView.setAdapter(new EmploisCompleteAdapter(MainActivity.this, R.layout.emploi_row, searchedArray));
        }
//        free space
        empArrayLast = null;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_MAIN_MAP)
        {
            if(resultCode == RESULT_OK)
            {
                //refresh Map globa data
                mapCount = data.getIntExtra("mapGlobal", 0);
                isMaxUse = mapCount < 1;
            }
        }
    }

    private	class updateTask extends AsyncTask<String, Void, String> {


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
                Toast.makeText(getApplicationContext(),"Erreur de connection au serveur", Toast.LENGTH_LONG).show();

            } else {

                try {
                    // get App info from server
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    int versionCode;
                    final String updateInfo;
                    final String appId = getPackageName();//your application package name i.e play store application url
                    objJson =jsonArray.getJSONObject(0);

                    versionCode = objJson.getInt("versionCode");
                    updateInfo = objJson.getString("response");
                    if (!isCancelled())
                    {
                        if(versionCode != 0 && versionCode > getVersionCode(getApplicationContext()))// New update available
                        {
                            // an version exist on server
                            updateAlert = new DialogBuilder.Update(MainActivity.this, "Mise à jour", "une nouvelle version est disponible:"+ "\n" + updateInfo);
                            updateAlert.showDialog();
                        }
                        //Load map setting
                        if(mapCount == 0 && !isMaxUse)
                        {
                            onFirstTime();
                        }
                        else
                        {
                            onCurrentDate();
                        }
                    }
                    if(isAdvertiserReqAllow){
                        asynMonetize = new monetizationTask();
                        asynMonetize.execute(Constant.ADVERTISER_URL);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private	class monetizationTask extends AsyncTask<String, Void, String> {


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
//                Toast.makeText(getApplicationContext(),"Erreur de connection au serveur", Toast.LENGTH_LONG).show();

            } else {

                try {
                    // get Monetizer info from server
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    objJson =jsonArray.getJSONObject(0);
                    App app = new App();

                    int advertiserId;
                    //sav in shared pref
                    // sav in singleton for other act
                    advertiserId = objJson.getInt("advetiser");
                    if(advertiserId != app.getMonetizerId()){
                        Prefs mPref = new Prefs(MainActivity.this);
                        mPref.setAdvertiserId(advertiserId);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }


    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            DialogBuilder.Exit builder = new DialogBuilder.Exit(MainActivity.this, getString(R.string.app_name), "Etes-vous sûr de vouloir quitter?");
            builder.showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static int getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException ex) {}
        return 0;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setQueryHint("Rechercher");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextChange(String newText) {
                String searchText = newText.trim();
                searchedArray = new ArrayList<Emploi>();
                searchInput = newText.trim();
                if (emploiLastAdapter != null)
                {
                    for (Emploi dm : empArrayLast) {
                        if (dm.getTitle().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            searchedArray.add(dm);
                        }
                    }
                    if (searchText.isEmpty()) {
//                        show all emploi
                        listView.setAdapter(emploiLastAdapter);
                    } else {
//                        display what user want
                        listView.setAdapter(new EmploisLatestAdapter(MainActivity.this, R.layout.emploi_row, searchedArray));

                    }

                }else
                {
                    for (Emploi dm : empArrayComp) {
                        if (dm.getTitle().toLowerCase()
                                .contains(searchText.toLowerCase())) {
                            searchedArray.add(dm);
                        }
                    }
                    if (searchText.isEmpty()) {
//                        show all emploi
                        listView.setAdapter(emploiCompAdapter);
                    } else {
//                        display what user want
                        listView.setAdapter(new EmploisCompleteAdapter(MainActivity.this, R.layout.emploi_row, searchedArray));

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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
            switch (position)
            {
                case 0:
                    Intent leMotisIntent = new Intent(MainActivity.this, LeMotivationActivity.class);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(leMotisIntent);
                    break;
                case 1:
                    mDrawerLayout.closeDrawer(mDrawerList);
                    //go to play store code
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id="+ getString(R.string.app_id))
                        ));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id="+ getString(R.string.app_id)
                                )));
                    }
                    break;
                case 2:
                    Intent intab=new Intent(MainActivity.this,AboutActivity.class);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(intab);
                    break;
                case 3:
                    //go to facebook web page
                    String url = getString(R.string.facebook_videos).trim();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(i);
                    break;
                case 4:
                    AlertDialog.Builder info = new AlertDialog.Builder(MainActivity.this);
                    info.setIcon(R.mipmap.ic_launcher);
                    mDrawerLayout.closeDrawer(mDrawerList);
                    info.setTitle("Déposer une Annonce");
                    info.setMessage("merci de nous écrire à l'adresse : " + getString(R.string.app_email));
                    info.setPositiveButton("OK", null);
                    info.show();
                    break;
                case 5:
                    //go to facebook web page
                    String groupeUrl = getString(R.string.facebook_groupe).trim();
                    Intent groupeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + groupeUrl));
                    mDrawerLayout.closeDrawer(mDrawerList);
                    startActivity(groupeIntent);
                    break;


                default:
                    mDrawerLayout.closeDrawer(mDrawerList);
            }

		}
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
//		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

//	Add custom leftMenu
    public static ArrayList<MenuModel> getDrawerShopDummyList() {
        ArrayList<MenuModel> list = new ArrayList<>();
        list.add(new MenuModel(0, "", "L.Motivation", "main_ic_lemotis"));
        list.add(new MenuModel(1, "", "Noter", "main_ic_rate"));
        list.add(new MenuModel(2, "", "A Propos", "main_ic_about"));
        list.add(new MenuModel(3, "", "Aide", "main_ic_help"));
        list.add(new MenuModel(4, "", "Déposer", "main_ic_add"));
        list.add(new MenuModel(5, "", "Page", "main_ic_group"));
        return list;
    }

//    Below intruction for reward video
    @Override
    public void onResume() {
        super.onResume();
        if(monetizerCode < 2){

        if(adIsBannish)
        {
            // Fab is not like
            fab.setVisibility(View.INVISIBLE);
        }
        else
        {
            if(mRewardedVideoAd.isLoaded())
            {
                fab.setVisibility(View.VISIBLE);
            }
            else
            {
                fab.setVisibility(View.INVISIBLE);
                mRewardedVideoAd.loadAd(getString(R.string.admob_rewarded_video_id), new AdRequest.Builder().build());
            }
        }
        mRewardedVideoAd.resume(this);
        }
        if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE){
            IronSource.onResume(this);
        }
    }

    @Override
    public void onPause() {
	    if(monetizerCode < 2){
            mRewardedVideoAd.pause(this);
        }
        if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE){
            IronSource.onPause(this);
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        //Exit from Application
        if(!(null == asynLast)){
            asynLast.cancel(true);
        }
        if(!(null == asynComp))
        {
            asynComp.cancel(true);
        }
        if(!(null == asynUpdate))
        {
            asynUpdate.cancel(true);
        }
        if(!(null == mapInfo))
        {
            mapInfo.cancel(true);
        }
        if(!(null == mapAccount))
        {
            mapAccount.cancel(true);
        }
        if(!(null == asynMonetize))
        {
            asynMonetize.cancel(true);
        }
        if(monetizerCode < 2){
            mRewardedVideoAd.destroy(this);
        }
        super.onDestroy();
    }

    public void showRewardedVideo(View view) {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
        else
        {
            fab.setVisibility(View.INVISIBLE);
        }
    }

    private void onFirstTime()
    {
        //if asyn task is not null
        mapInfo = new infoTask();
        mapInfo.execute(Constant.MAP_INFO_URL);
    }

    private void onCurrentDate() {
        Prefs memory = new Prefs(MainActivity.this);
        mapCount = memory.getMapCount();
        if(mapCount < 1)
        {
            isMaxUse = true;
        }
    }

    private	class infoTask extends AsyncTask<String, Void, String> {


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
                // Hide map button
                isMaxUse = true;
            } else {

                try {
                    // get Maps Info from Server
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objson = null;
                    Boolean isMapAllow = false;
                    Boolean isMapLock = true;
                    Long mDate = 0L;

                    objson = jsonArray.getJSONObject(0);
                    isMapAllow = objson.getInt(Constant.MAPS_ACCES) == 1;
                    isMapLock = objson.getInt(Constant.MAPS_LOCK) == 1;
                    mDate = objson.getLong("nDate");

//                    Log.v("InfoTask start", "mapAllow: " + isMapAllow + " isMapLock: " + isMapLock
//                            + " Today: " +mDate + "Count Left: " + countLeft + " isMaxUse: "+ isMaxUse);

                    if(!isCancelled())
                    {
                        Prefs mPrefs = new Prefs(MainActivity.this);
                        int count = mPrefs.getMapCount();
                        if(mDate != 0L)
                        {
                            if(!isMapLock && isMapAllow)
                            {
                                if(mDate > mPrefs.getToday())
                                {
                                    //it a new day
                                    mPrefs.setToday(mDate); // sav new Date
                                    mapAccount = new accountTask();
                                    mapAccount.execute(MAP_ACCOUNT_URL); // get Bundle
                                }
                                else
                                {
                                    // not new day ..
                                    if(count > 0 )
                                    {
                                        mapCount = count; // load preview date
                                        isMaxUse = false;
                                    }
                                    else
                                    {
                                        isMaxUse = true;
                                        mapCount = 0;
                                    }
                                }

                            }
                            else
                            {
                                // API Lock GPS or it a limit
                                mapCount = 0;
                                isMaxUse = true;
                            }

                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private	class accountTask extends AsyncTask<String, Void, String> {


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
                // Hide map Button
                isMaxUse = true;
            } else {

                try {
                    // get Maps Info from Server
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objson = null;
                    Boolean isMapAllow = false;
                    Boolean isMapLock = true;
                    Boolean locationHome = false, routeAvailable = false, refreshAvailable = false;
                    int mApiCount = 0;

                    objson = jsonArray.getJSONObject(0);
                    isMapAllow = objson.getInt(Constant.MAPS_ACCES) == 1;
                    isMapLock = objson.getInt(Constant.MAPS_LOCK) == 1;
                    locationHome = objson.getInt(Constant.MAPS_LOCATION_HOME) == 1;
                    routeAvailable = objson.getInt(Constant.MAPS_LOCATION_DEST) == 1;
                    refreshAvailable = objson.getInt(Constant.MAPS_LOCATION_REFRESH) == 1;

                    if(!isCancelled())
                    {
                        Prefs mPrefs = new Prefs(MainActivity.this);

                        if(!isMapLock && isMapAllow)
                        {
                            isMaxUse = false; // Non Next time : dispaly map button
                            if(locationHome)
                            {
                                mApiCount = mApiCount + 2;
                            }
                            if(routeAvailable)
                            {
                                mApiCount = mApiCount + 2;
                            }
                            if(refreshAvailable)
                            {
                                mApiCount = mApiCount + 180;
                            }
                        }
                        else
                        {
                            // API Lock GPS or it a limit
                            isMaxUse = true;
                            mApiCount = 0;
                        }
//                        mPrefs.setMapCount(mApiCount); wait for new soluce
                        mapCount = mApiCount;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private void initBanner() {
        if(monetizerCode == Constant.AD_NETWORK_STARTAPP)
        {
            initStartApp();
        }
        else
        {
            initAdmob();
        }
    }

    private void iniRewardedVideo()
    {
        if (monetizerCode == Constant.AD_NETWORK_STARTAPP)
        {
            initStAppRewardVideo();
        }
        else if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE)
        {
            initISRewardVideo();
        }
        else
        {
            initAdmobRewardVideo();
        }

    }

    private void initAdmobRewardVideo() {
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewarded(RewardItem rewardItem) {
//                Toast.makeText(getBaseContext(), "Ad triggered reward.", Toast.LENGTH_SHORT).show();
//                When had watch video
                startActivity(new Intent(getApplicationContext(), CvActivity.class));
                adIsBannish = true;
            }

            @Override
            public void onRewardedVideoAdLoaded() {
//                Toast.makeText(getBaseContext(), "Ad loaded.", Toast.LENGTH_SHORT).show();
                if(!adWillBug)
                {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {
//                Toast.makeText(getBaseContext(), "Ad opened.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoStarted() {
//                Toast.makeText(getBaseContext(), "Ad started.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdClosed() {
//                Toast.makeText(getBaseContext(), "Ad closed.", Toast.LENGTH_SHORT).show();
//                user had already see Reward video
                adIsBannish = true;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
//                Toast.makeText(getBaseContext(), "Ad left application.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
//                Toast.makeText(getBaseContext(), "Ad failed to load.", Toast.LENGTH_SHORT).show();
            }
        });
        // Load a reward based video ad
        mRewardedVideoAd.loadAd(getString(R.string.admob_rewarded_video_id), new AdRequest.Builder().build());

    }

    private void initISRewardVideo() {
        IronSource.init(this, getResources().getString(R.string.irsource_app_id));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IronSource.showRewardedVideo(getResources().getString(R.string.irsource_rew_video_placement));
            }
        });
        fab.setVisibility(View.INVISIBLE);
        IronSource.setRewardedVideoListener(new RewardedVideoListener() {
            /**
             * Invoked when the RewardedVideo ad view has opened.
             * Your Activity will lose focus. Please avoid performing heavy
             * tasks till the video ad will be closed.
             */
            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewardedVideoAdClicked(Placement placement) {
            }

            /*Invoked when the RewardedVideo ad view is about to be closed.
                        Your activity will now regain its focus.*/
            @Override
            public void onRewardedVideoAdClosed() {
                isRwVideoWatched = true;
            }
            /**
             * Invoked when there is a change in the ad availability status.
             *
             * @param - available - value will change to true when rewarded videos are *available.
             *          You can then show the video by calling showRewardedVideo().
             *          Value will change to false when no videos are available.
             */
            @Override
            public void onRewardedVideoAvailabilityChanged(boolean available) {
                //Change the in-app 'Traffic Driver' state according to availability.
                if(!isRwVideoWatched && available)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            /**
             * Invoked when the video ad starts playing.
             */
            @Override
            public void onRewardedVideoAdStarted() {
            }
            /*Invoked when the video ad finishes playing.*/
            @Override
            public void onRewardedVideoAdEnded() {
            }
            /**
             * Invoked when the user completed the video and should be rewarded.
             * If using server-to-server callbacks you may ignore this events and wait *for the callback from the ironSource server.
             *
             * @param - placement - the Placement the user completed a video from.
             */
            @Override
            public void onRewardedVideoAdRewarded(Placement placement) {
                /** here you can reward the user according to the given amount.
                 String rewardName = placement.getRewardName();
                 int rewardAmount = placement.getRewardAmount();
                 */
                //go to CV activity
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(getApplicationContext(), CvActivity.class));
                    }
                });
            }
            /* Invoked when RewardedVideo call to show a rewarded video has failed
             * IronSourceError contains the reason for the failure.
             */
            @Override
            public void onRewardedVideoAdShowFailed(IronSourceError error) {
            }
        });

    }
    private void initStAppRewardVideo() {
        final StartAppAd rewardedVideo = new StartAppAd(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rewardedVideo.showAd();
            }
        });
//        hide fab
        fab.setVisibility(View.INVISIBLE);

        rewardedVideo.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
//                go to CV activity
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
                startActivity(new Intent(getApplicationContext(), CvActivity.class));
            }
        });
        rewardedVideo.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });

    }

    private void initAdmob() {
        //show admob Banner
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);
    }

    private void initStartApp() {
        StartAppSDK.init(this, getResources().getString(R.string.startapp_app_id), false);
        StartAppAd.disableSplash();

        //Hide Admob Banner
        AdView adView = (AdView) findViewById(R.id.adView);
        adView.setVisibility(View.GONE);

        /** Add banner programmatically (within Java code, instead of within the layout xml) **/
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainReLayoutId);

        // Create new StartApp banner
        Banner startAppBanner = new Banner(this);
        RelativeLayout.LayoutParams bannerParameters =
                new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        bannerParameters.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bannerParameters.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        // Add the banner to the main layout
        mainLayout.addView(startAppBanner, bannerParameters);
    }

}
