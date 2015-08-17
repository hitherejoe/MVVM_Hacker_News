package com.hitherejoe.hackernews.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.model.PostViewModel;
import com.hitherejoe.hackernews.databinding.ItemStoryBinding;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.BindingHolder> {
    private List<Post> mPosts;
    private Context mContext;
    private boolean mIsUserPosts;

    public PostAdapter(Context context, List<Post> posts, boolean isUserPosts) {
        mContext = context;
        mPosts = posts;
        mIsUserPosts = isUserPosts;
    }

    @Override
    public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BindingHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false));
    }

    @Override
    public void onBindViewHolder(BindingHolder holder, int position) {
        ItemStoryBinding storyBinding = DataBindingUtil.bind(holder.itemView);
        storyBinding.setViewModel(new PostViewModel(mContext, mPosts.get(position), mIsUserPosts));
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void setItems(List<Post> posts) {
        mPosts = posts;
        notifyDataSetChanged();
    }

    public void addItem(Post post) {
        mPosts.add(post);
        notifyDataSetChanged();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public BindingHolder(View rowView) {
            super(rowView);
            binding = DataBindingUtil.bind(rowView);
        }
        public ViewDataBinding getBinding() {
            return binding;
        }
    }

}