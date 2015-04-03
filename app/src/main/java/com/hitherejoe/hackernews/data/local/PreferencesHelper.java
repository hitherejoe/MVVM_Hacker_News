package com.hitherejoe.hackernews.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static SharedPreferences mPref;

    public static final String PREF_FILE_NAME = "hacker_news_pref_file";

    private static final String PREF_KEY_DIALOG_FLAG = "PREF_KEY_DIALOG_FLAG";
    private static final String PREF_KEY_LAUNCH_COUNT = "PREF_KEY_LAUNCH_COUNT";
    private static final String PREF_KEY_FIRST_LAUNCH = "PREF_KEY_FIRST_LAUNCH";


    public PreferencesHelper(Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public boolean shouldShowRateDialog() {
        return mPref.getBoolean(PREF_KEY_DIALOG_FLAG, true);
    }

    public void putRateDialogShownFlag() {
        mPref.edit().putBoolean(PREF_KEY_DIALOG_FLAG, false).apply();
    }

    public long getLaunchCount() {
        long launchCount = mPref.getLong(PREF_KEY_LAUNCH_COUNT, 0) + 1;
        mPref.edit().putLong(PREF_KEY_LAUNCH_COUNT, launchCount).apply();
        return launchCount;
    }

    public Long getFirstLaunch() {
        Long firstLaunch = mPref.getLong(PREF_KEY_FIRST_LAUNCH, 0);
        if (firstLaunch == 0) {
            firstLaunch = System.currentTimeMillis();
            mPref.edit().putLong(PREF_KEY_FIRST_LAUNCH, firstLaunch).apply();
        }
        return firstLaunch;
    }

}
