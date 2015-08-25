package com.hitherejoe.mvvm_hackernews;

import android.content.Context;

import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.util.DefaultConfig;
import com.hitherejoe.mvvm_hackernews.util.MockModelsUtil;
import com.hitherejoe.mvvm_hackernews.viewModel.CommentHeaderViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.prettytime.PrettyTime;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class CommentHeaderViewModelTest {

    private CommentHeaderViewModel commentHeaderViewModel;
    private Post mPost;

    @Before
    public void setUp() {
        mPost = MockModelsUtil.createMockStoryWithText();
        commentHeaderViewModel = new CommentHeaderViewModel(RuntimeEnvironment.application, mPost);
    }

    @Test
    public void shouldGetCommentText() throws Exception {
        assertEquals(commentHeaderViewModel.getCommentText(), mPost.text);
    }

    @Test
    public void shouldGetCommentAuthor() throws Exception {
        Context context =RuntimeEnvironment.application;
        String author =
                context.getResources().getString(R.string.text_comment_author, mPost.by);
        assertEquals(commentHeaderViewModel.getCommentAuthor(), author);
    }

    @Test
    public void shouldGetCommentDate() throws Exception {
        String date = new PrettyTime().format(new Date(mPost.time * 1000));
        assertEquals(commentHeaderViewModel.getCommentDate(), date);
    }

}
