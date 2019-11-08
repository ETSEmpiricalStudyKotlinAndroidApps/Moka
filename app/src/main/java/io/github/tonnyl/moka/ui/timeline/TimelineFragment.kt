package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.EventOrg
import io.github.tonnyl.moka.databinding.FragmentTimelineBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.SearchBarActions
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class TimelineFragment : Fragment(), SearchBarActions,
    EmptyViewActions, EventActions, PagingNetworkStateActions {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

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
        TimelineAdapter(this@TimelineFragment).apply {
            eventActions = this@TimelineFragment
        }
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
            searchBarActions = this@TimelineFragment
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

    override fun onResume() {
        super.onResume()
        if (!::drawer.isInitialized) {
            drawer = parentFragment?.parentFragment?.view?.findViewById(R.id.drawer_layout)
                ?: return
        }
        toggle = ActionBarDrawerToggle(
            parentFragment?.activity,
            drawer,
            binding.mainSearchBar.mainSearchBarToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onPause() {
        super.onPause()

        drawer.removeDrawerListener(toggle)
    }

    override fun openAccountDialog() {
        findNavController().navigate(R.id.account_dialog)
    }

    override fun openSearch() {
        findNavController().navigate(R.id.search_fragment)
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

    override fun openProfile(login: String, profileType: ProfileType) {
        val args = ProfileFragmentArgs(login, profileType).toBundle()
        findNavController().navigate(R.id.profile_fragment, args)
    }

    override fun viewRepository(fullName: String, org: EventOrg?) {
        val loginAndRepoName = fullName.split("/")
        if (loginAndRepoName.size < 2) {
            return
        }
        val repositoryArgs = RepositoryFragmentArgs(
            loginAndRepoName[0],
            loginAndRepoName[1],
            getUserProfileType(org, loginAndRepoName[0])
        ).toBundle()
        findNavController().navigate(R.id.repository_fragment, repositoryArgs)
    }

    override fun openEventDetails(event: Event) {
        when (event.type) {
            Event.WATCH_EVENT,
            Event.PUBLIC_EVENT,
            Event.CREATE_EVENT,
            Event.TEAM_ADD_EVENT,
            Event.DELETE_EVENT -> {
                viewRepository(
                    event.repo?.name ?: return,
                    event.org
                )
            }
            Event.COMMIT_COMMENT_EVENT -> {

            }
            Event.FORK_EVENT -> {
                viewRepository(
                    event.payload?.forkee?.fullName ?: return,
                    event.org
                )
            }
            Event.GOLLUM_EVENT -> {

            }
            Event.ISSUE_COMMENT_EVENT -> {

            }
            Event.ISSUES_EVENT -> {

            }
            Event.MEMBER_EVENT -> {
                openProfile(
                    event.payload?.member?.login ?: return,
                    ProfileType.USER
                )
            }
            Event.PULL_REQUEST_EVENT -> {

            }
            Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {

            }
            Event.PULL_REQUEST_REVIEW_EVENT -> {

            }
            Event.REPOSITORY_EVENT -> {
                when (event.payload?.action) {
                    "created",
                    "archived",
                    "publicized",
                    "unarchived" -> {
                        viewRepository(
                            event.repo?.name ?: return,
                            event.org
                        )
                    }
                    "privatized",
                    "deleted" -> {
                        // ignore
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
            Event.PUSH_EVENT -> {

            }
            Event.RELEASE_EVENT -> {

            }
            Event.ORG_BLOCK_EVENT -> {
                openProfile(
                    event.payload?.blockedUser?.login ?: return,
                    ProfileType.USER
                )
            }
            Event.PROJECT_CARD_EVENT -> {

            }
            Event.PROJECT_COLUMN_EVENT -> {

            }
            Event.ORGANIZATION_EVENT -> {
                openProfile(
                    event.payload?.organization?.login ?: return,
                    ProfileType.ORGANIZATION
                )
            }
            Event.PROJECT_EVENT -> {

            }
            Event.DOWNLOAD_EVENT,
            Event.FOLLOW_EVENT,
            Event.GIST_EVENT,
            Event.FORK_APPLY_EVENT -> {
                // Events of these types are no longer delivered, just ignore them.
            }
        }
    }

    private fun getUserProfileType(org: EventOrg?, login: String): ProfileType {
        if (org?.login == login) {
            return ProfileType.ORGANIZATION
        }

        return ProfileType.USER
    }

}