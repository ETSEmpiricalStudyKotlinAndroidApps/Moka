package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import io.github.tonnyl.moka.R
import kotlinx.android.synthetic.main.fragment_timeline.*
import kotlinx.android.synthetic.main.layout_empty_content.*

class TimelineFragment : BaseMvRxFragment(), View.OnClickListener {

    private val viewModel: TimelineViewModel by fragmentViewModel()

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var timelineAdapter: TimelineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_timeline, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh.setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.indigo, null),
                ResourcesCompat.getColor(resources, R.color.teal, null),
                ResourcesCompat.getColor(resources, R.color.lightBlue, null),
                ResourcesCompat.getColor(resources, R.color.yellow, null),
                ResourcesCompat.getColor(resources, R.color.orange, null)
        )

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

        swipe_refresh.setOnRefreshListener {
            viewModel.refreshEventsData()
        }

        toolbar_search.setOnClickListener(this)

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && appbar?.elevation == 0f) {
                    ViewCompat.setElevation(appbar, resources.getDimension(R.dimen.toolbar_elevation))
                } else if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && appbar != null && appbar?.elevation != 0f) {
                    ViewCompat.setElevation(appbar, 0f)
                }
            }

        })

    }

    override fun onResume() {
        super.onResume()
        if (!::drawer.isInitialized) {
            drawer = parentFragment?.parentFragment?.view?.findViewById(R.id.drawer_layout)
                    ?: return
        }
        toggle = ActionBarDrawerToggle(parentFragment?.activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

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
                viewModel.refreshEventsData()
            }
            R.id.toolbar_search -> {
                parentFragment?.findNavController()?.navigate(R.id.action_to_search)
            }
        }
    }

}