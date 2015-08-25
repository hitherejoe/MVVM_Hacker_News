package com.hitherejoe.mvvm_hackernews.util;

import com.hitherejoe.mvvm_hackernews.model.Comment;
import com.hitherejoe.mvvm_hackernews.model.Post;
import com.hitherejoe.mvvm_hackernews.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MockModelsUtil {

    public static Long generateRandomLong() {
        return new Random().nextLong();
    }

    public static String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    public static int generateRandomInt() {
        return new Random().nextInt(80 - 65) + 65;
    }

    public static Post createMockStory() {
        Post story = new Post();
        story.id = generateRandomLong();
        story.postType = Post.PostType.STORY;
        story.url = "http://www.hitherejoe.com";
        story.title = generateRandomString();
        story.score = 1000L;
        story.by = "JoeBirch";
        story.time = new Date().getTime();
        return story;
    }

    public static Post createMockStoryWithText() {
        Post story = createMockStory();
        story.text = generateRandomString();
        return story;
    }

    public static Post createMockStoryWithTitle(String title) {
        Post story = createMockStory();
        story.title = title;
        story.postType = Post.PostType.STORY;
        return story;
    }

    public static Post createMockJobWithTitle(String title) {
        Post story = createMockStory();
        story.title = title;
        story.postType = Post.PostType.JOB;
        return story;
    }

    public static Post createMockStoryWithId(long id) {
        Post story = createMockStory();
        story.id = id;
        return story;
    }

    public static Post createMockAskStoryWithTitle(String title) {
        Post story = createMockStory();
        story.title = title;
        story.postType = Post.PostType.ASK;
        story.url = "";
        return story;
    }

    public static Comment createMockComment() {
        Comment comment = new Comment();
        comment.by = generateRandomString();
        comment.comments = new ArrayList<>();
        comment.depth = generateRandomInt();
        comment.id = generateRandomLong();
        comment.isTopLevelComment = false;
        comment.text = generateRandomString();
        comment.time = new Date().getTime();
        return comment;
    }

    public static User createMockUser() {
        User user = new User();
        user.id = generateRandomString();
        user.about = "about";
        user.karma = 100;
        user.submitted = new ArrayList<>();
        user.submitted.add(102234L);
        user.submitted.add(123454L);
        user.submitted.add(773454L);
        user.submitted.add(666454L);
        return user;
    }

    public static List<Long> createMockPostIdList(int count) {
        List<Long> idList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            idList.add(generateRandomLong());
        }
        return idList;
    }

}