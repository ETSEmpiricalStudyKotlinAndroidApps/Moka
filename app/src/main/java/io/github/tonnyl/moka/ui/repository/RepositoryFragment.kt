package io.github.tonnyl.moka.ui.repository

import android.content.res.Configuration
import android.os.Bundle
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
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.databinding.FragmentRepositoryBinding
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.issues.IssuesFragmentArgs
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs

class RepositoryFragment : Fragment() {

    private val args by navArgs<RepositoryFragmentArgs>()

    private val viewModel by viewModels<RepositoryViewModel> {
        ViewModelFactory(args.login, args.name, args.profileType)
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

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.apply {
            viewModel = this@RepositoryFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        binding.repositoryBottomAppBar.inflateMenu(R.menu.fragment_repository_menu)

        val repoObserver: Observer<Resource<Repository>> = Observer { repository ->
            repository.data?.topics?.let { topicList ->
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

        viewModel.userRepository.observe(viewLifecycleOwner, repoObserver)
        viewModel.organizationsRepository.observe(viewLifecycleOwner, repoObserver)

        viewModel.readmeFile.observe(viewLifecycleOwner, Observer { resources ->
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

                    val html =
                        when (getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                            Configuration.UI_MODE_NIGHT_YES -> {
                                (resources.data ?: return@Observer).replaceFirst(
                                    "github_light.css",
                                    "github_dark.css"
                                )
                            }
                            Configuration.UI_MODE_NIGHT_NO -> {
                                resources.data ?: return@Observer
                            }
                            else -> {
                                return@Observer
                            }
                        }

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

        viewModel.userEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                RepositoryEvent.VIEW_OWNERS_PROFILE -> {

                }
                RepositoryEvent.VIEW_WATCHERS -> {

                }
                RepositoryEvent.VIEW_STARGAZERS -> {

                }
                RepositoryEvent.VIEW_FORKS -> {

                }
                RepositoryEvent.VIEW_ISSUES -> {
                    val args = IssuesFragmentArgs(args.login, args.name)
                    parentFragment?.findNavController()
                        ?.navigate(R.id.issues_fragment, args.toBundle())
                }
                RepositoryEvent.VIEW_PULL_REQUESTS -> {
                    val args = PullRequestsFragmentArgs(args.login, args.name)
                    parentFragment?.findNavController()
                        ?.navigate(R.id.prs_fragment, args.toBundle())
                }
                RepositoryEvent.VIEW_PROJECTS -> {

                }
                RepositoryEvent.VIEW_LICENSE -> {

                }
            }
        })

    }

}