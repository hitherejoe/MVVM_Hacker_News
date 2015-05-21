package com.hitherejoe.hackernews;

import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.ui.activity.MainActivity;
import com.hitherejoe.hackernews.util.MockModelsUtil;

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

public class MainActivityTest extends BaseTestCase<MainActivity> {
    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testPostsShown() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        List<Post> postList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post mockPost = MockModelsUtil.createMockStoryWithTitle(Integer.toString(i));
            postIdList.add(mockPost.id);
            postList.add(mockPost);
        }
        when(mHackerNewsService.getTopStories()).thenReturn(Observable.just(postIdList));
        for (Post mockPost : postList) {
            when(mHackerNewsService.getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));
        }

        getActivity();
        for (Post post : postList) {
            onView(withText(post.title)).check(matches(isDisplayed()));
        }
    }

    public void testAskPostNoViewButton() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        Post mockPost = MockModelsUtil.createMockAskStoryWithTitle("Ask HN: Mock");
        postIdList.add(mockPost.id);
        when(mHackerNewsService.getTopStories()).thenReturn(Observable.just(postIdList));
        when(mHackerNewsService.getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));

        getActivity();
        onView(withId(R.id.text_view_post)).check(matches(not(isDisplayed())));
    }

    public void testPostHasViewButton() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        Post mockPost = MockModelsUtil.createMockStoryWithTitle("Post with url");
        postIdList.add(mockPost.id);
        when(mHackerNewsService.getTopStories()).thenReturn(Observable.just(postIdList));
        when(mHackerNewsService.getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));

        getActivity();
        onView(withId(R.id.text_view_post)).check(matches(isDisplayed()));
    }

    public void testViewPost() throws Exception {
        List<Long> postIdList = new ArrayList<>();
        Post mockPost = MockModelsUtil.createMockStoryWithTitle("Post with url");
        postIdList.add(mockPost.id);
        when(mHackerNewsService.getTopStories()).thenReturn(Observable.just(postIdList));
        when(mHackerNewsService.getStoryItem(mockPost.id.toString())).thenReturn(Observable.just(mockPost));

        getActivity();
        onView(withText(mockPost.title)).check(matches(isDisplayed()));
        onView(withText(mockPost.title)).perform(click());
        onView(withText(mockPost.title)).check(matches(isDisplayed()));
    }
}
