package io.github.tonnyl.moka.ui.inbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.databinding.FragmentInboxBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.*
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class InboxFragment : MainNavigationFragment(), SearchBarActions,
    EmptyViewActions, InboxActions,
    PagingNetworkStateActions {

    private val viewModel by viewModels<InboxViewModel> {
        ViewModelFactory(
            MokaDataBase.getInstance(
                requireContext(),
                mainViewModel.currentUser.value?.id ?: 0L
            ).notificationsDao()
        )
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentInboxBinding

    private val notificationAdapter by lazy(LazyThreadSafetyMode.NONE) {
        InboxAdapter(this@InboxFragment).apply {
            inboxActions = this@InboxFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInboxBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            emptyViewActions = this@InboxFragment
            searchBarActions = this@InboxFragment
            viewModel = this@InboxFragment.viewModel
            mainViewModel = this@InboxFragment.mainViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    addItemDecoration(
                        ListCategoryDecoration(
                            this,
                            getString(R.string.navigation_menu_inbox)
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

        mainViewModel.currentUser.observe(this, Observer {
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
        findNavController().navigate(R.id.search_fragment)
    }

    override fun openAccountDialog() {
        mainViewModel.currentUser.value?.login?.let {
            val args = ProfileFragmentArgs(it, ProfileType.USER).toBundle()
            findNavController().navigate(R.id.profile_fragment, args)
        }
    }

    override fun retryInitial() {
        mainViewModel.currentUser.value?.login?.let {
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