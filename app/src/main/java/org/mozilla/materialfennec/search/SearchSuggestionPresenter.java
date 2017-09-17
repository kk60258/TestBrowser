package org.mozilla.materialfennec.search;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by nineg on 2017/9/17.
 */

public interface SearchSuggestionPresenter extends SearchInputView.onTextChangedListener {
    interface Callback {
        void hideView();
        void showView();
        void setSuggestions(@NonNull List<String> suggestions, @NonNull String keyword);
    }

    void setCallback(Callback callback);

    interface Feedback {
        void onClickSuggestion(String s);
    }

    void setFeedback(Feedback feedback);

    void clickSuggestion(String suggestion);
}
