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
import kotlinx.android.synthetic.main.fragment_notifications.*
import kotlinx.android.synthetic.main.layout_empty_content.*
import kotlinx.android.synthetic.main.layout_main_search_bar.*
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

        with(recycler_view) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = notificationAdapter
        }

        viewModel.loadStatusLiveData.observe(this, Observer {
            when (it.initial?.status) {
                Status.SUCCESS -> {
                    swipe_refresh.isRefreshing = false
                }
                Status.LOADING -> {
                    swipe_refresh.isRefreshing = true
                }
                Status.ERROR -> {
                    swipe_refresh.isRefreshing = false

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
                main_search_bar_avatar.setOnClickListener(this@NotificationsFragment)
            } else {

            }
        })

        swipe_refresh.setOnRefreshListener {
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
        toggle = ActionBarDrawerToggle(parentFragment?.activity, drawer, main_search_bar_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

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
            empty_content_layout.visibility = View.VISIBLE
            recycler_view.visibility = View.GONE

            empty_content_title_text.text = getString(R.string.timeline_content_empty_title)
            empty_content_action_text.text = getString(R.string.timeline_content_empty_action)

            empty_content_action_text.setOnClickListener(this)
            empty_content_retry_button.setOnClickListener(this)
        } else {
            empty_content_layout.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
        }
    }

}