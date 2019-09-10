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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentRepositoryBinding
import io.github.tonnyl.moka.network.GlideLoader
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.issues.IssuesFragmentArgs
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs
import io.github.tonnyl.moka.util.formatNumberWithSuffix

class RepositoryFragment : Fragment() {

    private val args by navArgs<RepositoryFragmentArgs>()

    private val viewModel by viewModels<RepositoryViewModel> {
        ViewModelFactory(args.login, args.name)
    }

    private lateinit var binding: FragmentRepositoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = args.login
        val nameArg = args.name

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.repositoryBottomAppBar.replaceMenu(R.menu.fragment_repository_menu)

        viewModel.repositoryResult.observe(this, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    GlideLoader.loadAvatar(
                        resources.data?.ownerAvatarUrl?.toString(),
                        binding.repositoryOwnerAvatar
                    )
                    binding.repositoryOwnerName.text = resources.data?.ownerName
                    binding.repositoryOwnerLogin.text = resources.data?.ownerLogin
                    binding.repositoryName.text = nameArg
                    binding.repositoryDescription.text = resources.data?.description

                    if (resources.data?.primaryLanguage != null) {
                        binding.repositoryLanguageContent.text = resources.data.primaryLanguage.name
                        (binding.repositoryLanguageContent.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
                            Color.parseColor(resources.data.primaryLanguage.color)
                        )
                    } else {
                        binding.repositoryLanguageContent.text =
                            context?.getString(R.string.programming_language_unknown)
                        (binding.repositoryLanguageContent.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
                            Color.BLACK
                        )
                    }
                    binding.repositoryLicenseContent.text = resources.data?.licenseInfo?.name

                    val flags =
                        DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
                    resources.data?.updatedAt?.let {
                        binding.repositoryUpdatedOnContent.text =
                            DateUtils.formatDateTime(requireContext(), it.time, flags)
                    }
                    resources.data?.createdAt?.let {
                        binding.repositoryCreatedOnContent.text =
                            DateUtils.formatDateTime(requireContext(), it.time, flags)
                    }

                    binding.repositoryBranchContent.text = resources.data?.branchCount.toString()
                    binding.repositoryReleasesContent.text =
                        resources.data?.releasesCount.toString()

                    val watchersCount = resources.data?.watchersCount ?: 0
                    binding.repositoryWatchersCountText.text = formatNumberWithSuffix(watchersCount)
                    val stargazersCount = resources.data?.stargazersCount ?: 0
                    binding.repositoryStargazersCountText.text =
                        formatNumberWithSuffix(stargazersCount)
                    val forksCount = resources.data?.forksCount ?: 0
                    binding.repositoryForksCountText.text = formatNumberWithSuffix(forksCount)
                    val issuesCount = resources.data?.issuesCount ?: 0
                    binding.repositoryIssuesCountText.text = formatNumberWithSuffix(issuesCount)

                    binding.repositoryIssuesTextLayout.setOnClickListener {
                        val args = IssuesFragmentArgs(loginArg, nameArg)
                        parentFragment?.findNavController()
                            ?.navigate(R.id.issues_fragment, args.toBundle())
                    }

                    val pullRequestsCount = resources.data?.pullRequestsCount ?: 0
                    binding.repositoryPullRequestsCountText.text =
                        formatNumberWithSuffix(pullRequestsCount)

                    binding.repositoryPullRequestsTextLayout.setOnClickListener {
                        val args = PullRequestsFragmentArgs(loginArg, nameArg)
                        parentFragment?.findNavController()
                            ?.navigate(R.id.prs_fragment, args.toBundle())
                    }

                    val projectsCount = resources.data?.projectsCount ?: 0
                    binding.repositoryProjectsCountText.text = formatNumberWithSuffix(projectsCount)

                    resources.data?.defaultBranchRef?.let {
                        viewModel.updateBranchName(it.name)
                    }

                    resources.data?.topics?.let { topicList ->
                        binding.repositoryTopics.apply {
                            setHasFixedSize(true)
                            layoutManager = LinearLayoutManager(
                                requireContext(),
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            val adapter = RepositoryTopicAdapter()
                            this.adapter = adapter
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

        viewModel.readmeFileName.observe(this, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    if (resources.data != null) {
                        val branchName =
                            viewModel.repositoryResult.value?.data?.defaultBranchRef?.name
                        viewModel.updateExpression("$branchName:${resources.data.second}")
                    }
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

        viewModel.readmeFile.observe(this, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    binding.repositoryReadmeContent.apply {
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
                    binding.repositoryReadmeContent.loadDataWithBaseURL(
                        "file:///android_asset/",
                        html,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

    }

}