package com.bitlove.fetlife.legacy.model.inmemory;

import android.util.SparseArray;

import com.bitlove.fetlife.legacy.model.pojos.fetlife.dbjson.Event;
import com.bitlove.fetlife.legacy.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.legacy.view.screen.resource.ExploreActivity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Store class for resource objects that should not go into the Database
 */
public class InMemoryStorage {

    private SparseArray<List<Story>> feeds = new SparseArray<>();
    private SparseArray<List<Story>> profileFeeds = new SparseArray<>();
    private Set<Event> mapEvents = new HashSet<>();
    private Set<LatLng> mapPositions = new HashSet<>();
    private SparseArray<List<Story>> stuffYouLoveFeeds = new SparseArray<>();
    private SparseArray<List<Story>> freshAndPervyFeeds = new SparseArray<>();
    private SparseArray<List<Story>> kinkyAndPopularFeeds = new SparseArray<>();

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

    public void clearExploreFeed(ExploreActivity.Explore exploreType) {
        SparseArray<List<Story>> feeds = getFeeds(exploreType);
        synchronized (feeds) {
            feeds.clear();
        }
    }

    public void addExploreFeed(ExploreActivity.Explore exploreType, int page, List<Story> stories) {
        SparseArray<List<Story>> feeds = getFeeds(exploreType);
        synchronized (feeds) {
            feeds.put(page,stories);
        }
    }

    public List<Story> getExploreFeed(ExploreActivity.Explore exploreType) {
        SparseArray<List<Story>> feeds = getFeeds(exploreType);
        synchronized (feeds) {
            List<Story> stuffYouLoveFeed = new ArrayList<>();
            for (int i = 0; i < feeds.size(); i++) {
                stuffYouLoveFeed.addAll(feeds.get(feeds.keyAt(i)));
            }
            return stuffYouLoveFeed;
        }
    }

    private SparseArray<List<Story>> getFeeds(ExploreActivity.Explore exploreType) {
        switch (exploreType) {
            case STUFF_YOU_LOVE:
                return stuffYouLoveFeeds;
            case FRESH_AND_PERVY:
                return freshAndPervyFeeds;
            case KINKY_AND_POPULAR:
                return kinkyAndPopularFeeds;
            default:
                return null;
        }
    }

    public void clearProfileFeed() {
        synchronized (profileFeeds) {
            profileFeeds.clear();
        }
    }

    public void addProfileFeed(int page, List<Story> stories) {
        synchronized (feeds) {
            profileFeeds.put(page,stories);
        }
    }

    public List<Story> getProfileFeed() {
        synchronized (feeds) {
            List<Story> feed = new ArrayList<>();
            for (int i = 0; i < profileFeeds.size(); i++) {
                feed.addAll(profileFeeds.get(profileFeeds.keyAt(i)));
            }
            return feed;
        }
    }

    public Set<Event> getMapEvents() {
        return mapEvents;
    }

    public Set<LatLng> getMapPositions() {
        return mapPositions;
    }
}
