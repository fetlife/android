package com.bitlove.fetlife.github.view

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bitlove.fetlife.R
import com.bitlove.fetlife.databinding.ActivityGithubReleaseNotesBinding
import com.bitlove.fetlife.github.dto.Releases
import com.bitlove.fetlife.github.logic.GitHubReleasesViewModel
import com.bitlove.fetlife.github.logic.ReleaseHandler
import com.bitlove.fetlife.view.screen.BaseActivity
import com.bitlove.fetlife.view.screen.component.MenuActivityComponent
import org.koin.androidx.viewmodel.ext.android.viewModel

class GitHubReleaseNotesActivity : BaseActivity() {

    private val viewModel: GitHubReleasesViewModel by viewModel()

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(createIntent(context))
        }

        fun createIntent(context: Context): Intent {
            val intent = Intent(context, GitHubReleaseNotesActivity::class.java)
            intent.putExtra(EXTRA_HAS_BOTTOM_BAR, true)
            return intent
        }
    }

    override fun onCreateActivityComponents() {
        addActivityComponent(MenuActivityComponent())
    }

    override fun onSetContentView() {
        val binding: ActivityGithubReleaseNotesBinding = DataBindingUtil.setContentView(this, R.layout.activity_github_release_notes)
        viewModel.gitHubReleases.observe(this, Observer {
            binding.releaseHandler = ReleaseHandler()
            binding.releases = Releases(it)
            binding.executePendingBindings()
        })
        binding.lifecycleOwner = this
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}