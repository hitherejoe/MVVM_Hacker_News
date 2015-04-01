package com.hitherejoe.hackernews;


import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.activity.BookmarksActivity;
import com.hitherejoe.hackernews.util.MockModelsUtil;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class BookmarkActivityTest extends BaseTestCase<BookmarksActivity> {

    //TODO: Find / implement a way to test the recycler views correctly / better...

    private DataManager mDataManager;

    public BookmarkActivityTest() {
        super(BookmarksActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mDataManager = HackerNewsApplication.get().getDataManager();
    }

    public void testNoBookmarksShown() throws Exception {
        getActivity();
        onView(withText(R.string.no_bookmarks)).check(matches(isDisplayed()));
    }

    public void testBookmarksShown() throws Exception {
        List<Post> bookmarkList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post mockPost = MockModelsUtil.createMockStoryWithTitle(Integer.toString(i));
            mDataManager.addBookmark(mockPost).subscribe();
            bookmarkList.add(mockPost);
        }
        getActivity();
        for (Post post : bookmarkList) onView(withText(post.title)).check(matches(isDisplayed()));
        onView(withText(R.string.no_bookmarks)).check(matches(not(isDisplayed())));
    }

    public void testViewBookmark() throws Exception {
        Post mockBookmark = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(mockBookmark).subscribe();
        getActivity();
        onView(withText(R.string.no_bookmarks)).check(matches(not(isDisplayed())));
        onView(withText(mockBookmark.title)).check(matches(isDisplayed()));
        onView(withText(mockBookmark.title)).perform(click());
        onView(withText(mockBookmark.title)).check(matches(isDisplayed()));
    }

    public void testRemoveBookmark() throws Exception {
        Post mockBookmark = MockModelsUtil.createMockStory();
        mDataManager.addBookmark(mockBookmark).subscribe();
        getActivity();
        onView(withText(R.string.no_bookmarks)).check(matches(not(isDisplayed())));
        onView(withText(R.string.remove_button)).perform(click());
        onView(withText(R.string.no_bookmarks)).check(matches(isDisplayed()));
    }

}