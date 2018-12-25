package io.github.tonnyl.moka.ui.repository

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.net.Status
import io.github.tonnyl.moka.ui.issues.IssuesFragmentArgs
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs
import kotlinx.android.synthetic.main.fragment_repository.*

class RepositoryFragment : Fragment() {

    private lateinit var viewModel: RepositoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_repository, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = RepositoryFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).login
        val nameArg = RepositoryFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).name

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        val factory = ViewModelFactory(loginArg, nameArg)
        viewModel = ViewModelProviders.of(this, factory).get(RepositoryViewModel::class.java)

        viewModel.repositoryResult.observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    GlideLoader.loadAvatar(resources.data?.ownerAvatarUrl?.toString(), repository_owner_avatar)
                    repository_owner_name.text = resources.data?.ownerName
                    repository_owner_login.text = resources.data?.ownerLogin
                    repository_name.text = nameArg
                    repository_description.text = resources.data?.description

                    if (resources.data?.primaryLanguage != null) {
                        repository_language_text.text = resources.data.primaryLanguage.name
                        (repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.parseColor(resources.data.primaryLanguage.color))
                    } else {
                        repository_language_text.text = context?.getString(R.string.programming_language_unknown)
                        (repository_language_text.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.BLACK)
                    }
                    repository_license.text = resources.data?.licenseInfo?.name
                    if (resources.data?.updatedAt != null) {
                        repository_updated_at.text = getString(R.string.repository_update_time, DateUtils.getRelativeTimeSpanString(context
                                ?: return@Observer, resources.data.updatedAt.time, true))
                    }

                    val watchersCount = resources.data?.watchersCount ?: 0
                    repository_watchers.text = getString(R.string.repository_watchers, watchersCount, this.resources.getQuantityString(R.plurals.watchers_count_plurals, watchersCount))
                    val stargazersCount = resources.data?.stargazersCount ?: 0
                    repository_stars.text = getString(R.string.repository_stargazers, stargazersCount, this.resources.getQuantityString(R.plurals.stargazers_count_plurals, stargazersCount))
                    val forksCount = resources.data?.forksCount ?: 0
                    repository_forks.text = getString(R.string.repository_forks, forksCount, this.resources.getQuantityString(R.plurals.forks_count_plurals, forksCount))
                    val issuesCount = resources.data?.issuesCount ?: 0
                    repository_issues.text = getString(R.string.repository_issues, issuesCount, this.resources.getQuantityString(R.plurals.issues_count_plurals, issuesCount))

                    repository_issues.setOnClickListener {
                        val args = IssuesFragmentArgs.Builder(loginArg, nameArg)
                        parentFragment?.findNavController()?.navigate(R.id.action_to_issues, args.build().toBundle())
                    }

                    val pullRequestsCount = resources.data?.pullRequestsCount ?: 0
                    repository_pull_requests.text = getString(R.string.repository_pull_requests, pullRequestsCount, this.resources.getQuantityString(R.plurals.pull_requests_count_plurals, pullRequestsCount))

                    repository_pull_requests.setOnClickListener {
                        val args = PullRequestsFragmentArgs.Builder(loginArg, nameArg)
                        parentFragment?.findNavController()?.navigate(R.id.action_to_prs, args.build().toBundle())
                    }

                    val projectsCount = resources.data?.projectsCount ?: 0
                    repository_projects.text = getString(R.string.repository_projects, projectsCount, this.resources.getQuantityString(R.plurals.projects_count_plurals, projectsCount))

                    if (resources.data?.defaultBranchRef != null) {
                        observeReadmeFileNameData(resources.data.defaultBranchRef.name)
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

        repository_scroll_view.setOnScrollChangeListener { _: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (repository_scroll_view.canScrollVertically(-1).not()) {
                ViewCompat.setElevation(appbar, 0f)
            } else if (appbar.elevation == 0f) {
                ViewCompat.setElevation(appbar, resources.getDimension(R.dimen.toolbar_elevation))
            }
        }

    }

    private fun observeReadmeFileNameData(branchName: String) {
        viewModel.setBranchName(branchName).observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    if (resources.data != null) {
                        observeReadmeFileContent("$branchName:${resources.data.second}")
                        repository_readme_title.text = resources.data.second
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })
    }

    private fun observeReadmeFileContent(expression: String) {
        viewModel.setExpression(expression).observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    repository_readme_content.apply {
                        isScrollbarFadingEnabled = true
                        settings.javaScriptEnabled = false
                        settings.builtInZoomControls = false
                        settings.cacheMode = WebSettings.LOAD_NO_CACHE
                        settings.domStorageEnabled = true
                        settings.setSupportZoom(false)
                        settings.builtInZoomControls = false
                        settings.displayZoomControls = false
                        isVerticalScrollBarEnabled = false
                        isHorizontalScrollBarEnabled = false
                        settings.setAppCacheEnabled(false)
                    }

                    val html = resources.data ?: return@Observer
                    repository_readme_content.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })
    }

}