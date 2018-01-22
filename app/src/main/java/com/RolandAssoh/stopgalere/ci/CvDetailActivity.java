package com.RolandAssoh.stopgalere.ci;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.util.Constant;
import com.util.DialogBuilder;
import com.util.JsonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.CV;

import static com.util.Constant.ACTIVITY_FINISH;

public class CvDetailActivity extends AppCompatActivity {

    private WebView cvWebView;
    private TextView cvTitle;
    private Toolbar toolbar;
    private CV mCv;
    private DialogBuilder connectionAlert;
    ProgressBar pbarDetsCv;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cv_detail);
        toolbar = (Toolbar) findViewById(R.id.cvDetsToolbar);
        setSupportActionBar(toolbar);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(Constant.COLOR_PRIMARY_DARK));
        }
        getSupportActionBar().setTitle("Curriculum Vitae");

        cvTitle = (TextView) findViewById(R.id.cvDetsTitleId);
        pbarDetsCv = (ProgressBar) findViewById(R.id.cvDetsPrBar);
        scrollView = (ScrollView) findViewById(R.id.cvDetsSrcViewId);

        //        get Serializable from Intent
        mCv = (CV) getIntent().getSerializableExtra("cvObj");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String preview = bundle.getString("cvDlok");
            if (preview != null && preview.equals("1")) {
                cvWebView = (WebView) findViewById(R.id.cvDetsWebView);
//                All Emplois loading ok
                cvWebView.loadUrl(mCv.getContent());
                cvWebView.getSettings().setJavaScriptEnabled(true);
                cvWebView.setWebViewClient(new WebViewClient());
            }
            else
            {
                //        get  CV Details
                new getDescriptionTask().execute(Constant.CV_DESCRIPTION_URL + mCv.getId());
            }
        }

        // display Detail
        cvTitle.setText(mCv.getTitle());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cv_details, menu);
        MenuItem downloadItem = menu.findItem(R.id.menu_download);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        if(menuItem.getItemId() == R.id.menu_download)
        {
            if (isDownloadManagerAvailable());
            {
                String url = mCv.getDownloadUrl();
                String fileExtention = ".doc";
                String fileName = mCv.getTitle();
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setDescription("Curriculum vitae");
                request.setTitle(fileName);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
// in order for this if to run, you must use the android 3.2 to compile your app
                request.allowScanningByMediaScanner();
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + fileExtention);

// get download service and enqueue file
                DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                manager.enqueue(request);
                //see what happen in log
//                Log.v("download start", url);
            }

        }
        return super.onOptionsItemSelected(menuItem);
    }

    private class getDescriptionTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbarDetsCv.getIndeterminateDrawable().setColorFilter(Color.parseColor(Constant.REFRESH_PROGRESS_BAR_COLOR), android.graphics.PorterDuff.Mode.SRC_ATOP);
            pbarDetsCv.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            pbarDetsCv.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            if (null == result || result.length() == 0) {
                connectionAlert = new DialogBuilder(CvDetailActivity.this, ACTIVITY_FINISH,
                        "Erreur de connection au serveur", getString(R.string.connection_tips));
                connectionAlert.showDialog();

            }
            else
            {
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONObject objson = null;
                    cvWebView = (WebView) findViewById(R.id.cvDetsWebView);

                    objson = jsonArray.getJSONObject(0);

                    cvWebView.loadUrl(objson.getString(Constant.CV_ITEM_CONTENT));
                    mCv.setDownloadUrl(objson.getString(Constant.CV_ITEM_DOWNLOAD_URL));
                    cvWebView.getSettings().setJavaScriptEnabled(true);
                    cvWebView.setWebViewClient(new WebViewClient());

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

    private void clearIntent() {
        //        clearn intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String content = bundle.getString("cvDlok");
            if (content != null) {
                getIntent().removeExtra("cvDlok");
            }
        }

    }

    @Override
    public void onBackPressed() {
        clearIntent();
        CvDetailActivity.this.finish();
        super.onBackPressed();
    }

    public static boolean isDownloadManagerAvailable() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return true;
        }
        return false;
    }

}
