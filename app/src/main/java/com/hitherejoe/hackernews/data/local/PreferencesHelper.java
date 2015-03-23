package com.hitherejoe.hackernews.data.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesHelper {

    private static SharedPreferences mPref;

    public static final String PREF_FILE_NAME = "hacker_news_pref_file";

    private static final String PREF_KEY_DIALOG_FLAG = "PREF_KEY_DIALOG_FLAG";


    public PreferencesHelper(Context context) {
        mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public void clear() {
        mPref.edit().clear().apply();
    }

    public void putDialogFlag() {
        mPref.edit().putBoolean(PREF_KEY_DIALOG_FLAG, true).apply();
    }

    public boolean getDialogFlag() {
        return mPref.getBoolean(PREF_KEY_DIALOG_FLAG, false);
    }

}
