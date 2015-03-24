package com.hitherejoe.hackernews.ui.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.remote.AnalyticsHelper;
import com.hitherejoe.hackernews.ui.fragment.StoriesFragment;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFragment();
        if (!HackerNewsApplication.get().getDataManager().getPreferencesHelper().getDialogFlag()) showRateDialog();
    }

    private void showRateDialog() {

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
                AnalyticsHelper.trackAboutMenuItemClicked();
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_bookmarks:
                AnalyticsHelper.trackBookmarksMenuItemClicked();
                startActivity(new Intent(this, BookmarksActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new StoriesFragment();
        ft.replace(R.id.content_frame, fragment);
        ft.commit();
    }
}
