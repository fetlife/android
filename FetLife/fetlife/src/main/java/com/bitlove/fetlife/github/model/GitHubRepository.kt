package com.bitlove.fetlife.github.model

import androidx.lifecycle.LiveData
import org.koin.core.KoinComponent
import org.koin.core.inject
import androidx.lifecycle.MutableLiveData
import com.bitlove.fetlife.github.dto.Release
import com.bitlove.fetlife.util.VersionUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class GitHubRepository : KoinComponent {

    private val gitHubService: GitHubService by inject()

    fun getReleases(): LiveData<List<Release>> {
        val result = MutableLiveData<List<Release>>()
        gitHubService.gitHubApi.releases.enqueue(object : Callback<List<Release>> {
            override fun onFailure(call: Call<List<Release>>, t: Throwable) {
                result.value = null
            }

            override fun onResponse(call: Call<List<Release>>, response: Response<List<Release>>) {
                val releaseList = response.body()?.sortedWith(VersionUtil.getReleaseComparator())
                result.value = releaseList
            }
        })
        return result
    }

}