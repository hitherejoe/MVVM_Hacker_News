package com.hitherejoe.mvvm_hackernews.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.hitherejoe.mvvm_hackernews.R;
import com.hitherejoe.mvvm_hackernews.databinding.ItemCommentsHeaderBinding;
import com.hitherejoe.mvvm_hackernews.model.Comment;
import com.hitherejoe.mvvm_hackernews.viewModel.CommentHeaderViewModel;
import com.hitherejoe.mvvm_hackernews.viewModel.CommentViewModel;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.databinding.ItemCommentBinding;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.BindingHolder> {

    private static final int VIEW_TYPE_COMMENT = 0;
    private static final int VIEW_TYPE_HEADER = 1;

    private Context mContext;
    private Post mPost;
    private List<Comment> mComments;

    public CommentAdapter(Context context, Post post, List<Comment> comments) {
        mContext = context;
        mPost = post;
        mComments = comments;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            ItemCommentsHeaderBinding commentsHeaderBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_comments_header,
                    parent,
                    false);
            return new BindingHolder(commentsHeaderBinding);
        } else {
            ItemCommentBinding commentBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_comment,
                    parent,
                    false);
            return new BindingHolder(commentBinding);
        }
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            ItemCommentsHeaderBinding commentsHeaderBinding = (ItemCommentsHeaderBinding) holder.binding;
            commentsHeaderBinding.setViewModel(new CommentHeaderViewModel(mContext, mPost));
        } else {
            int actualPosition = (postHasText()) ? position - 1 : position;
            ItemCommentBinding commentsBinding = (ItemCommentBinding) holder.binding;
            mComments.get(actualPosition).isTopLevelComment = actualPosition == 0;
            commentsBinding.setViewModel(new CommentViewModel(mContext, mComments.get(actualPosition)));
        }
    }


    @Override
    public int getItemCount() {
        return postHasText() ? mComments.size() + 1 : mComments.size();
    }

    @Override
    public int getItemViewType(int position) {
        // If the post has text, then it's an ASK post - so we show the text as a header comment
        if (position == 0 && postHasText()) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_COMMENT;
        }
    }

    private boolean postHasText() {
        return mPost.text != null && !mPost.text.equals("");
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingHolder(ItemCommentBinding binding) {
            super(binding.containerItem);
            this.binding = binding;
        }

        public BindingHolder(ItemCommentsHeaderBinding binding) {
            super(binding.containerItem);
            this.binding = binding;
        }
    }

}
