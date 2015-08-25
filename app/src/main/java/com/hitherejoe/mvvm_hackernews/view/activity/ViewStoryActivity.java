package com.hitherejoe.mvvm_hackernews.view.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hitherejoe.mvvm_hackernews.R;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.util.DataUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ViewStoryActivity extends BaseActivity {

    @Bind(R.id.progress_indicator)
    LinearLayout mProgressContainer;

    @Bind(R.id.layout_offline)
    LinearLayout mOfflineLayout;

    @Bind(R.id.layout_story)
    RelativeLayout mStoryLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.web_view)
    WebView mWebView;

    public static final String EXTRA_POST =
            "com.hitherejoe.mvvm_hackernews.ui.activity.WebPageActivity.EXTRA_POST";
    private static final String KEY_PDF = "pdf";
    private static final String URL_GOOGLE_DOCS = "http://docs.google.com/gview?embedded=true&url=";
    private static final String URL_PLAY_STORE =
            "https://play.google.com/store/apps/details?id=com.hitherejoe.hackernews&hl=en_GB";
    private Post mPost;

    public static Intent getStartIntent(Context context, Post post) {
        Intent intent = new Intent(context, ViewStoryActivity.class);
        intent.putExtra(EXTRA_POST, post);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        mPost = bundle.getParcelable(EXTRA_POST);
        if (mPost == null) throw new IllegalArgumentException("ViewStoryActivity requires a Post object!");
        setupToolbar();
        setupWebView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_story, menu);
        setupShareActionProvider(menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_browser:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPost.url)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        setupWebView();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mPost.title);
        }
    }

    private void setupShareActionProvider(Menu menu) {
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_item_share));
        if (shareActionProvider != null) shareActionProvider.setShareIntent(getShareIntent());
    }

    private void setupWebView() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) mProgressContainer.setVisibility(ProgressBar.GONE);
            }
        });
        mWebView.setWebViewClient(new ProgressWebViewClient());
        mWebView.setInitialScale(1);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        if (DataUtils.isNetworkAvailable(this)) {
            showHideOfflineLayout(false);
            if (mPost.postType == Post.PostType.STORY) {
                String strippedUrl = mPost.url.split("\\?")[0].split("#")[0];
                mWebView.loadUrl(strippedUrl.endsWith(KEY_PDF) ? URL_GOOGLE_DOCS + mPost.url : mPost.url);
            } else {
                mWebView.loadUrl(mPost.url);
            }
        } else {
            showHideOfflineLayout(true);
        }
    }

    private Intent getShareIntent() {
        String shareText = mPost.title + " " + getString(R.string.seperator_name_points)
                + " " + mPost.url + " " + getString(R.string.via) + " " + URL_PLAY_STORE;
        return new Intent()
                .setAction(Intent.ACTION_SEND)
                .setType("text/plain")
                .putExtra(Intent.EXTRA_TEXT, shareText);
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineLayout.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mWebView.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressContainer.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }

    private class ProgressWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String page) {
            mProgressContainer.setVisibility(ProgressBar.GONE);
        }
    }

}
