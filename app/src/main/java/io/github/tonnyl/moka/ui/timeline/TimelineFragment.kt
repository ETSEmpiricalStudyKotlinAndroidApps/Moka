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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentTimelineBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_timeline.*
import kotlinx.android.synthetic.main.layout_empty_content.*
import kotlinx.android.synthetic.main.layout_main_search_bar.*
import io.github.tonnyl.moka.ui.main.ViewModelFactory as MainViewModelFactory

class TimelineFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var timelineAdapter: TimelineAdapter

    private lateinit var binding: FragmentTimelineBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory()).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(requireActivity(), ViewModelFactory()).get(TimelineViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = requireActivity()

        timelineAdapter = TimelineAdapter(requireContext())

        with(recycler_view) {
            recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            recycler_view.adapter = timelineAdapter
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
                    timelineAdapter.setAfterNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    timelineAdapter.setAfterNetworkState(NetworkState.error(it.after.message))
                }
                Status.LOADING -> {
                    timelineAdapter.setAfterNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }

            when (it.before?.status) {
                Status.SUCCESS -> {
                    timelineAdapter.setBeforeNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    timelineAdapter.setBeforeNetworkState(NetworkState.error(it.before.message))
                }
                Status.LOADING -> {
                    timelineAdapter.setBeforeNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }
        })

        viewModel.eventsResult.observe(this, Observer {
            timelineAdapter.submitList(it)

            val status = viewModel.loadStatusLiveData.value?.initial?.status
            showHideEmptyView(it.isEmpty()
                    && (status == Status.SUCCESS || status == Status.ERROR))
        })

        mainViewModel.login.observe(this, Observer { login ->
            login?.let {
                viewModel.refreshEventsData(it, false)
            }
        })

        mainViewModel.loginUserProfile.observe(this, Observer { data ->
            if (data != null) {
                main_search_bar_avatar.setOnClickListener(this@TimelineFragment)
            } else {

            }
        })

        swipe_refresh.setOnRefreshListener {
            viewModel.refreshEventsData(mainViewModel.login.value
                    ?: return@setOnRefreshListener, true)
        }

        main_search_bar_input_text.setOnClickListener(this@TimelineFragment)

        empty_content_title_text.text = getString(R.string.timeline_content_empty_title)
        empty_content_action_text.text = getString(R.string.timeline_content_empty_action)

        empty_content_action_text.setOnClickListener(this)
        empty_content_retry_button.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.empty_content_action_text -> {
                parentFragment?.findNavController()?.navigate(R.id.nav_explore)
            }
            R.id.empty_content_retry_button -> {
                swipe_refresh.post {
                    swipe_refresh.isRefreshing = true
                }
                viewModel.refreshEventsData(mainViewModel.login.value ?: return, true)
            }
            R.id.main_search_bar_avatar -> {
                val bundle = Bundle().apply {
                    putString("login", mainViewModel.login.value)
                }
                findNavController().navigate(R.id.action_timeline_to_user_profile, bundle)
            }
            R.id.main_search_bar_input_text -> {
                findNavController().navigate(R.id.action_to_search)
            }
        }
    }

    private fun showHideEmptyView(show: Boolean) {
        if (show) {
            empty_content_layout.visibility = View.VISIBLE
            recycler_view.visibility = View.GONE
        } else {
            empty_content_layout.visibility = View.GONE
            recycler_view.visibility = View.VISIBLE
        }
    }

}