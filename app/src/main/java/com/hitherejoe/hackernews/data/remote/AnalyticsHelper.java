package com.hitherejoe.hackernews.data.remote;

import android.content.Context;

import com.google.android.gms.analytics.HitBuilders.EventBuilder;
import com.hitherejoe.hackernews.HackerNewsApplication;

public class AnalyticsHelper {

    private static final String CATEGORY_STORY = "Story";
    private static final String CATEGORY_BOOKMARKS = "Bookmarks";
    private static final String CATEGORY_COMMENTS = "Comments";
    private static final String CATEGORY_USER = "User";
    private static final String CATEGORY_MENU = "Menu";
    private static final String CATEGORY_SOCIAL = "Social";

    private static final String ACTION_STORY_OPENED = "Opened";
    private static final String ACTION_COMMENTS_VIEWED = "Comments";
    private static final String ACTION_USER_VIEWED = "Viewed";
    private static final String ACTION_BOOKMARK_ADDED = "Added";
    private static final String ACTION_BOOKMARK_REMOVED = "Removed";
    private static final String ACTION_MENU_ITEM_CLICKED = "Clicked";
    private static final String ACTION_STORY_SHARED = "Shared";

    private static final String LABEL_STORY_VIEW_CLICK = "View";
    private static final String LABEL_STORY_CARD_CLICK = "Card";
    private static final String LABEL_BOOKMARK_MENU_ITEM = "Bookmarks";
    private static final String LABEL_ABOUT_MENU_ITEM = "About";
    private static final String LABEL_VIEW_IN_BROWSER_MENU_ITEM = "View in browser";


    public static void trackViewStoryClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_STORY)
                .setAction(ACTION_STORY_OPENED)
                .setLabel(LABEL_STORY_VIEW_CLICK)
                .build());
    }

    public static void trackStoryCardClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_STORY)
                .setAction(ACTION_STORY_OPENED)
                .setLabel(LABEL_STORY_CARD_CLICK)
                .build());
    }

    public static void trackViewCommentsClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_COMMENTS)
                .setAction(ACTION_COMMENTS_VIEWED)
                .build());
    }

    public static void trackUserNameClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_USER)
                .setAction(ACTION_USER_VIEWED)
                .build());
    }

    public static void trackBookmarksMenuItemClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_MENU)
                .setAction(ACTION_MENU_ITEM_CLICKED)
                .setLabel(LABEL_BOOKMARK_MENU_ITEM)
                .build());
    }

    public static void trackAboutMenuItemClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_MENU)
                .setAction(ACTION_MENU_ITEM_CLICKED)
                .setLabel(LABEL_ABOUT_MENU_ITEM)
                .build());
    }

    public static void trackStoryShared(Context context, String packageName) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_SOCIAL)
                .setAction(ACTION_STORY_SHARED)
                .setLabel(packageName)
                .build());
    }

    public static void trackViewStoryInBrowserMenuItemClicked(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_MENU)
                .setAction(ACTION_MENU_ITEM_CLICKED)
                .setLabel(LABEL_VIEW_IN_BROWSER_MENU_ITEM)
                .build());
    }

    public static void trackBookmarkAdded(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_BOOKMARKS)
                .setAction(ACTION_BOOKMARK_ADDED)
                .build());
    }

    public static void trackBookmarkRemoved(Context context) {
        HackerNewsApplication.get(context).getAnalyticsTrackerTracker().send(new EventBuilder()
                .setCategory(CATEGORY_BOOKMARKS)
                .setAction(ACTION_BOOKMARK_REMOVED)
                .build());
    }

}
