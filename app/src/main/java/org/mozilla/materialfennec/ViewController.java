package org.mozilla.materialfennec;

import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by nineg on 2017/9/16.
 */

public class ViewController {
    private Stack<Class> mViewTagStack = new Stack<>();
    private Map<Class, ViewHolder> mViewTagMap = new HashMap<>();
    private ViewHolder mFocused;
    private ViewHolder mLast = null;

    public static class ViewHolder<T> {
        Class<T> tag;
        View[] views;

        public ViewHolder(Class<T> tag, @NonNull View... views) {
            this.tag = tag;
            this.views = views;
        }

        public void hide() {
            int length = views.length;
            for (int i = 0; i < length; ++i) {
                if (views[i] != null) {
                    views[i].setVisibility(View.GONE);
                }
            }
        }

        public void show() {
            int length = views.length;
            for (int i = 0; i < length; ++i) {
                if (views[i] != null) {
                    views[i].setVisibility(View.VISIBLE);
                }
            }
        }

        public boolean canGoback() {
            if (views.length > 0 && views[0] instanceof WebView) {
                return ((WebView) views[0]).canGoBack();
            }
            return false;
        }

        public void goback() {
            if (views.length > 0 && views[0] instanceof WebView) {
                ((WebView) views[0]).goBack();
                return;
            }
        }

    }

    public ViewController() {
    }

    private void buryView(@NonNull ViewHolder holder) {
        holder.hide();
        Class tag = holder.tag;
//        if (mViewTagStack.contains(tag))
//            return;

        mViewTagStack.add(tag);
        mViewTagMap.put(tag, holder);
    }

    public boolean hasViews() {
        return !mViewTagStack.isEmpty();
    }
    public ViewHolder digOutView() {
        if (!hasViews())
            return null;
        Class tag = mViewTagStack.pop();
        ViewHolder viewHolder = mViewTagMap.get(tag);
        mFocused.hide();
        setFocusView(viewHolder, false);
        return viewHolder;
    }

    public void setFocusView(@NonNull ViewHolder viewHolder) {
        setFocusView(viewHolder, true);
    }

    private void setFocusView(@NonNull ViewHolder viewHolder, boolean buryLast) {
        if (viewHolder == mFocused) {
            return;
        }
        mLast = mFocused;
        mFocused = viewHolder;
        mFocused.show();
        if (buryLast && mLast != null) {
            buryView(mLast);
        }
    }

    public boolean canGoback() {
        return mFocused == null ? false : mFocused.canGoback();
    }

    public void goback() {
        if (mFocused == null) {
            return;
        }
        mFocused.goback();
    }
}
