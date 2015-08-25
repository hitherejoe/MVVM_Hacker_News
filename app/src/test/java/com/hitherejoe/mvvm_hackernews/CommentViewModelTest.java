package com.hitherejoe.mvvm_hackernews;

import android.content.Context;

import com.hitherejoe.mvvm_hackernews.model.Comment;
import com.hitherejoe.mvvm_hackernews.util.DefaultConfig;
import com.hitherejoe.mvvm_hackernews.util.MockModelsUtil;
import com.hitherejoe.mvvm_hackernews.viewModel.CommentViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ocpsoft.prettytime.PrettyTime;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = DefaultConfig.EMULATE_SDK, manifest = DefaultConfig.MANIFEST)
public class CommentViewModelTest {

    private CommentViewModel mCommentViewModel;
    private Comment mComment;

    @Before
    public void setUp() {
        mComment = MockModelsUtil.createMockComment();
        mCommentViewModel = new CommentViewModel(RuntimeEnvironment.application, mComment);
    }

    @Test
    public void shouldGetCommentText() throws Exception {
        assertEquals(mCommentViewModel.getCommentText(), mComment.text);
    }

    @Test
    public void shouldGetCommentAuthor() throws Exception {
        Context context =RuntimeEnvironment.application;
        String author =
                context.getResources().getString(R.string.text_comment_author, mComment.by);
        assertEquals(mCommentViewModel.getCommentAuthor(), author);
    }

    @Test
    public void shouldGetCommentDate() throws Exception {
        String date = new PrettyTime().format(new Date(mComment.time * 1000));
        assertEquals(mCommentViewModel.getCommentDate(), date);
    }

    @Test
    public void shouldGetCommentDepth() throws Exception {
        assertEquals(mCommentViewModel.getCommentDepth(), mComment.depth);
    }

    @Test
    public void shouldGetTopLevelComment() throws Exception {
        assertEquals(mCommentViewModel.getCommentIsTopLevel(), mComment.isTopLevelComment);
    }

}
