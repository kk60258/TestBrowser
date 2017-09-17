package org.mozilla.materialfennec.search;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import org.mozilla.materialfennec.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by nineg on 2017/9/17.
 */

public class SuggestionAsyncTask extends AsyncTask<String, Void, List<String>> {
    private WeakReference<Callback> mCallback;
    private String mKeyword;

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

    public SuggestionAsyncTask(@NonNull Callback callback) {
        mCallback = new WeakReference<Callback>(callback);
    }

    @Override
    protected void onPostExecute(List<String> suggestions) {
        super.onPostExecute(suggestions);
        Callback callback = mCallback.get();
        if (callback != null) {
            callback.onNewSuggestions(this, suggestions, mKeyword);
        }
    }
}