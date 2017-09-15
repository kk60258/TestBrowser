package org.mozilla.materialfennec.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nineg on 2017/9/15.
 */

public class SearchInputView extends EditText {
    private SearchTextWatcher mSearchTextWatcher;
    private List<WeakReference<onCommitListener>> mCommitListener;
    private List<WeakReference<onTextChangedListener>> mTextChangedListeners;

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
        if (mSearchTextWatcher == null) {
            mSearchTextWatcher = new SearchTextWatcher();
        }

        addTextChangedListener(mSearchTextWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mSearchTextWatcher != null) {
            removeTextChangedListener(mSearchTextWatcher);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() != KeyEvent.ACTION_DOWN) {
            return false;
        }

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            notifyCommit(getText());
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    class SearchTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            notifyTextChanged(s, start, before, count);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public void addOnCommitListener(onCommitListener commitListener) {
        if (mCommitListener == null)
            mCommitListener = new ArrayList<>();
        mCommitListener.add(new WeakReference<onCommitListener>(commitListener));
    }

    public void addOnTextChangedListener(onTextChangedListener textChangedListener) {
        if (mTextChangedListeners == null)
            mTextChangedListeners = new ArrayList<>();
        mTextChangedListeners.add(new WeakReference<onTextChangedListener>(textChangedListener));
    }

    private void notifyCommit(CharSequence cs) {
        if (mCommitListener == null)
            return;

        for(WeakReference<onCommitListener> listernerWeakReference : mCommitListener) {
            onCommitListener listerner = listernerWeakReference.get();
            if (listerner != null) {
                listerner.onCommited(cs);
            }
        }
    }

    private void notifyTextChanged(CharSequence s, int start, int before, int count) {
        if (mTextChangedListeners == null)
            return;

        for(WeakReference<onTextChangedListener> listernerWeakReference : mTextChangedListeners) {
            onTextChangedListener listerner = listernerWeakReference.get();
            if (listerner != null) {
                listerner.onTextChanged(s, start, before, count);
            }
        }
    }

    public interface onCommitListener {
        void onCommited(CharSequence cs);
    }

    public interface onTextChangedListener {
        void onTextChanged(CharSequence s, int start, int before, int count);
    }
}
