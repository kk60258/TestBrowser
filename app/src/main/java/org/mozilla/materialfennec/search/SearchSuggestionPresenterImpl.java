package org.mozilla.materialfennec.search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.mozilla.materialfennec.dependency.Dependency;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by nineg on 2017/9/17.
 */

public class SearchSuggestionPresenterImpl implements SearchSuggestionPresenter {
    private WeakReference<Callback> mCallback;
    private WeakReference<Feedback> mFeedback;
    private SuggestionAsyncTask mLastSyncTask;
    private SuggestionAsyncTask.Callback mSuggestionCallback;

    public SearchSuggestionPresenterImpl(Context context) {
        mSuggestionCallback = new SuggestionAsyncTask.Callback() {
            @Override
            public void onNewSuggestions(SuggestionAsyncTask task, List<String> suggestions, String keyword) {
                if (mLastSyncTask == task) {
                    mLastSyncTask = null;
                }
                notifyCallback(suggestions, keyword);
            }
        };
    }

    @Override
    public void onTextChanged(String s) {
        if (s.length() > 0) {
            notifyCallbackShow();

            if (mLastSyncTask != null) {
                mLastSyncTask.cancel(true);
            }

            mLastSyncTask = new SuggestionAsyncTask(mSuggestionCallback, Dependency.get(SuggestionIdlingResource.class));
            //serial executor
            mLastSyncTask.execute(s);
        } else {
            notifyCallbackHide();
        }
    }

    private void notifyCallbackShow() {
        Callback callback = mCallback != null ? mCallback.get() : null;
        if (callback != null) {
            callback.showView();
        }
    }

    private void notifyCallbackHide() {
        Callback callback = mCallback != null ? mCallback.get() : null;
        if (callback != null) {
            callback.hideView();
        }
    }

    private void notifyCallback(@NonNull List<String> suggestions, @NonNull String keyword) {
        Callback callback = mCallback != null ? mCallback.get() : null;
        if (callback != null) {
            callback.setSuggestions(suggestions, keyword);
        }
    }

    @Override
    public void setCallback(Callback callback) {
        mCallback = new WeakReference<>(callback);
    }

    private void notifyFeedback(@NonNull String suggestion) {
        Feedback feedback = mFeedback != null ? mFeedback.get() : null;
        if (feedback != null) {
            feedback.onClickSuggestion(suggestion);
        }
    }

    @Override
    public void setFeedback(Feedback feedback) {
        mFeedback = new WeakReference<>(feedback);
    }

    @Override
    public void clickSuggestion(String suggestion) {
        notifyFeedback(suggestion);
    }
}
