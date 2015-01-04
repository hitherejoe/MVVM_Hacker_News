package com.hitherejoe.hackernews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Story extends Post implements Parcelable {
    public Long score;
    public String title;
    public boolean isBookmarked;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.score);
        dest.writeString(this.title);
        dest.writeByte(isBookmarked ? (byte) 1 : (byte) 0);
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
        this.isBookmarked = in.readByte() != 0;
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
