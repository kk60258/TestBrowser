package org.mozilla.materialfennec.search;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mozilla.materialfennec.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by nineg on 2017/9/17.
 */

public class SuggestionAsyncTask extends AsyncTask<String, Void, List<String>> {
    private WeakReference<Callback> mCallback;
    private String mKeyword;
    private SuggestionIdlingResource mIdlingResource;

    @Override
    protected List<String> doInBackground(String... params) {
        if (isCancelled()) {
            return null;
        }
        mKeyword = params[0];
        return SearchHttpHelper.getSuggestions(mKeyword);
    }

    public interface Callback {
        void onNewSuggestions(SuggestionAsyncTask task, List<String> suggestions, String keyword);
    }

    public SuggestionAsyncTask(@NonNull Callback callback, @Nullable SuggestionIdlingResource idlingResource) {
        mCallback = new WeakReference<Callback>(callback);
        mIdlingResource = idlingResource;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    @Override
    protected void onPostExecute(List<String> suggestions) {
        super.onPostExecute(suggestions);
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }

        Callback callback = mCallback.get();
        if (callback != null) {
            callback.onNewSuggestions(this, suggestions, mKeyword);
        }
    }
}