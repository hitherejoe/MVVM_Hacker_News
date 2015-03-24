package com.hitherejoe.hackernews.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.local.DatabaseHelper;
import com.hitherejoe.hackernews.data.model.Story;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.util.ToastFactory;
import com.hitherejoe.hackernews.util.ViewUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class WebPageActivity extends BaseActivity {

    @InjectView(R.id.web_view)
    WebView mWebView;

    @InjectView(R.id.progress_indicator)
    LinearLayout mProgressBar;

    @InjectView(R.id.layout_offline)
    LinearLayout mOfflineContainer;

    public static final String EXTRA_POST_URL =
            "com.hitherejoe.HackerNews.ui.activity.WebPageActivity.EXTRA_POST_URL";
    private static final String KEY_PDF = "pdf";
    private static final String PDF_URL = "http://docs.google.com/gview?embedded=true&url=";
    private static final String PLAY_STORE_URL =
            "https://play.google.com/store/apps/details?id=com.hitherejoe.hackernews&hl=en_GB";
    private Story mPost;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);
        ButterKnife.inject(this);
        Bundle bundle = getIntent().getExtras();
        mPost = bundle.getParcelable(EXTRA_POST_URL);
        mDatabaseHelper = HackerNewsApplication.get().getDataManager().getDatabaseHelper();
        setupActionBar();
        setupContent();
    }

    private Intent getShareIntent() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareText = mPost.title + " " + getString(R.string.seperator_name_points)
                + " " + mPost.url + " " + getString(R.string.via) + " " + PLAY_STORE_URL;
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        return shareIntent;
    }

    private void setupActionBar() {
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mPost.title);
    }

    private void setupContent() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) mProgressBar.setVisibility(ProgressBar.GONE);
            }
        });
        mWebView.setWebViewClient(new CustomWebViewClient());
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

        if (ViewUtils.isNetworkAvailable(this)) {
            showHideOfflineLayout(false);
            if (mPost.url != null) {
                String urlEnd = mPost.url.substring(mPost.url.length() - 3);
                if (urlEnd.equals(KEY_PDF)) {
                    mWebView.loadUrl(PDF_URL + mPost.url);
                } else {
                    mWebView.loadUrl(mPost.url);
                }
            }
        } else {
            showHideOfflineLayout(true);
        }
    }

    private void showHideOfflineLayout(boolean isOffline) {
        mOfflineContainer.setVisibility(isOffline ? View.VISIBLE : View.GONE);
        mWebView.setVisibility(isOffline ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(isOffline ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_page, menu);
        setupShareActionProvider(menu);
        return true;
    }

    private void setupShareActionProvider(Menu menu) {
        ShareActionProvider shareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.menu_item_share));
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(getShareIntent());
            shareActionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {
                @Override
                public boolean onShareTargetSelected(ShareActionProvider shareActionProvider, Intent intent) {
                    AnalyticsHelper.trackStoryShared(intent.getComponent().getPackageName());
                    return false;
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_browser:
                AnalyticsHelper.trackViewStoryInBrowserMenuItemClicked();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPost.url)));
                return true;
            case R.id.action_bookmark:
                handleBookarkAdded();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.button_try_again)
    public void onTryAgainClick() {
        setupContent();
    }

    private void handleBookarkAdded() {
        if (mDatabaseHelper.doesBookmarkExist(mPost)) {
            ToastFactory.createToast(this, getString(R.string.bookmark_exists)).show();
        } else {
            mDatabaseHelper.bookmarkStory(mPost);
            ToastFactory.createToast(this, getString(R.string.bookmark_added)).show();
        }
    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String page) {
            mProgressBar.setVisibility(ProgressBar.GONE);
        }
    }

}
