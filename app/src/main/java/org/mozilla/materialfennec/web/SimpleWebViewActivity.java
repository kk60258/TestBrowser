package org.mozilla.materialfennec.web;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import org.mozilla.materialfennec.R;

/**
 * Created by nineg on 2017/9/15.
 */

public class SimpleWebViewActivity extends AppCompatActivity {

    public static Intent getIntent(Context context, String url) {
        Intent i = new Intent(context, SimpleWebViewActivity.class);
        i.putExtra("url", url);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout container = new FrameLayout(this);
        setContentView(container);

        WebView webView = WebViewProvider.createWebView(this);
        container.addView(webView);

//        setContentView(R.layout.simple_web);
//        WebView webView = (WebView) findViewById(R.id.webview);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient());
//        webView.setWebChromeClient(new WebChromeClient());


        Intent intent = getIntent();
        String url = null;
        if (intent != null) {
            url = intent.getStringExtra("url");
        }
        webView.loadUrl(TextUtils.isEmpty(url) ? "https://www.google.com/" : url);

//        webView.loadUrl("https://www.google.com/");
    }
}
