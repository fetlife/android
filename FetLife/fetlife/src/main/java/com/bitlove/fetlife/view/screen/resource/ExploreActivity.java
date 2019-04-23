package com.bitlove.fetlife.view.screen.resource;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.bitlove.fetlife.model.pojos.fetlife.json.Story;
import com.bitlove.fetlife.model.service.FetLifeApiIntentService;
import com.bitlove.fetlife.view.adapter.ResourceListRecyclerAdapter;
import com.bitlove.fetlife.view.adapter.feed.ExploreRecyclerAdapter;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

public class ExploreActivity extends FeedActivity {

    private static final String EXTRA_EXPLORE = "EXTRA_EXPLORE";
    private Explore exploreType;

    public enum Explore {
        FRESH_AND_PERVY,
        STUFF_YOU_LOVE,
        KINKY_AND_POPULAR
    }

    public static void startActivity(Context context, Explore type) {
        context.startActivity(createIntent(context, type));
    }

    public static Intent createIntent(Context context, Explore type) {
        Intent intent = new Intent(context, ExploreActivity.class);
        intent.putExtra(EXTRA_EXPLORE,type);
        intent.putExtra(EXTRA_HAS_BOTTOM_BAR, true);
        if (FetLifeApplication.getInstance().getUserSessionManager().getActiveUserPreferences().getBoolean(context.getString(R.string.settings_key_general_feed_as_start),false)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        exploreType = (Explore) getIntent().getSerializableExtra(EXTRA_EXPLORE);
        super.onCreate(savedInstanceState);
        switch (exploreType) {
            case KINKY_AND_POPULAR:
                setTitle(R.string.title_activity_kinky_and_popular);
                break;
            case FRESH_AND_PERVY:
                setTitle(R.string.title_activity_fresh_and_pervy);
                break;
            case STUFF_YOU_LOVE:
                setTitle(R.string.title_activity_stuff_you_love);
                break;
        }
    }

    protected void logEvent() {
        Answers.getInstance().logCustom(
                new CustomEvent(getClass().getSimpleName() + ":" + exploreType));
    }

    @Override
    protected ResourceListRecyclerAdapter<Story, ?> createRecyclerAdapter(Bundle savedInstanceState) {
        return new ExploreRecyclerAdapter(getFetLifeApplication(), exploreType, this, null);
    }

    @Override
    protected String getApiCallAction() {
        return FetLifeApiIntentService.ACTION_APICALL_EXPLORE;
    }

    @Override
    protected List<String> getApiParams() {
        List<String> params = new ArrayList<>();
        params.add(exploreType.toString());
        return params;
    }
}
