package com.hitherejoe.hackernews.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hitherejoe.hackernews.BuildConfig;
import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.DataManager;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.util.DataUtils;
import com.hitherejoe.hackernews.util.ToastFactory;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ViewStoryActivity extends BaseActivity {

    @Bind(R.id.web_view)
    WebView mWebView;

    @Bind(R.id.progress_indicator)
    LinearLayout mProgressContainer;

    @Bind(R.id.layout_offline)
    LinearLayout mOfflineLayout;

    private static final String TAG = "WebPageActivity";
    public static final String EXTRA_POST =
            "com.hitherejoe.HackerNews.ui.activity.WebPageActivity.EXTRA_POST";
    private static final String KEY_PDF = "pdf";
    private static final String URL_GOOGLE_DOCS = "http://docs.google.com/gview?embedded=true&url=";
    private static final String URL_PLAY_STORE =
            "https://play.google.com/store/apps/details?id=com.hitherejoe.hackernews&hl=en_GB";
    private Post mPost;
    private DataManager mDataManager;
    private List<Subscription> mSubscriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_story);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        mPost = bundle.getParcelable(EXTRA_POST);
        mDataManager = HackerNewsApplication.get(this).getComponent().dataManager();
        mSubscriptions = new ArrayList<>();
        setupActionBar();
        setupWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (Subscription subscription : mSubscriptions) subscription.unsubscribe();
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
                if (!BuildConfig.DEBUG) AnalyticsHelper.trackViewStoryInBrowserMenuItemClicked(this);
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPost.url)));
                return true;
            case R.id.action_bookmark:
                addBookmark();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        setupWebView();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(mPost.title);
        }
    }

    private void setupShareActionProvider(Menu menu) {
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_item_share));
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(getShareIntent());
            shareActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
                    if (!BuildConfig.DEBUG) AnalyticsHelper.trackStoryShared(ViewStoryActivity.this, intent.getComponent().getPackageName());
                    return false;
                }
            });
        }
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

    private void addBookmark() {
        mSubscriptions.add(mDataManager.addBookmark(this, mPost)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(mDataManager.getScheduler())
                .subscribe(new Observer<Post>() {

                    private Post bookmarkResult;

                    @Override
                    public void onCompleted() {
                        ToastFactory.createToast(
                                ViewStoryActivity.this,
                                bookmarkResult == null ? getString(R.string.bookmark_exists) : getString(R.string.bookmark_added)
                        ).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "There was an error bookmarking the story " + e);
                        ToastFactory.createToast(
                                ViewStoryActivity.this,
                                getString(R.string.bookmark_error)
                        ).show();
                    }

                    @Override
                    public void onNext(Post story) {
                        bookmarkResult = story;
                    }
                }));
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
