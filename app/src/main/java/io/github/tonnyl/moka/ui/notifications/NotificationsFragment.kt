package io.github.tonnyl.moka.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.FragmentNotificationsBinding
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

class NotificationsFragment : Fragment(), SearchBarActions,
    EmptyViewActions, NotificationActions,
    PagingNetworkStateActions {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: FragmentNotificationsBinding

    private val notificationAdapter by lazy(LazyThreadSafetyMode.NONE) {
        NotificationAdapter(this@NotificationsFragment).apply {
            notificationActions = this@NotificationsFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                MokaDataBase.getInstance(
                    requireContext(),
                    mainViewModel.userId.value ?: return
                ).notificationsDao()
            )
        ).get(NotificationsViewModel::class.java)

        binding.apply {
            emptyViewActions = this@NotificationsFragment
            searchBarActions = this@NotificationsFragment
            viewModel = this@NotificationsFragment.viewModel
            mainViewModel = this@NotificationsFragment.mainViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    addItemDecoration(
                        ListCategoryDecoration(
                            this,
                            getString(R.string.navigation_menu_notifications)
                        )
                    )

                    adapter = notificationAdapter
                }

                notificationAdapter.submitList(it)
            }
        })

        viewModel.previousNextLoadStatusLiveData.observe(this, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    notificationAdapter.setNetworkState(
                        Pair(
                            it.direction,
                            NetworkState.LOADED
                        )
                    )
                }
                Status.ERROR -> {
                    notificationAdapter.setNetworkState(
                        Pair(
                            it.direction,
                            NetworkState.error(it.resource.message)
                        )
                    )
                }
                Status.LOADING -> {
                    notificationAdapter.setNetworkState(
                        Pair(
                            it.direction,
                            NetworkState.LOADING
                        )
                    )
                }
                null -> {

                }
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

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // trick: use the position as item view's order.
        val notification = viewModel.data.value?.get(item.order)

        when (item.itemId) {
            R.id.notification_menu_mark_as_read -> {
                // todo
            }
            R.id.notification_menu_unsubscribe -> {
                // todo
            }
        }

        return true
    }

    override fun openSearch() {
        findNavController().navigate(R.id.action_to_search)
    }

    override fun openAccountDialog() {
        mainViewModel.login.value?.let {
            val args = ProfileFragmentArgs(it, ProfileType.USER).toBundle()
            findNavController().navigate(R.id.action_timeline_to_user_profile, args)
        }
    }

    override fun retryInitial() {
        mainViewModel.login.value?.let {
            viewModel.refreshData(it, true)
        }
    }

    override fun doAction() {

    }

    override fun openNotification(notification: Notification) {

    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

}