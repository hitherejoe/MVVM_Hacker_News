package com.hitherejoe.hackernews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Story extends Post implements Parcelable {
    public Long score;
    public String title;
    public StoryType storyType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Story story = (Story) o;

        if (score != null ? !score.equals(story.score) : story.score != null) return false;
        if (storyType != story.storyType) return false;
        if (title != null ? !title.equals(story.title) : story.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = score != null ? score.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (storyType != null ? storyType.hashCode() : 0);
        return result;
    }

    public static enum StoryType {
        LINK("link"),
        ASK("ask");

        private String string;

        StoryType(String string) {
            this.string = string;
        }

        public static StoryType fromString(String string) {
            if (string != null) {
                for (StoryType storyType : StoryType.values()) {
                    if (string.equalsIgnoreCase(storyType.string)) {
                        return storyType;
                    }
                }
            }
            return null;
        }

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.score);
        dest.writeString(this.title);
        dest.writeInt(this.storyType == null ? -1 : this.storyType.ordinal());
        dest.writeString(this.by);
        dest.writeValue(this.id);
        dest.writeValue(this.time);
        dest.writeString(this.type);
        dest.writeSerializable(this.kids);
        dest.writeString(this.url);
    }

    public Story() {
    }

    private Story(Parcel in) {
        this.score = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        int tmpStoryType = in.readInt();
        this.storyType = tmpStoryType == -1 ? null : StoryType.values()[tmpStoryType];
        this.by = in.readString();
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.time = (Integer) in.readValue(Integer.class.getClassLoader());
        this.type = in.readString();
        this.kids = (ArrayList<Long>) in.readSerializable();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>() {
        public Story createFromParcel(Parcel source) {
            return new Story(source);
        }

        public Story[] newArray(int size) {
            return new Story[size];
        }
    };
}
