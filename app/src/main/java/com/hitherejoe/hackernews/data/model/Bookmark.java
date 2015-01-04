package com.hitherejoe.hackernews.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Bookmark implements Parcelable {
    public String by;
    public Long _id;
    public String title;
    public Long score;
    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.by);
        dest.writeValue(this._id);
        dest.writeString(this.title);
        dest.writeValue(this.score);
        dest.writeString(this.url);
    }

    public Bookmark() {
    }

    private Bookmark(Parcel in) {
        this.by = in.readString();
        this._id = (Long) in.readValue(Long.class.getClassLoader());
        this.title = in.readString();
        this.score = (Long) in.readValue(Long.class.getClassLoader());
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Bookmark> CREATOR = new Parcelable.Creator<Bookmark>() {
        public Bookmark createFromParcel(Parcel source) {
            return new Bookmark(source);
        }

        public Bookmark[] newArray(int size) {
            return new Bookmark[size];
        }
    };
}
