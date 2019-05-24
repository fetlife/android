package com.bitlove.fetlife.event;

import com.bitlove.fetlife.github.dto.Release;

public class LatestReleaseEvent {

    private final Release latestRelease;
    private final Release latestPreRelease;
    private final boolean forcedCheck;

    public LatestReleaseEvent(Release latestRelease, Release latestPreRelease, boolean forcedCheck) {
        this.latestRelease = latestRelease;
        this.latestPreRelease = latestPreRelease;
        this.forcedCheck = forcedCheck;
    }

    public Release getLatestRelease() {
        return latestRelease;
    }

    public Release getLatestPreRelease() {
        return latestPreRelease;
    }

    public boolean isForcedCheck() {
        return forcedCheck;
    }
}
