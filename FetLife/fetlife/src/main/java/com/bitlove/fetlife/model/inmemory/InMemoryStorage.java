package com.bitlove.fetlife.model.inmemory;

import android.util.SparseArray;

import com.bitlove.fetlife.model.pojos.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * Store class for resource objects that should nto into the Database
 */
public class InMemoryStorage {

    private SparseArray<List<Story>> feeds = new SparseArray<>();

    public void clearFeed() {
        synchronized (feeds) {
            feeds.clear();
        }
    }

    public void addFeed(int page, List<Story> stories) {
        synchronized (feeds) {
            feeds.put(page,stories);
        }
    }

    public List<Story> getFeed() {
        synchronized (feeds) {
            List<Story> feed = new ArrayList<>();
            for (int i = 0; i < feeds.size(); i++) {
                feed.addAll(feeds.get(feeds.keyAt(i)));
            }
            return feed;
        }
    }


}
