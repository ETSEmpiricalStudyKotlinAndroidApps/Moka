package io.github.tonnyl.moka.ui.explore

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
import io.github.tonnyl.moka.ui.explore.filters.TrendingFilterFragment
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.layout_main_search_bar.*

class ExploreFragment : Fragment() {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var repositoryAdapter: ExploreAdapter

    private val viewModel: ExploreViewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory()).get(ExploreViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_explore, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        fun initAdapterIfNeeded() {
            val repositories = viewModel.trendingRepositories.value?.body()
            val developers = viewModel.trendingDevelopers.value?.body()

            if (repositories != null && developers != null && !this::repositoryAdapter.isInitialized) {
                repositoryAdapter = ExploreAdapter("All Languages", "Daily", repositories, developers)
                repositoryAdapter.onItemClick = {
                    findNavController().navigate(R.id.action_to_trending_developers)
                }

                recycler_view.adapter = repositoryAdapter
            }
        }

        viewModel.trendingRepositories.observe(this, Observer { response ->
            initAdapterIfNeeded()
        })

        viewModel.trendingDevelopers.observe(this, Observer {
            initAdapterIfNeeded()
        })

        val sheet = TrendingFilterFragment()
        fab.setOnClickListener {
            sheet.show(childFragmentManager, TrendingFilterFragment::class.java.simpleName)
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

}