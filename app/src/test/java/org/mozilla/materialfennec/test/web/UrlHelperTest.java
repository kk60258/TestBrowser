package org.mozilla.materialfennec.test.web;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.materialfennec.BuildConfig;
import org.mozilla.materialfennec.search.SearchHttpHelper;
import org.mozilla.materialfennec.web.UrlHelper;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 * Created by nineg on 2017/9/24.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UrlHelperTest {

    @Test
    public void urlHelper_getWebURL_notModifyValidInput() {
        Assert.assertEquals("https://www.gamer.com.tw", UrlHelper.getWebURL("https://www.gamer.com.tw"));
    }

    @Test
    public void urlHelper_getWebURL_beginWithHttp() {
        String expect = "http://abc";
        String real =  UrlHelper.getWebURL(expect);
        Assert.assertTrue(real.startsWith("http"));
    }

    @Test
    public void urlHelper_getWebURL_beginWithHttps() {
        String expect = "https://abc";
        String real =  UrlHelper.getWebURL(expect);
        Assert.assertTrue(real.startsWith("https://"));
    }

    @Test
    public void urlHelper_getWebURL_beginWithWww() {
        String expect = "https://www.abc";//SearchHttpHelper.getSearchUrl("www.abc");
        String real =  UrlHelper.getWebURL("www.abc");
        Assert.assertEquals(expect, real);
    }

    @Test
    public void urlHelper_getWebURL_search() {
        String expect = SearchHttpHelper.getSearchUrl("abc");
        String real =  UrlHelper.getWebURL("abc");
        Assert.assertEquals(expect, real);
    }
}
