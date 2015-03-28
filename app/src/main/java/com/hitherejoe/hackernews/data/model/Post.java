package com.hitherejoe.hackernews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Post implements Parcelable {

    public Long id;
    public String by;
    public Integer time;
    public String type;
    public ArrayList<Long> kids;
    public String url;
    public Long score;
    public String title;
    public PostType postType;

    public static enum PostType {
        LINK("link"),
        ASK("ask");

        private String string;

        PostType(String string) {
            this.string = string;
        }

        public static PostType fromString(String string) {
            if (string != null) {
                for (PostType postType : PostType.values()) {
                    if (string.equalsIgnoreCase(postType.string)) return postType;
                }
            }
            return null;
        }
    }

    public Post() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post story = (Post) o;

        if (by != null ? !by.equals(story.by) : story.by != null) return false;
        if (id != null ? !id.equals(story.id) : story.id != null) return false;
        if (kids != null ? !kids.equals(story.kids) : story.kids != null) return false;
        if (score != null ? !score.equals(story.score) : story.score != null) return false;
        if (postType != story.postType) return false;
        if (time != null ? !time.equals(story.time) : story.time != null) return false;
        if (title != null ? !title.equals(story.title) : story.title != null) return false;
        if (type != null ? !type.equals(story.type) : story.type != null) return false;
        if (url != null ? !url.equals(story.url) : story.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = by != null ? by.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (kids != null ? kids.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (postType != null ? postType.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.by);
        dest.writeValue(this.id);
        dest.writeValue(this.time);
        dest.writeString(this.type);
        dest.writeSerializable(this.kids);
        dest.writeString(this.url);
        dest.writeValue(this.score);
        dest.writeString(this.title);
        dest.writeInt(this.postType == null ? -1 : this.postType.ordinal());
    }

    private Post(Parcel in) {
        this.by = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.time = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = in.readString();
        this.kids = (ArrayList<Long>) in.readSerializable();
        this.url = in.readString();
        this.score = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        int tmpStoryType = in.readInt();
        this.postType = tmpStoryType == -1 ? null : PostType.values()[tmpStoryType];
    }

    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        public Post createFromParcel(Parcel source) {
            return new Post(source);
        }

        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
}
