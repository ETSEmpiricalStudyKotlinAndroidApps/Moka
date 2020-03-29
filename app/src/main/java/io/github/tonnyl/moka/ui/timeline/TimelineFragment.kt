package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.EventOrg
import io.github.tonnyl.moka.databinding.FragmentTimelineBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.MainNavigationFragment
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.issue.IssueFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.ui.timeline.EventItemEvent.*
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class TimelineFragment : MainNavigationFragment(),
    EmptyViewActions, PagingNetworkStateActions {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<TimelineViewModel> {
        ViewModelFactory(
            MokaDataBase.getInstance(
                requireContext(),
                mainViewModel.currentUser.value?.id ?: 0L
            ).eventDao()
        )
    }

    private val timelineAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EventAdapter(viewLifecycleOwner, viewModel, this@TimelineFragment)
    }

    private lateinit var binding: FragmentTimelineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            emptyViewActions = this@TimelineFragment
            mainViewModel = this@TimelineFragment.mainViewModel
            viewModel = this@TimelineFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.previousNextLoadStatusLiveData.observe(viewLifecycleOwner, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    timelineAdapter.setNetworkState(Pair(it.direction, NetworkState.LOADED))
                }
                Status.ERROR -> {
                    timelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.error(it.resource.message))
                    )
                }
                Status.LOADING -> {
                    timelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.LOADING)
                    )
                }
                null -> {

                }
            }
        })

        viewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    addItemDecoration(
                        ListCategoryDecoration(
                            this,
                            getString(R.string.navigation_menu_timeline)
                        )
                    )

                    adapter = timelineAdapter
                }

                timelineAdapter.submitList(it)
            }
        })

        viewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(event.login, event.type).toBundle()
                    )
                }
                is ViewRepository -> {
                    val loginAndRepoName = event.fullName.split("/")
                    if (loginAndRepoName.size < 2) {
                        return@Observer
                    }
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(
                            loginAndRepoName[0],
                            loginAndRepoName[1],
                            getUserProfileType(event.org, loginAndRepoName[0])
                        ).toBundle()
                    )
                }
                is ViewIssueDetail -> {
                    findNavController().navigate(
                        R.id.issue_fragment,
                        IssueFragmentArgs(event.repoOwner, event.repoName, event.number).toBundle()
                    )
                }
            }
        })

        mainViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            viewModel.refreshData(it.login, false)
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData(
                mainViewModel.currentUser.value?.login
                    ?: return@setOnRefreshListener, true
            )
        }

        with(binding.emptyContent) {
            emptyContentTitleText.text = getString(R.string.timeline_content_empty_title)
            emptyContentActionText.text = getString(R.string.timeline_content_empty_action)
        }

    }

    override fun doAction() {
        parentFragment?.findNavController()?.navigate(R.id.nav_explore)
    }

    override fun retryInitial() {
        viewModel.refreshData(mainViewModel.currentUser.value?.login ?: return, true)
    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

    private fun getUserProfileType(org: EventOrg?, login: String): ProfileType {
        if (org?.login == login) {
            return ProfileType.ORGANIZATION
        }

        return ProfileType.USER
    }

}