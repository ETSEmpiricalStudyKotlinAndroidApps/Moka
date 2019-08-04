package io.github.tonnyl.moka.ui.explore.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.FragmentExplorePageBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.explore.ExploreViewModel
import io.github.tonnyl.moka.ui.explore.ViewModelFactory
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class TrendingRepositoriesFragment : Fragment() {

    private lateinit var repositoriesAdapter: TrendingRepositoryAdapter

    private lateinit var viewModel: ExploreViewModel
    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
    }

    private lateinit var binding: FragmentExplorePageBinding

    companion object {

        fun newInstance(): TrendingRepositoriesFragment = TrendingRepositoriesFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExplorePageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MokaDataBase.getInstance(requireContext(), mainViewModel.userId.value ?: return).let {
            viewModel = ViewModelProviders.of(
                requireParentFragment(),
                ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
            ).get(ExploreViewModel::class.java)
        }

        with(binding.recyclerView) {
            repositoriesAdapter = TrendingRepositoryAdapter("All Languages", "Daily")
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = repositoriesAdapter
        }

        viewModel.repositoriesRemoteStatus.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false

                    showHideEmptyView(it.data.isNullOrEmpty())
                }
                Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false

                    showHideEmptyView(viewModel.repositoriesLocalData.value.isNullOrEmpty())
                }
                Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
            }
        })

        viewModel.repositoriesLocalData.observe(this, Observer {
            repositoriesAdapter.submitList(it)
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshTrendingRepositories()
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