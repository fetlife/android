package com.bitlove.fetlife

import com.bitlove.fetlife.github.model.GitHubRepository
import com.bitlove.fetlife.github.model.GitHubService
import com.bitlove.fetlife.github.logic.GitHubReleasesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KoinHelper {

    companion object {
        fun start(application: FetLifeApplication) {

            startKoin {
                androidLogger()
                androidContext(application)
                modules(module {
                    single<GitHubService> { GitHubService() }
                    single<GitHubRepository> { GitHubRepository() }
                    viewModel { GitHubReleasesViewModel() }
                })
            }


        }
    }
}