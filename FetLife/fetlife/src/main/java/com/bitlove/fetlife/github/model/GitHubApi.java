package com.bitlove.fetlife.github.model;

import com.bitlove.fetlife.github.dto.Release;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GitHubApi {

    @GET("/repos/fetlife/android/releases/latest")
    Call<Release> getLatestRelease();

    @GET("/repos/fetlife/android/releases")
    Call<List<Release>> getReleases();

}
