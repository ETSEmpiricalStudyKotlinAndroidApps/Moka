package io.github.tonnyl.moka.ui.timeline

import android.os.Bundle
import android.util.ArrayMap
import android.view.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.util.dp2px
import kotlinx.android.synthetic.main.fragment_timeline.*

class TimelineFragment : Fragment(), TimelineAdapter.FetchRepositoryInfoInterface {

    private lateinit var viewModel: EventsViewModel

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipe_refresh.setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.indigo, null),
                ResourcesCompat.getColor(resources, R.color.teal, null),
                ResourcesCompat.getColor(resources, R.color.lightBlue, null),
                ResourcesCompat.getColor(resources, R.color.yellow, null),
                ResourcesCompat.getColor(resources, R.color.orange, null)
        )

        val arrayMap = ArrayMap<Class<out ViewModel>, ViewModel>().apply {
            put(EventsViewModel::class.java, EventsViewModel())
        }
        val factory = ViewModelFactory(arrayMap)
        viewModel = ViewModelProviders.of(this, factory).get(EventsViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && appbar?.elevation == 0f) {
                    ViewCompat.setElevation(appbar, dp2px(4f, resources).toFloat())
                } else if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && appbar != null && appbar?.elevation != 0f) {
                    ViewCompat.setElevation(appbar, 0f)
                }
            }

        })

        viewModel.results.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.isSuccessful && response.body() != null) {
                val timelineAdapter = TimelineAdapter(response.body()!!)
                timelineAdapter.fetchRepositoryInfoInterface = this
                recycler_view.adapter = timelineAdapter
            }
        })
    }

    override fun fetchInfo(position: Int, login: String, repositoryName: String, repositoryCreatorIsOrg: Boolean) {
        if (repositoryCreatorIsOrg) {
            viewModel.orgRepositoryCard(login, repositoryName).observe(viewLifecycleOwner, Observer { orgRepoResp ->
                if (orgRepoResp != null && orgRepoResp.hasErrors().not() && recycler_view.adapter is TimelineAdapter) {
                    (recycler_view.adapter as TimelineAdapter).updateRepoCard(position, orgRepoResp.data()!!)
                }
            })
        } else {
            viewModel.userRepositoryCard(login, repositoryName).observe(viewLifecycleOwner, Observer { userRepoResp ->
                if (userRepoResp != null && userRepoResp.hasErrors().not() && recycler_view.adapter is TimelineAdapter) {
                    (recycler_view.adapter as TimelineAdapter).updateRepoCard(position, userRepoResp.data()!!)
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (::drawer.isInitialized.not()) {
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_timeline, menu)
    }

}