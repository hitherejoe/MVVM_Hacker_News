package com.hitherejoe.hackernews.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.ui.fragment.StoriesFragment;
import com.hitherejoe.hackernews.util.RateUtils;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    public static Intent getStartIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addStoriesFragment();
        if (HackerNewsApplication.get(this).getComponent().dataManager().getPreferencesHelper().shouldShowRateDialog()) {
            RateUtils.showRateDialog(this, mOnRateDialogClickListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                AnalyticsHelper.trackAboutMenuItemClicked(this);
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_bookmarks:
                AnalyticsHelper.trackBookmarksMenuItemClicked(this);
                startActivity(new Intent(this, BookmarksActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addStoriesFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_frame, new StoriesFragment())
                .commit();
    }

    private OnClickListener mOnRateDialogClickListener = new OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case -2:
                    HackerNewsApplication.get(MainActivity.this).getComponent().dataManager().getPreferencesHelper().putRateDialogShownFlag();
                    break;
                case -1:
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    HackerNewsApplication.get(MainActivity.this).getComponent().dataManager().getPreferencesHelper().putRateDialogShownFlag();
                    break;
            }
            dialog.dismiss();
        }
    };

}
