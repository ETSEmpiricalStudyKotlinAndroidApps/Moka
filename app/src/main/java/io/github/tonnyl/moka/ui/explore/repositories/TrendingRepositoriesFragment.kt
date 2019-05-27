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
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.explore.ExploreViewModel
import io.github.tonnyl.moka.ui.explore.ViewModelFactory

class TrendingRepositoriesFragment : Fragment() {

    private lateinit var repositoriesAdapter: TrendingRepositoryAdapter
    private lateinit var viewModel: ExploreViewModel

    private lateinit var binding: FragmentExplorePageBinding

    companion object {

        fun newInstance(): TrendingRepositoriesFragment = TrendingRepositoriesFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentExplorePageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(requireParentFragment(), ViewModelFactory()).get(ExploreViewModel::class.java)

        with(binding.recyclerView) {
            repositoriesAdapter = TrendingRepositoryAdapter("All Languages", "Daily")
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = repositoriesAdapter
        }

        viewModel.trendingRepositories.observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false

                    showHideEmptyView(it.data.isNullOrEmpty())

                    repositoriesAdapter.submitList(it.data)
                }
                Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false

                    showHideEmptyView(true)
                }
                Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
            }
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.trendingRepositories.refresh()
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