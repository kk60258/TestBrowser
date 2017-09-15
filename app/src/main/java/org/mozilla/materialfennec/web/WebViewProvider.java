package org.mozilla.materialfennec.web;

import android.content.Context;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by nineg on 2017/9/15.
 */

public class WebViewProvider {

    public static WebView createWebView(Context context) {
        WebView webView = new MyWebView(context);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        return webView;
    }

    static class MyWebViewClient extends WebViewClient {

    }

    static class MyWebChromeClient extends WebChromeClient {

    }
}
