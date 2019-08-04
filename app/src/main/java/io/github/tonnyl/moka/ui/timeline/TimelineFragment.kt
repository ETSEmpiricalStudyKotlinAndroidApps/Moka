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
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class TimelineFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var mainViewModel: MainViewModel

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var timelineAdapter: TimelineAdapter

    private lateinit var binding: FragmentTimelineBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
        viewModel = ViewModelProviders.of(
            requireActivity(),
            ViewModelFactory(
                MokaDataBase.getInstance(
                    requireContext(),
                    mainViewModel.userId.value ?: return
                ).eventDao()
            )
        ).get(TimelineViewModel::class.java)

        binding.apply {
            mainViewModel = this@TimelineFragment.mainViewModel
            lifecycleOwner = this@TimelineFragment.viewLifecycleOwner
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = TimelineAdapter(requireContext()).also {
                timelineAdapter = it
            }
        }

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
                }
                null -> {

                }
            }
        })

        viewModel.data.observe(this, Observer {
            timelineAdapter.submitList(it)
            showHideEmptyView(it.isEmpty())
        })

        mainViewModel.login.observe(this, Observer { login ->
            login?.let {
                viewModel.refreshData(it, false)
            }
        })

        mainViewModel.loginUserProfile.observe(this, Observer { data ->
            if (data != null) {
                binding.mainSearchBar.mainSearchBarAvatar.setOnClickListener(this@TimelineFragment)
            } else {

            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData(
                mainViewModel.login.value
                    ?: return@setOnRefreshListener, true
            )
        }

        binding.mainSearchBar.mainSearchBarInputText.setOnClickListener(this@TimelineFragment)

        binding.emptyContent.emptyContentTitleText.text =
            getString(R.string.timeline_content_empty_title)
        binding.emptyContent.emptyContentActionText.text =
            getString(R.string.timeline_content_empty_action)

        binding.emptyContent.emptyContentActionText.setOnClickListener(this)
        binding.emptyContent.emptyContentRetryButton.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.empty_content_action_text -> {
                parentFragment?.findNavController()?.navigate(R.id.nav_explore)
            }
            R.id.empty_content_retry_button -> {
                binding.swipeRefresh.post {
                    binding.swipeRefresh.isRefreshing = true
                }
                viewModel.refreshData(mainViewModel.login.value ?: return, true)
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
            binding.emptyContent.root.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyContent.root.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

}