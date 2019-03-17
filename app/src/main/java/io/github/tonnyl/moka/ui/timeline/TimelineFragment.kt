package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentTimelineBinding
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.ui.main.MainViewModel
import io.github.tonnyl.moka.ui.main.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_timeline.*
import kotlinx.android.synthetic.main.layout_empty_content.*
import kotlinx.android.synthetic.main.layout_main_search_bar.*

class TimelineFragment : BaseMvRxFragment(), View.OnClickListener {

    private val viewModel: TimelineViewModel by fragmentViewModel()
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

        mainViewModel = ViewModelProviders.of(requireActivity(), ViewModelFactory()).get(MainViewModel::class.java)

        timelineAdapter = TimelineAdapter(requireContext())
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = timelineAdapter

        viewModel.asyncSubscribe(TimelineState::eventRequest, onFail = {

        }, onSuccess = {
            timelineAdapter.submitList(it)

            if (timelineAdapter.itemCount == 0) {
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
        })

        viewModel.selectSubscribe(TimelineState::isInitialLoading) { isInitialLoading ->
            swipe_refresh.post {
                swipe_refresh.isRefreshing = isInitialLoading
            }
        }

        mainViewModel.login.observe(this, Observer { login ->
            login?.let {
                viewModel.refreshEventsData(it)
            }
        })

        mainViewModel.loginUserProfile.observe(this, Observer { data ->
            if (data != null) {
                GlideLoader.loadAvatar(data.viewer().avatarUrl().toString(), main_search_bar_avatgar)

                main_search_bar_avatgar.setOnClickListener(this@TimelineFragment)
            } else {

            }
        })

        swipe_refresh.setOnRefreshListener {
            viewModel.refreshEventsData(mainViewModel.login.value ?: return@setOnRefreshListener)
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

    override fun invalidate() {

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
                viewModel.refreshEventsData(mainViewModel.login.value ?: return)
            }
            R.id.main_search_bar_avatgar -> {
                val bundle = Bundle().apply {
                    putString("login", mainViewModel.login.value)
                }
                findNavController().navigate(R.id.action_timeline_to_user_profile, bundle)
            }
        }
    }

}