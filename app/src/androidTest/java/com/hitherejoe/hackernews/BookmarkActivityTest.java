package com.hitherejoe.hackernews;

import com.hitherejoe.hackernews.ui.activity.BookmarksActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class BookmarkActivityTest extends BaseTestCase<BookmarksActivity> {

    public BookmarkActivityTest() {
        super(BookmarksActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void testActivityShown() throws Exception {
        getActivity();
        Thread.sleep(5000);
        onView(withText(R.string.no_bookmarks)).check(matches(isDisplayed()));
    }



}