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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentExploreBinding
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.explore.filters.TrendingFilterFragment
import io.github.tonnyl.moka.ui.main.MainViewModel
import io.github.tonnyl.moka.ui.profile.UserProfileFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.layout_empty_content.*
import kotlinx.android.synthetic.main.layout_main_search_bar.*
import io.github.tonnyl.moka.ui.main.ViewModelFactory as MainViewModelFactory

class ExploreFragment : Fragment(), ExploreRepositoryActions, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var repositoryAdapter: ExploreAdapter

    private lateinit var viewModel: ExploreViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: FragmentExploreBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory()).get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(ExploreViewModel::class.java)

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = requireActivity()

        fun dealDataAndUpdateUI() {
            val repositoriesValue = viewModel.trendingRepositories.value
            val developersValue = viewModel.trendingDevelopers.value

            when {
                repositoriesValue?.status == Status.LOADING
                        || developersValue?.status == Status.LOADING -> {
                    swipe_refresh.isRefreshing = true
                }
                repositoriesValue?.status == Status.ERROR
                        || developersValue?.status == Status.ERROR -> {
                    swipe_refresh.isRefreshing = false

                    showHideEmptyView(true)
                }
                repositoriesValue?.status == Status.SUCCESS
                        && developersValue?.status == Status.SUCCESS -> {
                    swipe_refresh.isRefreshing = false

                    if (!repositoriesValue.data.isNullOrEmpty()
                            && !developersValue.data.isNullOrEmpty()) {
                        if (!this::repositoryAdapter.isInitialized) {
                            recycler_view.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                            repositoryAdapter = ExploreAdapter("All Languages", "Daily", repositoriesValue.data, developersValue.data)
                            repositoryAdapter.actions = this@ExploreFragment
                            recycler_view.adapter = repositoryAdapter
                        } else {
                            repositoryAdapter.repositories = repositoriesValue.data
                            repositoryAdapter.developers = developersValue.data

                            repositoryAdapter.notifyDataSetChanged()
                        }

                        showHideEmptyView(false)
                    } else {
                        showHideEmptyView(true)
                    }
                }
            }
        }

        mainViewModel.loginUserProfile.observe(this, Observer { data ->
            if (data != null) {
                main_search_bar_avatar.setOnClickListener(this@ExploreFragment)
            } else {

            }
        })

        viewModel.trendingRepositories.observe(this, Observer {
            dealDataAndUpdateUI()
        })

        viewModel.trendingDevelopers.observe(this, Observer {
            dealDataAndUpdateUI()
        })

        empty_content_title_text.text = getString(R.string.timeline_content_empty_title)
        empty_content_action_text.text = getString(R.string.timeline_content_empty_action)

        fab.setOnClickListener(this@ExploreFragment)
        swipe_refresh.setOnRefreshListener(this@ExploreFragment)
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
        when (v?.id) {
            R.id.fab -> {
                val sheet = TrendingFilterFragment.newInstance()
                sheet.show(childFragmentManager, TrendingFilterFragment::class.java.simpleName)
            }
            R.id.empty_content_action_text -> {

            }
            R.id.empty_content_retry_button -> {

            }
        }
    }

    override fun onRefresh() {
        viewModel.trendingDevelopers.refresh()
        viewModel.trendingRepositories.refresh()
    }

    override fun openProfile(login: String) {
        val builder = UserProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, builder.toBundle())
    }

    override fun openRepository(login: String, repositoryName: String) {
        val builder = RepositoryFragmentArgs(login, repositoryName)
        findNavController().navigate(R.id.action_to_repository, builder.toBundle())
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