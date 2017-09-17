package org.mozilla.materialfennec;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.mozilla.materialfennec.dependency.Dependency;
import org.mozilla.materialfennec.search.SearchInputView;
import org.mozilla.materialfennec.search.SearchSuggestionPresenter;
import org.mozilla.materialfennec.search.SearchSuggestionView;
import org.mozilla.materialfennec.web.WebViewProvider;

public class MainActivity extends AppCompatActivity {
    private SearchInputView urlView;
    private ImageView menuView;
    private ImageView switchView;
    private CardView cardView;
    private ViewPager pagerView;
    private ViewGroup mRootContainer;
    private LinearLayout containerView;
    private ViewGroup mDefaultLandingContainer;

    private ImageView clearView;
    private TabLayout tabsView;
    private ViewStub mSearchViewStub;
    private SearchSuggestionView mSearchSuggestionView;
    private int containerPadding;
    private ViewController mViewController;
    private ViewController.ViewHolder mDefaultLandingViewHolder;
    private SearchInputView.onCommitListener mOnCommitListener;
    private View.OnFocusChangeListener mOnFocusChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        containerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        mViewController = Dependency.get(ViewController.class);
        mRootContainer = (ViewGroup) findViewById(R.id.root_container);
        mDefaultLandingContainer = (ViewGroup) findViewById(R.id.default_landing_container);
        containerView = (LinearLayout) findViewById(R.id.container);
        cardView = (CardView) findViewById(R.id.card);
        urlView = (SearchInputView) findViewById(R.id.url);
        menuView = (ImageView) findViewById(R.id.menu);
        switchView = (ImageView) findViewById(R.id.switcher);
        pagerView = (ViewPager) findViewById(R.id.pager);
        clearView = (ImageView) findViewById(R.id.clear);
        tabsView = (TabLayout) findViewById(R.id.tabs);
        mSearchViewStub = (ViewStub) findViewById(R.id.search_suggestion_view_stub);
        mDefaultLandingViewHolder = new ViewController.ViewHolder(MainActivity.class, mDefaultLandingContainer);
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlView.clearFocus();
                urlView.setText("");
            }
        });

        mOnFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switchView.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
                menuView.setVisibility(hasFocus ? View.GONE : View.VISIBLE);
            }
        };

        urlView.addOnFocusChangeListener(mOnFocusChangeListener);

        LayoutTransition transition = containerView.getLayoutTransition();

        transition.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (!urlView.hasFocus()) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(urlView.getWindowToken(), 0);

                    clearView.setVisibility(View.GONE);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
                    params.setMargins(containerPadding, containerPadding, containerPadding, containerPadding);
                    cardView.setLayoutParams(params);
                }
            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (urlView.hasFocus()) {
                    clearView.setVisibility(View.VISIBLE);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cardView.getLayoutParams();
                    params.setMargins(0, 0, 0, 0);
                    cardView.setLayoutParams(params);
                }
            }
        });

        pagerView.setAdapter(new HomeAdapter(getSupportFragmentManager()));

        tabsView.setupWithViewPager(pagerView);

        transition.setDuration(100);
        mOnCommitListener = new SearchInputView.onCommitListener() {
            @Override
            public void onCommited(CharSequence s) {
                Context c = MainActivity.this;
//                Intent i = SimpleWebViewActivity.getIntent(c, s.toString());
//                c.startActivity(i);
                if (TextUtils.isEmpty(s))
                    return;

                WebView webView = (WebView) mRootContainer.findViewById(R.id.webview);
                if (webView == null) {
                    webView = WebViewProvider.createWebView(c);
                    webView.setId(R.id.webview);
                    mRootContainer.addView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
                webView.loadUrl(s.toString());
                ViewController.ViewHolder holder = new ViewController.ViewHolder(webView.getClass(), webView);
                mViewController.setFocusView(holder);
            }
        };
        urlView.addOnCommitListener(mOnCommitListener);

        SearchSuggestionPresenter searchSuggestionPresenter = Dependency.get(SearchSuggestionPresenter.class);

        mSearchSuggestionView = (SearchSuggestionView) mSearchViewStub.inflate();
        searchSuggestionPresenter.setCallback(mSearchSuggestionView);
        searchSuggestionPresenter.setFeedback(urlView.getSuggestionFeedBack());
        urlView.addOnTextChangedListener(searchSuggestionPresenter);

        mViewController.setFocusView(mDefaultLandingViewHolder);
    }

    @Override
    public void onBackPressed() {
        if (urlView.hasFocus()) {
            urlView.clearFocus();
        } else {
            if (mViewController.canGoback()) {
                mViewController.goback();
                return;
            } else if (mViewController.hasViews()) {
                mViewController.digOutView();
                return;
            }
            super.onBackPressed();
        }
    }

    public static class HomeAdapter extends FragmentPagerAdapter {
        private static HomePanel[] panels = new HomePanel[] {
            new TopSitesFragment(),
            DummyFragment.create("Bookmarks"),
            DummyFragment.create("History"),
            DummyFragment.create("Reading List"),
            DummyFragment.create("Recent Tabs"),
        };

        public HomeAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) panels[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return panels[position].getTitle();
        }

        @Override
        public int getCount() {
            return panels.length;
        }
    }
}
