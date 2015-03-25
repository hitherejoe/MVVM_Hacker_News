package com.hitherejoe.hackernews.util;

import com.hitherejoe.hackernews.data.model.Story;

import java.util.Random;

public class MockModelsUtil {

    public static Long generateRandomString() {
        return new Random().nextLong();
    }

    public static Story createMockStory() {
        Story story = new Story();
        story.id = generateRandomString();
        story.storyType = Story.StoryType.LINK;
        story.url = "www.hitherejoe.com";
        story.title = "Title";
        story.score = 1000L;
        story.by = "JoeBirch";
        return story;
    }

}