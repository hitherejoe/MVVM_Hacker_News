package com.hitherejoe.hackernews.util;

import android.content.Context;
import android.content.DialogInterface;

import com.hitherejoe.hackernews.HackerNewsApplication;
import com.hitherejoe.hackernews.data.local.PreferencesHelper;

public class RateUtils {

    private final static int DAYS_UNTIL_PROMPT = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 6;

    public static void showRateDialog(Context context, DialogInterface.OnClickListener onClickListener) {
        PreferencesHelper preferencesHelper =
                HackerNewsApplication.get().getDataManager().getPreferencesHelper();

        Long firstLaunch = preferencesHelper.getFirstLaunch();
        long launchCount = preferencesHelper.getLaunchCount();

        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                DialogFactory.createRateDialog(
                        context,
                        onClickListener
                ).show();
            }
        }
    }

}