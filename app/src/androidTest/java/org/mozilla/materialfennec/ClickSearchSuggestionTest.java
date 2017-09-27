package org.mozilla.materialfennec;


import android.content.res.Resources;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mozilla.materialfennec.search.SearchInputView;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.startsWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ClickSearchSuggestionTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void setup() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @After
    public void release() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
    /***
     * To check
     *
     * 1. Type an input text on search bar
     * 2. Wait a few seconds
     * 3. Check if all suggestions in listview are started with the input text
     * 4. Click the last item of suggestions
     * 5. Check if content of the search bar matches the last item.
     * */
    @Test
    public void clickSearchSuggestionTest() {
        String INPUT_STRING = "saw";
        ViewInteraction searchInputView = onView(
                allOf(withId(R.id.url), isDisplayed()));
        searchInputView.perform(typeText(INPUT_STRING), closeSoftKeyboard());

        int childSize = getListChildCount(R.id.search_suggestion_listview);
        for (int i = 0; i < childSize -1; ++i) {
            onData(anything())
                    .inAdapterView(withId(R.id.search_suggestion_listview))
                    .atPosition(i)
                    .check(matches(withText(startsWith(INPUT_STRING))));
        }

        final String[] contents = {""};
        int testIndex = childSize - 1;

        onData(anything())
                .inAdapterView(withId(R.id.search_suggestion_listview))
                .atPosition(testIndex).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }
            @Override
            public String getDescription() {
                return "get TextView content";
            }
            @Override
            public void perform(UiController uiController, View view) {
                TextView textView = (TextView) view;
                contents[0] = textView.getText().toString();
            }
        }, click());


        onView(Matchers.<View>instanceOf(SearchInputView.class)).check(matches(withText(contents[0])));
    }

//    private void checkAllItemsInListView(DataInteraction dataInteraction, ) {
//
//    }

    /**
     * Get the size of listview
     * **/
    private int getListChildCount(int id) {
        final int[] count = {0};
        onView(withId(id)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ListView.class);
            }
            @Override
            public String getDescription() {
                return "getting child count";
            }
            @Override
            public void perform(UiController uiController, View view) {
                ListView list = (ListView) view;
                count[0] = list.getChildCount();
            }
        });
        return count[0];
    }

    /**
     * Get the content of textivew which is at the given index of children in listview
     * **/
    private String getListChildDataAt(final int id, final int index) {
        final String[] data = {""};
        onView(withId(id)).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ListView.class);
            }
            @Override
            public String getDescription() {
                return "getListChildDataAt id: " + id + ", index: " + index;
            }
            @Override
            public void perform(UiController uiController, View view) {
                ListView list = (ListView) view;
                data[0] = ((TextView)list.getChildAt(index)).getText().toString();
            }
        });
        return data[0];
    }


    /**
     * enhanced withText to deal with that original withText can only handle to string to string comparison.
     * **/

    private static Matcher<View> withTextEnhancement(final String expectedText) {

        return new BoundedMatcher<View, TextView>(TextView.class) {
            String actualText;

            @Override
            public void describeTo(Description description) {
                description.appendText("expect: ");
                description.appendText(expectedText);
                description.appendText("; actual: ");
                description.appendText(actualText);
            }

            @Override
            public boolean matchesSafely(TextView textView) {
                if (actualText == null) {
                    try {
                        actualText = textView.getText() == null ? "123null123" : textView.getText().toString();

                    } catch (Resources.NotFoundException e) {

                    }
                }
                if (expectedText != null) {
                    return expectedText.equals(actualText);
                } else {
                    return false;
                }
            }
        };
    }
}
