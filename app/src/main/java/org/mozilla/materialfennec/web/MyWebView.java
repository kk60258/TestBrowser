package org.mozilla.materialfennec.web;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Patterns;
import android.webkit.WebView;

import org.mozilla.materialfennec.search.SearchHttpHelper;

/**
 * Created by nineg on 2017/9/16.
 */

public class MyWebView extends WebView {
    public MyWebView(Context context) {
        super(context);
    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void loadUrl(String url) {
        if (url.startsWith("http://") || url.startsWith("https://")) {

        } else if (Patterns.WEB_URL.matcher(url).matches()) {
            url = "https://" + url;
        } else {
            //search it
            url = SearchHttpHelper.getSearchUrl(url);
        }
        super.loadUrl(url);
    }
}
