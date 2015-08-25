package com.hitherejoe.mvvm_hackernews;

import android.content.Context;
import android.view.View;

import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.util.DefaultConfig;
import com.hitherejoe.mvvm_hackernews.util.MockModelsUtil;
import com.hitherejoe.mvvm_hackernews.viewModel.PostViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class PostViewModelTest {

    private Context mContext;
    private PostViewModel mPostViewModel;
    private Post mPost;

    @Before
    public void setUp() {
        mContext = RuntimeEnvironment.application;
        mPost = MockModelsUtil.createMockStory();
        mPostViewModel = new PostViewModel(mContext, mPost, false);
    }

    @Test
    public void shouldGetPostScore() throws Exception {
        String postScore = mPost.score + mContext.getResources().getString(R.string.story_points);
        assertEquals(mPostViewModel.getPostScore(), postScore);
    }

    @Test
    public void shouldGetPostTitle() throws Exception {
        assertEquals(mPostViewModel.getPostTitle(), mPost.title);
    }

    @Test
    public void shouldGetPostAuthor() throws Exception {
        String author = mContext.getString(R.string.text_post_author, mPost.by);
        assertEquals(mPostViewModel.getPostAuthor().toString(), author);
    }

    @Test
    public void shouldGetCommentsVisibility() throws Exception {
        // Our mock post is of the type story, so this should return gone
        mPost.kids = null;
        assertEquals(mPostViewModel.getCommentsVisibility(), View.GONE);
        mPost.kids = new ArrayList<>();
        assertEquals(mPostViewModel.getCommentsVisibility(), View.VISIBLE);
        mPost.kids = null;
        mPost.postType = Post.PostType.ASK;
        assertEquals(mPostViewModel.getCommentsVisibility(), View.VISIBLE);
    }
}
