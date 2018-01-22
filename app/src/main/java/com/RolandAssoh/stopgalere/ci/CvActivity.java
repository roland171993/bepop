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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.util.Constant;
import com.util.DialogBuilder;
import com.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import data.CvCompleteAdapter;
import data.CvFastAdapter;
import model.CV;

import static com.util.Constant.ACTIVITY_NOT_FINISH;

public class CvActivity extends AppCompatActivity {
    private DialogBuilder requestAlert;
    private CvFastAdapter cvFastAdpater;
    private CvCompleteAdapter cvCompAdapter;
    private ArrayList<CV> cvArrayFast = new ArrayList<>(), cvArrayComp = new ArrayList<>();
    private ListView listView;
    private Toolbar toolbar;
    public AsyncTask<String, Void, String> cvAsynFast, cvAsynComp;
    ProgressBar pBarFast, pBarComp;
    Handler cvHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv);
        toolbar = (Toolbar) findViewById(R.id.cvToolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }
        getSupportActionBar().setTitle("Curriculum Vitae");

        listView = (ListView) findViewById(R.id.listCvId);
        pBarFast = (ProgressBar) findViewById(R.id.cvPBarFast);
        cvFastAdpater = new CvFastAdapter(CvActivity.this, R.layout.cv_row, cvArrayFast);
        listView.setAdapter(cvFastAdpater);
//        get All CV
        if (JsonUtils.isNetworkAvailable(CvActivity.this)) {
            cvAsynFast = new latestTask();
            cvAsynFast.execute(Constant.CV_FAST_URL);
            cvHandler = new Handler();
            cvHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //            get All Emplois
                    cvAsynComp = new completeTask();
                    cvAsynComp.execute(Constant.CV_COMPLETE_URL);
                }
            }, Constant.REQUEST_CV_DELAY);
        } else {
            Toast.makeText(getApplicationContext(), "Pas de connection au Reseau!!!", Toast.LENGTH_LONG).show();
        }

    }

    private	class latestTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pBarFast.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
            pBarFast.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pBarFast.setVisibility(View.INVISIBLE);
            listView.setVisibility(View.VISIBLE);

            if (null == result || result.length() == 0) {
                //Can't contact server
                requestAlert = new DialogBuilder(CvActivity.this,ACTIVITY_NOT_FINISH, "Erreur de connection au serveur"
                        , "le serveur est peut être encours de maintenance ou Réseau faible. "
                        + getString(R.string.connection_tips));
                requestAlert.showDialog();

            } else {

                try {

                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        CV mCv = new CV();

                        if(isCancelled())
                            break;
                        mCv.setId(objJson.getString(Constant.CV_ITEM_ID).trim());
                        mCv.setTitle(objJson.getString(Constant.CV_ITEM_TITLE).trim());

                        cvArrayFast.add(mCv);
//                            Log.v("All News Fragements", objJson.getString(Constant.CATEGORY_NAME));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Build
                cvFastAdpater.notifyDataSetChanged();
            }
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
                //Can't contact server
                Toast.makeText(CvActivity.this,"Erreur de connection au serveur", Toast.LENGTH_LONG).show();

            } else {

                try {

                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objJson = null;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        CV cv = new CV();
                        if(isCancelled())
                            break;
                        cv.setId(objJson.getString(Constant.CV_ITEM_ID).trim());
                        cv.setTitle(objJson.getString(Constant.CV_ITEM_TITLE).trim());
                        cv.setContent(objJson.getString(Constant.CV_ITEM_CONTENT).trim());
                        cv.setDownloadUrl(objJson.getString(Constant.CV_ITEM_DOWNLOAD_URL).trim());

                        cvArrayComp.add(cv);
//                            Log.v("All News Fragements", objJson.getString(Constant.CATEGORY_NAME));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Build ListView
                if(!networkErr)
                {
                    updateListView();
                }
            }

        }
    }

    private void updateListView() {
        //        show listeView
        cvFastAdpater = null;
        pBarComp = (ProgressBar) findViewById(R.id.cvPBarComp);
        pBarComp.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
        pBarComp.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
        // change Adapter
        cvCompAdapter = new CvCompleteAdapter(CvActivity.this, R.layout.cv_row, cvArrayComp);
        listView.setAdapter(cvCompAdapter);
        cvCompAdapter.notifyDataSetChanged();
//        hide Listview
        pBarComp.setVisibility(View.INVISIBLE);
        listView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        killAllAsyn();
        super.onBackPressed();
    }

    private void killAllAsyn() {
        //kill Asyn task
        if(!(null == cvAsynFast))
        {
            cvAsynFast.cancel(true);
        }
        if(!(null == cvAsynComp))
        {
            cvAsynComp.cancel(true);
        }
        CvActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        //kill Asyn task
        if(!(null == cvAsynFast))
        {
            cvAsynFast.cancel(true);
        }
        if(!(null == cvAsynComp))
        {
            cvAsynComp.cancel(true);
        }
        CvActivity.this.finish();
        super.onDestroy();
    }
}
