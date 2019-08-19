package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Event
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
import io.github.tonnyl.moka.widget.ListCategoryDecoration
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class TimelineFragment : Fragment(), SearchBarActions,
    EmptyViewActions, EventActions, PagingNetworkStateActions {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewModel: TimelineViewModel
    private lateinit var mainViewModel: MainViewModel

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

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(
                MokaDataBase.getInstance(
                    requireContext(),
                    mainViewModel.userId.value ?: return
                ).eventDao()
            )
        ).get(TimelineViewModel::class.java)

        binding.apply {
            emptyViewActions = this@TimelineFragment
            searchBarActions = this@TimelineFragment
            mainViewModel = this@TimelineFragment.mainViewModel
            viewModel = this@TimelineFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.previousNextLoadStatusLiveData.observe(this, Observer {
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

        viewModel.data.observe(this, Observer {
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

        mainViewModel.login.observe(this, Observer { login ->
            login?.let {
                viewModel.refreshData(it, false)
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData(
                mainViewModel.login.value
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
        mainViewModel.login.value?.let {
            val args = ProfileFragmentArgs(it, ProfileType.USER).toBundle()
            findNavController().navigate(R.id.action_timeline_to_user_profile, args)

        }
    }

    override fun openSearch() {
        findNavController().navigate(R.id.action_to_search)
    }

    override fun doAction() {
        parentFragment?.findNavController()?.navigate(R.id.nav_explore)
    }

    override fun retryInitial() {
        viewModel.refreshData(mainViewModel.login.value ?: return, true)
    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

    override fun openProfile(login: String, profileType: ProfileType) {
        val args = ProfileFragmentArgs(login, profileType).toBundle()
        findNavController().navigate(R.id.action_timeline_to_user_profile, args)
    }

    override fun openEventDetails(event: Event) {

    }

}