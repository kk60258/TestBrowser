package org.mozilla.materialfennec.web;

import android.text.TextUtils;
import android.util.Patterns;

import org.mozilla.materialfennec.search.SearchHttpHelper;

import static org.mozilla.materialfennec.search.SearchHttpHelper.GOOGLE_SEARCH_HOST;

/**
 * Created by nineg on 2017/9/24.
 */

public class UrlHelper {
    public static String getWebURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return GOOGLE_SEARCH_HOST;
        }


        if (url.startsWith("http://") || url.startsWith("https://")) {
        } else if (Patterns.WEB_URL.matcher(url).matches()) {
            url = "https://" + url;
        } else {
            //search it
            url = SearchHttpHelper.getSearchUrl(url);
        }
        return url;
    }
}
