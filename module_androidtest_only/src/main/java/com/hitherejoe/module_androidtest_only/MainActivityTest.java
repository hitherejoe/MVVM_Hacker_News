package com.hitherejoe.module_androidtest_only;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.hitherejoe.module_androidtest_only.injection.TestComponentRule;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.util.MockModelsUtil;
import com.hitherejoe.mvvm_hackernews.view.activity.MainActivity;

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

import com.hitherejoe.mvvm_hackernews.R;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public final ActivityTestRule<MainActivity> main =
            new ActivityTestRule<>(MainActivity.class, false, false);

    @Rule
    public final TestComponentRule component = new TestComponentRule();

    @Test
    public void testPostsShowAndAreScrollableInFeed() {
        List<Long> postIdList = MockModelsUtil.createMockPostIdList(20);
        List<Post> postList = new ArrayList<>();
        for (Long id : postIdList) {
            postList.add(MockModelsUtil.createMockStoryWithId(id));
        }

        stubMockPosts(postIdList, postList);
        main.launchActivity(null);

        checkPostsDisplayOnRecyclerView(postList);
    }

    @Test
    public void testStoryPostHasViewButton() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        Post mockPost = MockModelsUtil.createMockStoryWithTitle("Post with url");
        postIdList.add(mockPost.id);
        when(component.getMockHackerNewsService().getTopStories()).thenReturn(Observable.just(postIdList));
        when(component.getMockHackerNewsService().getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));

        Intent i = new Intent(MainActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        onView(withId(R.id.text_view_post)).check(matches(isDisplayed()));
    }

    @Test
    public void testJobPostHasViewButton() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        Post mockPost = MockModelsUtil.createMockJobWithTitle("Post with url");
        postIdList.add(mockPost.id);
        when(component.getMockHackerNewsService().getTopStories()).thenReturn(Observable.just(postIdList));
        when(component.getMockHackerNewsService().getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));

        Intent i = new Intent(MainActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        onView(withId(R.id.text_view_post)).check(matches(isDisplayed()));
    }

    @Test
    public void testViewPost() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        Post mockPost = MockModelsUtil.createMockStoryWithTitle("Post with url");
        postIdList.add(mockPost.id);
        when(component.getMockHackerNewsService().getTopStories()).thenReturn(Observable.just(postIdList));
        when(component.getMockHackerNewsService().getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));

        Intent i = new Intent(MainActivity.getStartIntent(InstrumentationRegistry.getTargetContext()));
        main.launchActivity(i);
        onView(withText(mockPost.title)).check(matches(isDisplayed()));
        onView(withText(mockPost.title)).perform(click());
        onView(withText(mockPost.title)).check(matches(isDisplayed()));
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
