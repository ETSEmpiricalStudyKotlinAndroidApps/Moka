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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentNotificationsBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.main.MainViewModel
import io.github.tonnyl.moka.ui.main.ViewModelFactory as MainViewModelFactory

class NotificationsFragment : Fragment(), View.OnClickListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewModel: NotificationsViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: FragmentNotificationsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory()).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(NotificationsViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = requireActivity()

        val notificationAdapter = NotificationAdapter({

        }, {

        })

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = notificationAdapter
        }

        binding.emptyContent.emptyContentTitleText.text = getString(R.string.timeline_content_empty_title)
        binding.emptyContent.emptyContentActionText.text = getString(R.string.timeline_content_empty_action)

        binding.emptyContent.emptyContentActionText.setOnClickListener(this)
        binding.emptyContent.emptyContentRetryButton.setOnClickListener(this)

        viewModel.loadStatusLiveData.observe(this, Observer {
            when (it.initial?.status) {
                Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false

                    showHideEmptyView(true)
                }
                null -> {

                }
            }

            when (it.after?.status) {
                Status.SUCCESS -> {
                    notificationAdapter.setAfterNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    notificationAdapter.setAfterNetworkState(NetworkState.error(it.after.message))
                }
                Status.LOADING -> {
                    notificationAdapter.setAfterNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }

            when (it.before?.status) {
                Status.SUCCESS -> {
                    notificationAdapter.setBeforeNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    notificationAdapter.setBeforeNetworkState(NetworkState.error(it.before.message))
                }
                Status.LOADING -> {
                    notificationAdapter.setBeforeNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }
        })

        viewModel.notificationsResult.observe(this, Observer {
            notificationAdapter.submitList(it)

            val status = viewModel.loadStatusLiveData.value?.initial?.status

            showHideEmptyView(it.isEmpty()
                    && (status == Status.SUCCESS || status == Status.ERROR))
        })

        mainViewModel.login.observe(this, Observer { login ->
            login?.let {
                viewModel.refreshNotificationsData(it, false)
            }
        })

        mainViewModel.loginUserProfile.observe(this, Observer { data ->
            if (data != null) {
                binding.mainSearchBar.mainSearchBarAvatar.setOnClickListener(this@NotificationsFragment)
            } else {

            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshNotificationsData(mainViewModel.login.value
                    ?: return@setOnRefreshListener, true)
        }

    }

    override fun onResume() {
        super.onResume()
        if (!::drawer.isInitialized) {
            drawer = parentFragment?.parentFragment?.view?.findViewById(R.id.drawer_layout)
                    ?: return
        }
        toggle = ActionBarDrawerToggle(parentFragment?.activity, drawer, binding.mainSearchBar.mainSearchBarToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onPause() {
        super.onPause()

        drawer.removeDrawerListener(toggle)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        // trick: use the position as item view's order.
        val notification = viewModel.notificationsResult.value?.get(item.order)

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

    override fun onClick(v: View?) {

    }

    private fun showHideEmptyView(show: Boolean) {
        if (show) {
            binding.emptyContent.root.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyContent.root.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

}