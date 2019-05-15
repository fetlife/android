package com.bitlove.fetlife.github.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bitlove.fetlife.github.model.GitHubRepository
import com.bitlove.fetlife.github.dto.Release
import org.koin.core.KoinComponent
import org.koin.core.inject

class GitHubReleasesViewModel : ViewModel(), KoinComponent {

    private val gitHubRepository: GitHubRepository by inject()

    val gitHubReleases: LiveData<List<Release>>

    init {
        gitHubReleases = Transformations.map(gitHubRepository.getReleases()) { result -> result }
    }
}