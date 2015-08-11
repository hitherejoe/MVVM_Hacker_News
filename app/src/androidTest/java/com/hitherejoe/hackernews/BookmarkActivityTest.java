package com.hitherejoe.hackernews;


import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.injection.TestComponentRule;
import com.hitherejoe.hackernews.ui.activity.BookmarksActivity;
import com.hitherejoe.hackernews.util.MockModelsUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class BookmarkActivityTest {

    @Rule
    public final ActivityTestRule<BookmarksActivity> main =
            new ActivityTestRule<>(BookmarksActivity.class, false, false);

    @Rule
    public final TestComponentRule component = new TestComponentRule();

    @Test
    public void testNoBookmarksShown() {
        component.getDatabaseHelper().clearBookmarks();
        Intent i = new Intent(BookmarksActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        onView(withText(R.string.no_bookmarks)).check(matches(isDisplayed()));
    }

    @Test
    public void testBookmarksShown() {
        component.getDatabaseHelper().clearBookmarks();
        List<Post> bookmarkList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post mockPost = MockModelsUtil.createMockStoryWithTitle(Integer.toString(i));
            component.getDataManager().addBookmark(InstrumentationRegistry.getContext(), mockPost).subscribe();
            bookmarkList.add(mockPost);
        }
        Intent i = new Intent(BookmarksActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        for (Post post : bookmarkList) onView(withText(post.title)).check(matches(isDisplayed()));
        onView(withText(R.string.no_bookmarks)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testViewBookmark() {
        component.getDatabaseHelper().clearBookmarks();
        Post mockBookmark = MockModelsUtil.createMockStory();
        component.getDataManager().addBookmark(InstrumentationRegistry.getContext(), mockBookmark).subscribe();
        Intent i = new Intent(BookmarksActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        onView(withText(R.string.no_bookmarks)).check(matches(not(isDisplayed())));
        onView(withText(mockBookmark.title)).check(matches(isDisplayed()));
        onView(withText(mockBookmark.title)).perform(click());
        onView(withText(mockBookmark.title)).check(matches(isDisplayed()));
    }

    @Test
    public void testRemoveBookmark() {
        component.getDatabaseHelper().clearBookmarks();
        Post mockBookmark = MockModelsUtil.createMockStory();
        component.getDataManager().addBookmark(InstrumentationRegistry.getContext(), mockBookmark).subscribe();
        Intent i = new Intent(BookmarksActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        onView(withText(R.string.no_bookmarks)).check(matches(not(isDisplayed())));
        onView(withText(R.string.remove_button)).perform(click());
        onView(withText(R.string.no_bookmarks)).check(matches(isDisplayed()));
    }

    private void checkPostsDisplayOnRecyclerView(List<Post> postsToCheck) {
        for (int i = 0; i < postsToCheck.size(); i++) {
            onView(withId(R.id.recycler_stories))
                    .perform(RecyclerViewActions.scrollToPosition(i));
            checkPostDisplays(postsToCheck.get(i));
        }
    }

    private void checkPostDisplays(Post post) {
        onView(withText(post.title))
                .check(matches(isDisplayed()));
    }

    private void stubMockPosts(List<Long> postIds, List<Post> mockPosts) {
        when(component.getMockHackerNewsService().getTopStories())
                .thenReturn(Observable.just(postIds));
        for (Long id : postIds) {
            for (Post post : mockPosts) {
                if (post.id.equals(id)) {
                    when(component.getMockHackerNewsService().getStoryItem(id.toString()))
                            .thenReturn(Observable.just(post));
                }
            }
        }
    }

}