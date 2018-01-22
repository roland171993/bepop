package com.RolandAssoh.stopgalere.ci;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.map.GetDirectionsData;
import com.util.Constant;
import com.util.DialogBuilder;
import com.util.Prefs;

import model.Emploi;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener
        {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    ProgressBar pBarCount, pBarRefresh;
    private DialogBuilder.LocationSetting alertGPS;
    private DialogBuilder.Exit alertExit;
    private Switch goSwitch;
    int PROXIMITY_RADIUS = 10000;
    private double uLatitude, uLongitude; // set t private and test
    private double desLatitude = 7.6904, desLongitude = -5.0390; // set to private and test
    private Emploi emploi;
    private SeekBar zoomSBar;
    private int MIN = 1;
    private int MAX = 20;
    private int INIT = 11;
    private FloatingActionButton goFab;
    protected static final int REPEAT_INTERVAL = 10000; // 10 sec
    protected static final int UI_REFRECH_INTERVAL = 3000;// 2 seconds
    protected static final int REQUEST_UPDATE_INTERVAL = 1000;
    protected static final int REQUEST_FAST_INTERVAL = 1000; // 1sec
    protected static boolean isOff = false, mapFirstUse = true ;
    protected static int countValue = 0 ;
    private Handler mHandler, lockHandler, pbHandler;
    private Runnable runnable, lockRunnable, pbRunnable;
    SupportMapFragment mapFragment;
    private LinearLayout linearFirst, linearSecond;
    protected Boolean isFirst = true;
    private Boolean isBadDestroy = true;
    private View map;


            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }
        zoomSBar = (SeekBar) findViewById(R.id.seekBarId);
        goFab = (FloatingActionButton) findViewById(R.id.fabGoId);
        pBarCount = (ProgressBar) findViewById(R.id.timePbarId);
        pBarRefresh = (ProgressBar) findViewById(R.id.refreshPbarId);
        goSwitch = (Switch) findViewById(R.id.goSwitchId);
        linearFirst = (LinearLayout) findViewById(R.id.linearLayoutHeadId);
        linearSecond = (LinearLayout) findViewById(R.id.linearLayoutProgressId);
        map = (View) findViewById(R.id.map);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        //Check if Google Play Services Available or not
//        if (!CheckGooglePlayServices()) {
////            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
//            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
//            alertDialog.setTitle("EXECUTION IMPOSSIBLE");
//            alertDialog.setMessage("Vous devez installer Google Play Service avant d'utiliser cette " +
//                    "fonctionnalité ");
//            alertDialog.setNegativeButton("QUITTER", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    finish();
//                }
//            });
//        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mapFragment= (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        pBarRefresh.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
        zoomSBar.setProgress(INIT);
        zoomSBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean userFrom) {
                if (progress < MIN) {
                    //set to default
                    seekBar.setProgress(MIN);
                }
                else if(progress > MAX) {
                    seekBar.setProgress(MAX);
                }
                else {
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
                goSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(goSwitch.isChecked())
                        {
                            if(isRouteAvailable())
                            {
                                if(isGpsEnable())
                                {
                                    if(isUserInArea() && !isUserNotLocation())
                                    {
                                        isOff = false;
                                        progBarDisplay();
                                        // first display
                                        mMap.clear();
                                        uiDrawDest(); // Ui DrawUser is always in uiDrawDest
                                        //next Display
                                        onDrivingStart();
                                        buttonsHide();
                                        Toast.makeText(getApplicationContext(),"Mode guide activée", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        onUserOutArea();
                                    }

                                }
                                else
                                {
                                    goSwitch.setVisibility(View.INVISIBLE);
                                    btHideSpecific();
                                }
                            }
                            else
                            {
                                //do not have enough credit
                                onCountNotEnough();
                            }

                        }
                        else
                        {
                            onSwitchOff();
                            Toast.makeText(getApplicationContext(),"Mode guide désactivé",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                goSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            goSwitch.getThumbDrawable().setColorFilter(checked ? Color.parseColor(Constant.THEME_ACCENT_COLOR) : Color.GRAY, PorterDuff.Mode.MULTIPLY);
                            goSwitch.getTrackDrawable().setColorFilter(!checked ? Color.parseColor(Constant.THEME_ACCENT_COLOR) : Color.GRAY, PorterDuff.Mode.MULTIPLY);
                        }
                    }
                }); // goSwitch end
                // load emploi location
                emploi = (Emploi) getIntent().getSerializableExtra("empLoc");
                desLatitude = emploi.getLatitude();
                desLongitude = emploi.getLongitude();
                // Load map setting
                countValue = getIntent().getIntExtra("countValue", 0);
                Log.v("Map Load ma", "countValue" + countValue + "IntentValue" + getIntent().getIntExtra("countValue",0));

    }

            private boolean isLocationAvailable() {
                Log.v("Map isCountEnough", "countValue: " + countValue);
                return countValue > 0;
            }
            private boolean isRouteAvailable()
            {
                return countValue > 1 ;
            }

            private void onSwitchOff() {
                isOff = true;
                progBarHide();
                onDrivingStop();
                buttonsDisplay();
            }

            private void onDrivingStop() {
                if(!(null == mHandler))
                {
                    mHandler.removeCallbacksAndMessages(runnable);
                }
                if(!(null == lockHandler))
                {
                    lockHandler.removeCallbacksAndMessages(lockRunnable);
                }
                if(!(null == pbHandler))
                {
                    pbHandler.removeCallbacksAndMessages(pbRunnable);
                }
            }

            private void checkGpsStatut() {
                LocationManager locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    showGPSDisabledAlertToUser();
                    buttonsHide();
                }
                else
                {
                    buttonsDisplay();
                }
            }
            public boolean isGpsEnable()
            {
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            }

            private void progBarDisplay() {
                pBarCount.setVisibility(View.VISIBLE);
            }

            private void progBarHide() {
                pBarCount.setVisibility(View.INVISIBLE);
            }

            private void uiDrawDest() {
                Object dataToSend[] = new Object[2];
                String url;
//                desLatitude = 7.6904;
//                desLongitude = -5.0390;
                LatLng latLng = new LatLng(desLatitude, desLongitude);
                dataToSend = new Object[3];
                usePass(); // decrement countValue
                if(uLatitude != desLatitude && uLongitude != desLongitude)
                {
                    uiDrawUser();
                    url = getDirectionsUrl();
                    GetDirectionsData getDirectionsData = new GetDirectionsData();
                    dataToSend[0] = mMap;
                    dataToSend[1] = url;
                    dataToSend[2] = latLng;
                    getDirectionsData.execute(dataToSend);

                    MarkerOptions markerDest = new MarkerOptions();
                    markerDest.position(latLng);
                    markerDest.draggable(false);
                    markerDest.title(getString(R.string.position_destination));
                    markerDest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    // move camera to Destination
                    mMap.addMarker(markerDest);
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    showDistLeft();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Vous êtes arrivé", Toast.LENGTH_LONG).show();
                    goSwitch.setChecked(false);
                    uiDrawUser();
                    onSwitchOff();
                }
            }

            private void uiDrawUser()
            {
                LatLng latLng = new LatLng(uLatitude,uLongitude);
                MarkerOptions markerOptions = new MarkerOptions();
                usePass(); // decrement countValue
                markerOptions.position(latLng);
                markerOptions.draggable(false);
                markerOptions.title(getString(R.string.position_user));
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(INIT));
            }

            private void usePass()
            {
                countValue --;
                Log.v("Map UsePass", "countValue: " + countValue);
            }

            private void buttonsDisplay() {
//                btnMyLocation.setVisibility(View.VISIBLE);
                goFab.setVisibility(View.VISIBLE);
                if(!mMap.getUiSettings().isMyLocationButtonEnabled())
                {
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
                goSwitch.setVisibility(View.VISIBLE);
            }

            private void buttonsHide() {
//                btnMyLocation.setVisibility(View.INVISIBLE);
                goFab.setVisibility(View.INVISIBLE);
                if(mMap.getUiSettings().isMyLocationButtonEnabled())
                {
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            }

//            private boolean CheckGooglePlayServices() {
//        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        int result = googleAPI.isGooglePlayServicesAvailable(this);
//        if(result != ConnectionResult.SUCCESS) {
//            if(googleAPI.isUserResolvableError(result)) {
//                googleAPI.getErrorDialog(this, result,
//                        0).show();
//            }
//            return false;
//        }
//        return true;
//    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
        // Check if GPS is Enable
        checkGpsStatut();
        // Manage myLocationButton
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if(isLocationAvailable())
                {
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    uiDrawUser();
                    Toast.makeText(MapsActivity.this,"Votre position Actuelle", Toast.LENGTH_LONG).show();
                    return true;
                }
                else
                {
                    onCountNotEnough();
                    return true; //--> true: nothing will happen if it'is empty
                }
            }
        });

        mMap.setOnMarkerClickListener(this);
        // set up map
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(20.0f);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // change my Location button icon
        ImageView btnMyLocation = (ImageView) ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        btnMyLocation.setImageResource(R.drawable.ic_map_user);

        // Edit Buttons size
        btnMyLocation.getLayoutParams().height = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.fab_size_mini);
        btnMyLocation.getLayoutParams().width = getApplicationContext().getResources().getDimensionPixelSize(R.dimen.fab_size_mini);
        btnMyLocation.setScaleType(ImageView.ScaleType.FIT_XY);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) btnMyLocation.getLayoutParams();
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            rlp.addRule(RelativeLayout.ALIGN_PARENT_END, 0);
            rlp.addRule(RelativeLayout.ALIGN_END, 0);
        }
        else
        {
            // Inverse both buttons position
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) goFab.getLayoutParams();
            lp.gravity = (Gravity.LEFT | Gravity.BOTTOM) ;
            goFab.setLayoutParams(lp);
        }
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rlp.setMargins(5, 0, 0, getApplicationContext().getResources().getDimensionPixelSize(R.dimen.fab_bottom_navigation_margin));

        btnMyLocation.setLayoutParams(rlp);

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onClick(View v)
    {

        switch(v.getId()) {


            case R.id.fabGoId:
                if(isRouteAvailable())
                {
                    //Cancel action if GPS is Disable
                    if(isGpsEnable())
                    {
                        if(isUserInArea() && !isUserNotLocation())
                        {
                            mMap.clear();
                            uiDrawDest();
                        }
                        else
                        {
                            onUserOutArea();
                        }
                    }
                    else
                    {
                        btHideSpecific();
                    }
                }
                else
                {
                    //do not have enough credit
                    onCountNotEnough();
                }
                break;

        }
    }

            private void onCountNotEnough() {
                goSwitch.setVisibility(View.INVISIBLE);
                linearSecond.setVisibility(View.INVISIBLE);
                buttonsHide(); // fab + destination
                isOff = true;
            }

            private void btHideSpecific() {
                Toast.makeText(getApplicationContext(),"Votre GPS est désactivé", Toast.LENGTH_LONG).show();
                buttonsHide();
            }

            private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+uLatitude+","+uLongitude);
        googleDirectionsUrl.append("&destination="+desLatitude+","+desLongitude);
        googleDirectionsUrl.append("&key="+ getString(R.string.map_api_key));

        return googleDirectionsUrl.toString();
    }



    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(REQUEST_UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(REQUEST_FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
//        if (mCurrLocationMarker != null) {
//            mCurrLocationMarker.remove();
//        }

        uLatitude = location.getLatitude();
        uLongitude = location.getLongitude();

        if(isFirst)
        {
            if(isLocationAvailable())
            {
                uiDrawUser();
                Toast.makeText(MapsActivity.this,"Votre position Actuelle", Toast.LENGTH_LONG).show();
                isFirst = false;
            }
            else
            {
                onCountNotEnough();
            }
        }
        else
        {
            if(isLocationAvailable())
            {
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }
                uiDrawUser();
            }
            else
            {
                onCountNotEnough();
            }
        }


        //stop location updates
        locationStopUpdates();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Accès non autorisé", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }


            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.setDraggable(false);
                return false;
            }

            // my own function
            private void showGPSDisabledAlertToUser(){
                alertGPS = new DialogBuilder.LocationSetting(MapsActivity.this);
                alertGPS.showDialog();
            }

            @Override
            protected void onRestart() {
                super.onRestart();
                checkGpsStatut();
            }

            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
                Intent returnIntent = getIntent();
                returnIntent.putExtra("returnValue", countValue);
                setResult(RESULT_OK, returnIntent);
                isOff = true;
                isBadDestroy = false;
                MapsActivity.this.finish();
                return super.onKeyDown(keyCode, event);
            }

            private void onDrivingStart()
            {
                mHandler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(!isOff)
                        {
                            // uiDrawDest 2
                            if(countValue > 1)
                            {
                                mHandler.postDelayed(this,REPEAT_INTERVAL); // Repeat every 10 sec
                                uiRefresh();
                            }
                            else
                            {
                                onCountNotEnough();
                            }
                        }
                    }
                };
                mHandler.postDelayed(runnable, REPEAT_INTERVAL);

            }

            private void uiRefresh()
            {
                if(!isOff) // Dont refresh again swith button is off
                {
                    pBarCount.setProgress(pBarCount.getProgress() + 10);
                    if(pBarCount.getProgress() >= 60 )
                    {
                        uiLock();
                        lockHandler = new Handler();
                        lockRunnable = new Runnable() {
                            @Override
                            public void run() {
                                uiUnlock();
                                pBarCount.setProgress(0);
                            }
                        };
                        lockHandler.postDelayed(lockRunnable, UI_REFRECH_INTERVAL);
                    }
                    if (pBarCount.getProgress() >= 50 && pBarCount.getProgress() <60)
                    {
                        pbHandler = new Handler();
                        pbRunnable = new Runnable() {
                            @Override
                            public void run() {
                                pBarCount.setProgress(60);
                            }
                        };
                        pbHandler.postDelayed(pbRunnable, 7000);
                    }
                }

            }
            private void uiLock()
            {
                // Hide everything
                buttonsHide();
                linearFirst.setVisibility(View.INVISIBLE);
                linearSecond.setVisibility(View.INVISIBLE);
                map.setVisibility(View.INVISIBLE);
                pBarRefresh.setVisibility(View.VISIBLE);
                locationStartUpdates();

            }
            private void uiUnlock()
            {
                mMap.clear();
                //Dipslay Everything
                uiDrawDest(); //uiDrawDest is already in uiDrawDest
                linearFirst.setVisibility(View.VISIBLE);
                linearSecond.setVisibility(View.VISIBLE);
                map.setVisibility(View.VISIBLE);
                pBarRefresh.setVisibility(View.INVISIBLE);
                locationStopUpdates();
            }

            private void showDistLeft() {
                double disInMeter = calculationByDistance(uLatitude,uLongitude, desLatitude,desLongitude);
                int km = (int) (disInMeter / 1000);
                int meter = (int) disInMeter;


                if(disInMeter > 1000)
                {
                    Toast.makeText(getApplicationContext(),"Distance restant: "+ km +" KM" ,Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Distance restant: " + meter +" mètre(s)" ,Toast.LENGTH_LONG).show();
                }
            }

            protected double calculationByDistance(double startLat, double startLong, double endLat, double endLong) {

                float distanceInMeters;

                Location startLoc = new Location("");
                startLoc.setLatitude(startLat);
                startLoc.setLongitude(startLong);
                Location endLoc = new Location("");
                endLoc.setLatitude(endLat);
                endLoc.setLongitude(endLong);
                distanceInMeters = startLoc.distanceTo(endLoc);

                return distanceInMeters;
            }

            protected void locationStartUpdates() {
                // Create the location request
                mLocationRequest = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(REQUEST_UPDATE_INTERVAL)
                        .setFastestInterval(REQUEST_FAST_INTERVAL);
                // Request location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                        mLocationRequest, this);
//                Log.d("reque", "--->>>>");
            }

            public void locationStopUpdates()
            {
                if (mGoogleApiClient != null) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//                    Log.d("onLocationChanged", "Removing Location Updates");
                }
            }

            private boolean isUserInArea()
            {
                double latLimitMin = -34.90395297; // Below cap town
                double latLimitMax = 35.89572526; // Gibraltar
                double logLimitMin = -17.66601562; // Dakar
                double logLimitMax = 51.2677002; //Africa korn

                return uLatitude >= latLimitMin && uLatitude <= latLimitMax && uLongitude >= logLimitMin && uLongitude <= logLimitMax;
            }

            private void onUserOutArea()
            {
                DialogBuilder.LocationError locAlert = new DialogBuilder.LocationError(MapsActivity.this,"ITINERAIRE NON DISPONIBLE",
                        "Vous êtes en dehors de la zone de couverture");
                locAlert.showDialog();
                if(goSwitch.getVisibility() == View.VISIBLE)
                    goSwitch.setVisibility(View.INVISIBLE);

                mMap.clear();
                LatLng mLatLng = new LatLng(desLatitude,desLongitude);
                MarkerOptions societyMarker = new MarkerOptions();
                societyMarker.position(mLatLng);
                societyMarker.draggable(false);
                societyMarker.title(getString(R.string.position_destination));
                societyMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                mMap.addMarker(societyMarker);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(INIT));
            }

            private boolean isUserNotLocation()
            {
                return uLatitude == 0 || uLongitude == 0;
            }

            @Override
            protected void onDestroy() {
                Bundle mBundle =  getIntent().getExtras();
                if(mBundle != null)
                {
                    String content = mBundle.getString("empLoc");
                    if(content != null)
                    {
                        getIntent().removeExtra("empLoc");
                    }
                }
                if(isBadDestroy)
                {
                    Prefs mPrefs = new Prefs(MapsActivity.this);
                    mPrefs.setOnDestroyCount(countValue);
                    Log.v("Map BadClose", "countValue");
                }
                onDrivingStop();
                super.onDestroy();
            }
        }


