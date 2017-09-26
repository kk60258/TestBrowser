package org.mozilla.materialfennec.search;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.mozilla.materialfennec.R;
import org.mozilla.materialfennec.ViewController;
import org.mozilla.materialfennec.dependency.Dependency;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nineg on 2017/9/17.
 */

public class SearchSuggestionView extends FrameLayout implements SearchSuggestionPresenter.Callback {
    private Context mContext;
    private SearchSuggestionPresenter mPresenter;
    private SearchSuggestListAdapter mAdapter;
    private ListView mListView;
    private ViewController mViewController;
    private ViewController.ViewHolder mViewHolder;
    public SearchSuggestionView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SearchSuggestionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchSuggestionView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SearchSuggestionView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mPresenter = Dependency.get(SearchSuggestionPresenter.class);
        mAdapter = new SearchSuggestListAdapter(mPresenter);
        mListView = new ListView(context);
        mListView.setId(R.id.search_suggestion_listview);
        mListView.setAdapter(mAdapter);
        addView(mListView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mViewHolder = new ViewController.ViewHolder(SearchSuggestionView.class, this);
        mViewController = Dependency.get(ViewController.class);

    }

    @Override
    public void hideView() {
        mViewController.digOutView();
    }

    @Override
    public void showView() {
        mViewController.setFocusView(mViewHolder);
    }

    @Override
    public void setSuggestions(@NonNull List<String> suggestions, @NonNull String keyword) {
        mAdapter.setSuggestions(suggestions, keyword);
    }

    private static class SearchSuggestListAdapter extends BaseAdapter {
        private @NonNull List<String> mSuggestions = new ArrayList<>();
        private String mKeyword;
        private SearchSuggestionPresenter mPresenter;

        static class ViewHolder {
//            static int key = 0x11;
            TextView textView;
        }

        public SearchSuggestListAdapter(SearchSuggestionPresenter suggestionPresenter) {
            mPresenter = suggestionPresenter;
        }

        public void setSuggestions(@NonNull List<String> suggestions, @NonNull String keyword) {
            mSuggestions.clear();
            mSuggestions.addAll(suggestions);
            mKeyword = keyword;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mSuggestions.size();
        }

        @Override
        public String getItem(int position) {
            return mSuggestions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String s = getItem(position);

            if (convertView == null) {
                TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_textview, parent, false);
                ViewHolder holder = new ViewHolder();
                holder.textView = tv;
                convertView = tv;
                convertView.setTag(holder);
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            setSuggestion(holder.textView, s, mKeyword);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPresenter.clickSuggestion(s);
                }
            });
            return convertView;
        }

        public void setSuggestion(TextView tv, @NonNull String suggestion, @NonNull String filter) {
            Spannable filterStyled = getColoredText(filter, Color.DKGRAY);

            tv.setText(filterStyled);

            String suggestionText = suggestion.replace(filter, "");
            Spannable suggestionStyled = getColoredText(suggestionText, Color.LTGRAY);

            tv.append(suggestionStyled);
        }

        private Spannable getColoredText(@NonNull String text, @ColorInt int color) {
            Spannable textStyled = new SpannableString(text);
            textStyled.setSpan(
                    new ForegroundColorSpan(color),
                    0,
                    text.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            return textStyled;
        }
    }
}
