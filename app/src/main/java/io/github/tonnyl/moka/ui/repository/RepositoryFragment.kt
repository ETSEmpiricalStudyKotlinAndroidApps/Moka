package io.github.tonnyl.moka.ui.repository

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentRepositoryBinding
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.net.Status
import io.github.tonnyl.moka.ui.issues.IssuesFragmentArgs
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.fragment_repository.*

class RepositoryFragment : Fragment() {

    private lateinit var viewModel: RepositoryViewModel

    private lateinit var binding: FragmentRepositoryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepositoryBinding.inflate(inflater, container, false)

        return binding.root
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

        repository_bottom_app_bar.replaceMenu(R.menu.fragment_repository_menu)

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
                        repository_language_content.text = resources.data.primaryLanguage.name
                        (repository_language_content.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.parseColor(resources.data.primaryLanguage.color))
                    } else {
                        repository_language_content.text = context?.getString(R.string.programming_language_unknown)
                        (repository_language_content.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(Color.BLACK)
                    }
                    repository_license_content.text = resources.data?.licenseInfo?.name

                    val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
                    resources.data?.updatedAt?.let {
                        repository_updated_on_content.text = DateUtils.formatDateTime(requireContext(), it.time, flags)
                    }
                    resources.data?.createdAt?.let {
                        repository_created_on_content.text = DateUtils.formatDateTime(requireContext(), it.time, flags)
                    }

                    repository_branch_content.text = resources.data?.branchCount.toString()
                    repository_releases_content.text = resources.data?.releasesCount.toString()

                    val watchersCount = resources.data?.watchersCount ?: 0
                    repository_watchers_count_text.text = formatNumberWithSuffix(watchersCount)
                    val stargazersCount = resources.data?.stargazersCount ?: 0
                    repository_stargazers_count_text.text = formatNumberWithSuffix(stargazersCount)
                    val forksCount = resources.data?.forksCount ?: 0
                    repository_forks_count_text.text = formatNumberWithSuffix(forksCount)
                    val issuesCount = resources.data?.issuesCount ?: 0
                    repository_issues_count_text.text = formatNumberWithSuffix(issuesCount)

                    repository_issues_text_layout.setOnClickListener {
                        val args = IssuesFragmentArgs.Builder(loginArg, nameArg)
                        parentFragment?.findNavController()?.navigate(R.id.action_to_issues, args.build().toBundle())
                    }

                    val pullRequestsCount = resources.data?.pullRequestsCount ?: 0
                    repository_pull_requests_count_text.text = formatNumberWithSuffix(pullRequestsCount)

                    repository_pull_requests_text_layout.setOnClickListener {
                        val args = PullRequestsFragmentArgs.Builder(loginArg, nameArg)
                        parentFragment?.findNavController()?.navigate(R.id.action_to_prs, args.build().toBundle())
                    }

                    val projectsCount = resources.data?.projectsCount ?: 0
                    repository_projects_count_text.text = formatNumberWithSuffix(projectsCount)

                    resources.data?.defaultBranchRef?.let {
                        observeReadmeFileNameData(resources.data.defaultBranchRef.name)
                    }

                    resources.data?.topics?.let { topicList ->
                        repository_topics.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            val adapter = RepositoryTopicAdapter()
                            repository_topics.adapter = adapter
                            adapter.submitList(topicList)
                        }
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

    }

    private fun observeReadmeFileNameData(branchName: String) {
        viewModel.setBranchName(branchName).observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    if (resources.data != null) {
                        observeReadmeFileContent("$branchName:${resources.data.second}")
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