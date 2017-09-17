package org.mozilla.materialfennec.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nineg on 2017/9/15.
 */

/**
 * A view to handle search input changes and pass to each observers, including focusChanged, textChanged, and textCommitted.
 * */
public class SearchInputView extends EditText implements TextWatcher {
    private List<WeakReference<OnFocusChangeListener>> mFocusChangeListeners;
    private List<WeakReference<onCommitListener>> mCommitListener;
    private List<WeakReference<onTextChangedListener>> mTextChangedListeners;
    private SearchSuggestionPresenter.Feedback mFeedback;
    private boolean mStopNotify = false;

    public SearchSuggestionPresenter.Feedback getSuggestionFeedBack() {
        if (mFeedback == null) {
            mFeedback = new SearchSuggestionPresenter.Feedback() {
                @Override
                public void onClickSuggestion(String s) {
                    SearchInputView.this.onClickSuggestion(s);
                }
            };
        }
        return mFeedback;
    }

    public SearchInputView(Context context) {
        super(context);
    }

    public SearchInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchInputView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setObservers(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        resetObservers(this);
    }

    private void resetObservers(EditText searchEditText) {
        searchEditText.setOnEditorActionListener(null);
        searchEditText.removeTextChangedListener(this);
        searchEditText.setOnFocusChangeListener(null);
    }

    private void setObservers(EditText searchEditText) {
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || isEnterKey(event)) {
                    String text = v.getText().toString().trim();
                    notifyCommit(text);
                    v.clearFocus();
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(this);

        searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                notifyFocusChanged(v, hasFocus);
            }
        });
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (isEnterKey(event)) {
//            String text = getText().toString().trim();
//            notifyCommit(text);
//            clearFocus();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }

    private boolean isEnterKey(KeyEvent keyEvent) {
        return keyEvent != null && keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER;
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String text = s == null ? "" : s.toString();
        notifyTextChanged(text);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //To handle focus changed
    public void addOnFocusChangeListener(OnFocusChangeListener focusChangeListener) {
        if (mFocusChangeListeners == null)
            mFocusChangeListeners = new ArrayList<>();
        mFocusChangeListeners.add(new WeakReference<OnFocusChangeListener>(focusChangeListener));
    }

    //To handle search commit
    public void addOnCommitListener(onCommitListener commitListener) {
        if (mCommitListener == null)
            mCommitListener = new ArrayList<>();
        mCommitListener.add(new WeakReference<onCommitListener>(commitListener));
    }

    //To handle text changed
    public void addOnTextChangedListener(onTextChangedListener textChangedListener) {
        if (mTextChangedListeners == null)
            mTextChangedListeners = new ArrayList<>();
        mTextChangedListeners.add(new WeakReference<onTextChangedListener>(textChangedListener));
    }

    private void notifyFocusChanged(View v, boolean hasFocus) {
        if (!hasFocus) {
            InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        if (mFocusChangeListeners == null || mStopNotify)
            return;

        for(WeakReference<OnFocusChangeListener> listernerWeakReference : mFocusChangeListeners) {
            OnFocusChangeListener listerner = listernerWeakReference.get();
            if (listerner != null) {
                listerner.onFocusChange(v, hasFocus);
            }
        }
    }

    private void notifyCommit(CharSequence cs) {
        if (mCommitListener == null || mStopNotify)
            return;

        for(WeakReference<onCommitListener> listernerWeakReference : mCommitListener) {
            onCommitListener listerner = listernerWeakReference.get();
            if (listerner != null) {
                listerner.onCommited(cs);
            }
        }
    }

    private void notifyTextChanged(String s) {
        if (mTextChangedListeners == null || mStopNotify)
            return;

        for(WeakReference<onTextChangedListener> listernerWeakReference : mTextChangedListeners) {
            onTextChangedListener listerner = listernerWeakReference.get();
            if (listerner != null) {
                listerner.onTextChanged(s);
            }
        }
    }

    private void onClickSuggestion(String s) {
        mStopNotify = true;
        setText(s);
        mStopNotify = false;
        notifyCommit(s);
        clearFocus();
    }

    public interface onCommitListener {
        void onCommited(CharSequence cs);
    }

    public interface onTextChangedListener {
        void onTextChanged(String s);
    }
}
