package com.hitherejoe.hackernews.data.remote;

import android.content.Context;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.hitherejoe.hackernews.R;
import com.hitherejoe.hackernews.data.model.Comment;
import com.hitherejoe.hackernews.data.model.Post;
import com.hitherejoe.hackernews.data.model.Story;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseHelper {

    public static final String ENDPOINT_USER = "https://hacker-news.firebaseio.com/v0/user/";
    public static final String ENDPOINT_TOP_STORIES = "https://hacker-news.firebaseio.com/v0/topstories";
    public static final String ENDPOINT_ITEM = "https://hacker-news.firebaseio.com/v0/item/";

    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_STORY = "story";

    public static final String KEY_TYPE = "type";
    public static final String KEY_TEXT = "text";
    public static final String KEY_BY = "by";
    public static final String KEY_TIME = "time";
    public static final String KEY_KIDS = "kids";
    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_SCORE = "score";
    public static final String KEY_URL = "url";
    public static final String KEY_SUBMITTED = "submitted";

    private Context mContext;

    public FirebaseHelper(Context context) {
        Firebase.setAndroidContext(context);
        mContext = context;
    }

    public void getData(final String endpoint, final DataRetrievedListener onDataRetrievedListener) {
        Firebase ref = new Firebase(endpoint);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Long> submittedIds = new ArrayList<Long>();
                if (endpoint.equals(ENDPOINT_TOP_STORIES)) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        submittedIds.add((Long) snap.getValue());
                    }
                } else {
                    Map<Long, Object> userData = (Map<Long, Object>) snapshot.getValue();
                    submittedIds = (ArrayList<Long>) userData.get(KEY_SUBMITTED);
                }
                onDataRetrievedListener.onDataRetrieved(submittedIds);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mContext, R.string.error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getItem(String id, final ItemRetrievedListener onItemRetrieved) {
        Firebase postRef = new Firebase(ENDPOINT_ITEM + id);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<Long, Object> submittedData = (Map<Long, Object>) dataSnapshot.getValue();
                String type = (String) submittedData.get(KEY_TYPE);
                if (type != null) {
                    if (type.equals(TYPE_COMMENT)) {
                        Comment item = new Comment();
                        item.text = (String) submittedData.get(KEY_TEXT);
                        item.by = (String) submittedData.get(KEY_BY);
                        item.time = (Long) submittedData.get(KEY_TIME);
                        item.kids = (ArrayList<Long>) submittedData.get(KEY_KIDS);
                        if (item.by != null && !item.by.trim().isEmpty()
                                && item.text != null && !item.text.trim().isEmpty()) {
                            onItemRetrieved.onItemRetrieved(item);
                        } else {
                            onItemRetrieved.onItemNotValid();
                        }
                    } else if (type.equals(TYPE_STORY)) {
                        Story item = new Story();
                        item.id = (Long) submittedData.get(KEY_ID);
                        item.title = (String) submittedData.get(KEY_TITLE);
                        item.by = (String) submittedData.get(KEY_BY);
                        item.score = (Long) submittedData.get(KEY_SCORE);
                        item.kids = ((ArrayList<Long>)(submittedData.get(KEY_KIDS)));
                        item.url = (String) submittedData.get(KEY_URL);
                        if (item.by != null && !item.by.trim().isEmpty()
                                && item.title != null && !item.title.trim().isEmpty()) {
                            onItemRetrieved.onItemRetrieved(item);
                        } else {
                            onItemRetrieved.onItemNotValid();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(mContext, R.string.error_generic, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface ItemRetrievedListener {
        public void onItemRetrieved(Post post);
        public void onItemNotValid();
    }

    public interface DataRetrievedListener {
        public void onDataRetrieved(ArrayList<Long> ids);
    }
}
