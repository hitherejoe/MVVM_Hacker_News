package com.hitherejoe.mvvm_hackernews.model;

import java.util.ArrayList;

public class Comment {

    public String text;
    public Long time;
    public String by;
    public Long id;
    public String type;
    public ArrayList<Long> kids;
    public ArrayList<Comment> comments;
    public int depth;
    public boolean isTopLevelComment;

    public Comment() {
        comments = new ArrayList<>();
        isTopLevelComment = false;
        depth = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Comment comment = (Comment) o;

        if (depth != comment.depth) return false;
        if (isTopLevelComment != comment.isTopLevelComment) return false;
        if (by != null ? !by.equals(comment.by) : comment.by != null) return false;
        if (comments != null ? !comments.equals(comment.comments) : comment.comments != null)
            return false;
        if (id != null ? !id.equals(comment.id) : comment.id != null) return false;
        if (kids != null ? !kids.equals(comment.kids) : comment.kids != null) return false;
        if (text != null ? !text.equals(comment.text) : comment.text != null) return false;
        if (time != null ? !time.equals(comment.time) : comment.time != null) return false;
        if (type != null ? !type.equals(comment.type) : comment.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (by != null ? by.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (kids != null ? kids.hashCode() : 0);
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + depth;
        result = 31 * result + (isTopLevelComment ? 1 : 0);
        return result;
    }
}
