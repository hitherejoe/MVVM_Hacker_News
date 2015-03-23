package com.hitherejoe.hackernews.util;

import com.hitherejoe.hackernews.data.model.Bookmark;
import com.hitherejoe.hackernews.data.model.Story;

import java.util.ArrayList;

public class DataUtils {

    public static Bookmark createBookmarkObject(Story story) {
        Bookmark bookmark = new Bookmark();
        bookmark.by = story.by;
        bookmark._id = story.id;
        bookmark.score = story.score;
        bookmark.title = story.title;
        bookmark.url = story.url;
        return bookmark;
    }

    public static Story createStoryObject(Bookmark bookmark) {
        Story story = new Story();
        story.by = bookmark.by;
        story.id = bookmark._id;
        story.score = bookmark.score;
        story.title = bookmark.title;
        story.url = bookmark.url;
        return story;
    }
}
