package com.example.asif.movies.WebView_Link;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.asif.movies.R;

public class BrowserWebview extends AppCompatActivity{
    String url , movieName ;
    boolean isDone = false ,isImdb = false;
    WebView browser;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wikipedia_webview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progress=new ProgressDialog(this);
        progress.setMessage("Loading ...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.show();


        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            url =(String) b.get("url");
            movieName = (String) b.get("movie name");
            String i = (String) b.get("imdb");
            if("imdb".equals(i)){
                isImdb = true;
                Log.d("IMDB", "    true");

            }
        }

        browser = (WebView) findViewById(R.id.wikiWebView);
        browser.setWebViewClient(new MyBrowser());
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.canGoBack();
        browser.canGoForward();
        browser.loadUrl(url);
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String urlNew) {
                BrowserWebview.this.setTitle(view.getTitle());
                if(!isImdb){
                    if(!isDone){
                        String title = browser.getTitle();
                        movieName.replace("_"," ");
                        movieName = movieName + " - Wikipedia";
                        if(!movieName.equals(title)){
                            url = url + "_(film)";
                            browser.loadUrl(url);
                        }
                        isDone = true;
                        Log.d("Error", title +"  to "+movieName);
                    }
                }
                progress.dismiss();
            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (browser.canGoBack()) {
            browser.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && browser.canGoBack()) {
            browser.goBack(); // Go to previous page
            return true;
        }
        // Use this as else part
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.open_browser, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.open:
                Intent i = new Intent (Intent.ACTION_VIEW);
                i.setData(Uri.parse(browser.getUrl()));
                startActivity(i);

            case android.R.id.home:
                if (browser.canGoBack()) {
                    browser.goBack();
                } else {
                    super.onBackPressed();
                }
                break;
        }
        return true;
    }
}
