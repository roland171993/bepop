package com.RolandAssoh.stopgalere.ci;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.squareup.picasso.Picasso;
import com.startapp.android.publish.adsCommon.Ad;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;
import com.util.Constant;
import com.util.DialogBuilder;
import com.util.JsonUtils;
import com.util.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Emploi;

import static com.util.Constant.ACTIVITY_FINISH;
import static com.util.Constant.ACTIVITY_NOT_FINISH;
import static com.util.Constant.MAP_ACCOUNT_URL;

public class EmploiDetailActivity extends AppCompatActivity {

    private Emploi emploi;
    private DialogBuilder connectionAlert;
    private DialogBuilder.Apply apply;
    private DialogBuilder.EmploisShare dShare;
    private TextView title, addDate, email, website, mobile1, mobile2, salary, city, endDate,
            nameContact, nameEmail, nameWebSite;
    private ImageView societyPic;
    private static int monetizerCode = 1;
    //Admob variable
    private InterstitialAd mInterstitialAd;
    ProgressBar pbarDets;
    ScrollView mScrollView;
    public static int adCounter = 0;
    protected static boolean isMaxUse = false;
    protected static int empDetsAdCounter = 0;
    protected static int countLeft = 0;
    static final int REQUEST_DETS_MAP = 2;
    private boolean isBadDestroy = true;
    private Handler adHandler;
    //StartApp
    private StartAppAd startAppAd = new StartAppAd(this);
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emploi_detail);
        toolbar = (Toolbar) findViewById(R.id.emplDetsToolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }
        getSupportActionBar().setTitle("");

        // attach Ui to Java code
        title = (TextView) findViewById(R.id.detsTitleTextId);
        addDate = (TextView) findViewById(R.id.detsADateTextId);
        email = (TextView) findViewById(R.id.detsEmailTextId);
        website = (TextView) findViewById(R.id.detsWSiteTextId);
        mobile1 = (TextView) findViewById(R.id.detsMob1TextId);
        mobile2 = (TextView) findViewById(R.id.detsMob2TextId);
        salary = (TextView) findViewById(R.id.detsSalaryTextId);
        city = (TextView) findViewById(R.id.detsCityTextId);
        endDate = (TextView) findViewById(R.id.detsEDateTextId);
        nameContact = (TextView) findViewById(R.id.detsContaTitleId);
        nameEmail = (TextView) findViewById(R.id.detsEmailTitleId);
        nameWebSite = (TextView) findViewById(R.id.detsWSTitleId);

        pbarDets = (ProgressBar) findViewById(R.id.detsProgressBar);
        mScrollView = (ScrollView) findViewById(R.id.detsScrollViewId);
        societyPic = (ImageView) findViewById(R.id.detsSocietyImgId);

        //        get Serializable from Intent
        emploi = (Emploi) getIntent().getSerializableExtra("emploiObj");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String preview = bundle.getString("loadingok");
            if (preview != null && preview.equals("1")) {
//                All Emplois loading ok
                TextView description = (TextView) findViewById(R.id.detsDescriTextId);
                TextView society = (TextView) findViewById(R.id.detsSocietyTextId);
                TextView studyLevel = (TextView) findViewById(R.id.detsSLevelTextId);
                TextView sexe = (TextView) findViewById(R.id.detsSexeTextId);
                TextView contratType = (TextView) findViewById(R.id.detsCTypeTextId);
                TextView workMode = (TextView) findViewById(R.id.detsWModeTextId);
                TextView experience = (TextView) findViewById(R.id.detsEXPTextId);
                TextView activitySector = (TextView) findViewById(R.id.detsASectTextId);

                description.setText(emploi.getDescription());
                society.setText(emploi.getSociety());
                studyLevel.setText(emploi.getStudyLevel());
                sexe.setText(emploi.getSexe());
                contratType.setText(emploi.getContratType());
                workMode.setText(emploi.getWorkMode());
                experience.setText(emploi.getExperience());
                activitySector.setText(emploi.getActivitySector());
                displayImage();
            }
            else
            {
                //        get  News Details from SERVER
                new getDescriptionTask().execute(Constant.EMPLOI_DESCRIPTION_URL + emploi.getEmploiId());
            }
        }
//        Load Maps Setting
        if(savedInstanceState == null)
        {
            countLeft = getIntent().getIntExtra("mainCount", 0);
            isMaxUse = countLeft < 1;
            //Setup Advertise
            monetizerCode = App.getMonetizerId();
        }

//        show content
        title.setText(emploi.getTitle());
        addDate.setText(emploi.getAddDate());
        email.setText(emploi.getEmail());
        website.setText(emploi.getWebSite());
        mobile1.setText(emploi.getMobile1());
        mobile2.setText(emploi.getMobile2());
        salary.setText(emploi.getSalary());
        city.setText(emploi.getCity());
        endDate.setText(emploi.getEndDate());
//          hide empty field
        if (emploi.getSalary().isEmpty() || emploi.getSalary().equals("0") || emploi.getSalary().trim().toLowerCase().equals("fcfa")) {
            salary.setVisibility(View.GONE);
        }
        if(emploi.getEmail().trim().isEmpty() )
        {
            nameEmail.setVisibility(View.GONE);
        }
        if(emploi.getWebSite().trim().isEmpty())
        {
            nameWebSite.setVisibility(View.GONE);
        }
        if(emploi.getMobile1().trim().isEmpty() && emploi.getMobile2().trim().isEmpty())
        {
            nameContact.setVisibility(View.GONE);
        }

        //load Advertise
        iniInterstitial();
    }

    private void iniInterstitial() {
        if (monetizerCode == Constant.AD_NETWORK_STARTAPP)
        {
            if(empDetsAdCounter <  3)
                initStAppInterstitial();
        }
        else if(monetizerCode == Constant.AD_NETWORK_IRONSOURCE)
        {
            if(empDetsAdCounter < 3)
                initISInterstitial();
        }
        else
        {
            initAdmobInterstitial();
        }
    }

    private void initAdmobInterstitial() {
        //        //Load interstitial Admob
        mInterstitialAd = newInterstitialAd();
        loadInterstitial(); // get New add from API with you Admob_Interstitail_ID
    }

    private void displayImage() {
        //        load society picture
        if(!emploi.getSocietyPicUrl().trim().equals(""))
        {
            societyPic.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext()).load(emploi.getSocietyPicUrl().replace(" ", "%20").trim()).into(societyPic);

        }
    }

    private void clearIntent() {
        //        clearn intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String content = bundle.getString("loadingok");
            if (content != null) {
                getIntent().removeExtra("loadingok");
            }
        }

    }


    @Override
    public void onBackPressed() {
        if(monetizerCode < 2){
            showInterstitial();
        }
        //        Kill activity
        returnCount();
        clearIntent();
        EmploiDetailActivity.this.finish();
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        if(monetizerCode < 2){
            //Load interstitial Admob
        mInterstitialAd = newInterstitialAd();
        loadInterstitial(); // get New add from API with you Admob_Interstitail_ID
        }
        super.onStop();
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
    protected void onRestart() {
        if(monetizerCode < 2){
            //Show Interstitial
            showInterstitial();
        }
        super.onRestart();
    }



    @Override
    protected void onDestroy() {
        // detroy asynchr Task
        clearIntent();
        if(isBadDestroy)
        {
            Prefs mPrefs = new Prefs(EmploiDetailActivity.this);
            mPrefs.setOnDestroyCount(countLeft);
        }
        //Save Map Data
        super.onDestroy();
    }

    private void returnCount() {
        // return map Global value
        Intent returnIntent = getIntent();
        returnIntent.putExtra("mapGlobal", countLeft);
        setResult(RESULT_OK, returnIntent);
        isBadDestroy = false;
    }

    //launch call
    private void launchCall(String phoneNumber) {
        phoneNumber = "tel:" + phoneNumber;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        startActivity(callIntent);
    }

    private class getDescriptionTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbarDets.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
            pbarDets.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pbarDets.setVisibility(View.INVISIBLE);
            mScrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                connectionAlert = new DialogBuilder(EmploiDetailActivity.this, ACTIVITY_FINISH, "Erreur de connection au serveur"
                        ,getString(R.string.connection_tips));
                connectionAlert.showDialog();

            }
            else
            {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objson = null;
                    TextView description = (TextView) findViewById(R.id.detsDescriTextId);
                    TextView society = (TextView) findViewById(R.id.detsSocietyTextId);
                    TextView studyLevel = (TextView) findViewById(R.id.detsSLevelTextId);
                    TextView sexe = (TextView) findViewById(R.id.detsSexeTextId);
                    TextView contratType = (TextView) findViewById(R.id.detsCTypeTextId);
                    TextView workMode = (TextView) findViewById(R.id.detsWModeTextId);
                    TextView experience = (TextView) findViewById(R.id.detsEXPTextId);
                    TextView activitySector = (TextView) findViewById(R.id.detsASectTextId);

                    objson = jsonArray.getJSONObject(0);

                    description.setText(objson.getString(Constant.EMPLOI_ITEM_DESCRI));
                    studyLevel.setText(objson.getString(Constant.EMPLOI_ITEM_STUDYLEVEL));
                    sexe.setText(objson.getString(Constant.EMPLOI_ITEM_SEXE));
                    society.setText(objson.getString(Constant.EMPLOI_ITEM_SOCIETY));
                    workMode.setText(objson.getString(Constant.EMPLOI_ITEM_WORKMODE));
                    experience.setText(objson.getString(Constant.EMPLOI_ITEM_EXP));
                    activitySector.setText(objson.getString(Constant.EMPLOI_ITEM_SECTOR));
                    contratType.setText(objson.getString(Constant.EMPLOI_ITEM_CONTRAT));
                    emploi.setSocietyPicUrl(objson.getString(Constant.EMPLOI_ITEM_SOCIETY_IMAGE));
                    displayImage();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_emploi_item_details, menu);

        MenuItem callMobile1 = menu.findItem(R.id.call_mobile1);
        MenuItem callMobile2 = menu.findItem(R.id.call_mobile2);
        MenuItem sendMail = menu.findItem(R.id.send_mail);
        MenuItem showWebSite = menu.findItem(R.id.show_website);
        MenuItem empLocation = menu.findItem(R.id.emp_location);

        if(emploi.getMobile1().toString().trim().equals("") || emploi.getMobile1().toString().length() < 8)
        {
            callMobile1.setVisible(false);
        }
        if(emploi.getMobile2().toString().trim().equals("") || emploi.getMobile2().toString().length() < 8)
        {
            callMobile2.setVisible(false);
        }
        if(emploi.getEmail().toString().trim().equals(""))
        {
            sendMail.setVisible(false);
        }
        if(emploi.getWebSite().toString().trim().equals(""))
        {
            showWebSite.setVisible(false);
        }
        if(emploi.getLatitude() == 0 && emploi.getLongitude() == 0 || isMaxUse || countLeft < 3)
        {
            empLocation.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.call_mobile2:
                launchCall(emploi.getMobile2());
                return true;
            case R.id.call_mobile1:
                launchCall(emploi.getMobile1());
                return true;
            case R.id.show_website:
                String url = emploi.getWebSite().trim();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
                startActivity(i);
                return true;
            case R.id.send_mail:
                //Send email
                if(emploi.getEmail().contains("@"))
                {
                    //Confirm message
                    apply = new DialogBuilder.Apply(EmploiDetailActivity.this,true,
                            "Postuler", "Merci de joindre les documents démandés" +"\n" +"à l'email suivant", emploi.getEmail());
                    apply.showDialog();
                }
                else
                {
                    // Apply on Society Web Site
                    apply = new DialogBuilder.Apply(EmploiDetailActivity.this,false,
                            "Postuler", "Vous devez vous inscrire ou connecter au site web " + "\n" +"de la société avant de postuler",emploi.getEmail());
                    apply.showDialog();
                }

                return true;
            case R.id.emp_location:
                //Get Itinéraire
                if(countLeft > 2 && !isMaxUse)
                {
                    Intent mapIntent = new Intent(EmploiDetailActivity.this, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("empLoc", emploi);
                    mapIntent.putExtra("countValue", countLeft);
                    mapIntent.putExtras(bundle);
                    if (isGooglePlayServicesAvailable()) {
                        if(monetizerCode < 2)
                        {
                            showInterstitial();
                        }
                        startActivityForResult(mapIntent, REQUEST_DETS_MAP);
                    }
                }
                else
                {
                    returnCount();
                    EmploiDetailActivity.this.finish();
                }
                return true;
            case R.id.send_sms:
//                show ad after 3 share
                if (adCounter == 2) {
                    //Show Interstitial
                    if(monetizerCode < 2){
                        showInterstitial();
                        //Load interstitial Admob
                    mInterstitialAd = newInterstitialAd();
                    loadInterstitial(); // get New add from API with you Admob_Interstitail_ID
                    adCounter = 0;
                    }
                }
                adCounter ++;
                //get User name, receiver Number
                dShare = new DialogBuilder.EmploisShare(EmploiDetailActivity.this, emploi);
                dShare.showDialog();
                return true;



            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_DETS_MAP)
        {
            if(resultCode == RESULT_OK)
            {
                //update Map count left
                countLeft = data.getIntExtra("returnValue", 0);
                isMaxUse = countLeft < 1;
            }
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                DialogBuilder pServiceAlert = new DialogBuilder(EmploiDetailActivity.this,
                        ACTIVITY_NOT_FINISH, "EXECUTION IMPOSSIBLE", "Vous devez installer Google Play Service avant d'utiliser cette " +
                        "fonctionnalité");
                pServiceAlert.showDialog();
            }
            return false;
        }
        return true;
    }

    // Below we have Admob Function
    public void loadInterstitial()
    {
        AdRequest adRequest = new AdRequest.Builder().setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitial()
    {
        //Show the ad if it's ready. or reload
        if(mInterstitialAd != null && mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }else
        {
//            //put code here to execute if ad did not show, example :
//            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();


        }
    }

    private InterstitialAd newInterstitialAd()
    {
        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // put code to execute on ad closure, go to next level
            }
        });
        return interstitialAd;
    }

    //other adverse
    private void initISInterstitial(){
        IronSource.setInterstitialListener(new InterstitialListener() {
            /**
             Invoked when Interstitial Ad is ready to be shown after load function was called.
             */
            @Override
            public void onInterstitialAdReady() {
                empDetsAdCounter ++;
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
                ;
            }

            @Override
            public void adDisplayed(Ad ad) {
                empDetsAdCounter ++;
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
