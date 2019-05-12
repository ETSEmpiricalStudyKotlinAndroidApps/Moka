package io.github.tonnyl.moka.ui.search.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentSearchPageBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.ViewModelFactory as ParentViewModelFactory

class SearchedUsersFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentSearchPageBinding

    private lateinit var parentViewModel: SearchViewModel

    private lateinit var searchedUsersViewModel: SearchedUsersViewModel

    private val adapter: SearchedUserAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchedUserAdapter({}, {})
    }

    companion object {

        fun newInstance(): SearchedUsersFragment = SearchedUsersFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchPageBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentViewModel = ViewModelProviders.of(requireParentFragment(), ParentViewModelFactory()).get(SearchViewModel::class.java)
        searchedUsersViewModel = ViewModelProviders.of(this, ViewModelFactory()).get(SearchedUsersViewModel::class.java)

        parentViewModel.input.observe(requireParentFragment(), Observer {
            searchedUsersViewModel.refresh(it)
        })

        searchedUsersViewModel.loadStatusLiveData.observe(this, Observer {
            if (it.initial == null
                    && it.before == null
                    && it.after == null) {
                binding.emptyContent.root.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }

            when (it.initial?.status) {
                Status.SUCCESS, Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                null -> {

                }
            }

            when (it.after?.status) {
                Status.SUCCESS -> {
                    adapter.setAfterNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    adapter.setAfterNetworkState(NetworkState.error(it.after.message))
                }
                Status.LOADING -> {
                    adapter.setAfterNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }

            when (it.before?.status) {
                Status.SUCCESS -> {
                    adapter.setBeforeNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    adapter.setBeforeNetworkState(NetworkState.error(it.before.message))
                }
                Status.LOADING -> {
                    adapter.setBeforeNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }
        })

        searchedUsersViewModel.searchedUsersResult.observe(this, Observer {
            showHideEmptyView(it.isEmpty()
                    && searchedUsersViewModel.loadStatusLiveData.value?.initial?.status == Status.SUCCESS)

            if (binding.recyclerView.adapter == null) {
                binding.recyclerView.setHasFixedSize(false)
                binding.recyclerView.layoutManager = LinearLayoutManager(context
                        ?: return@Observer, RecyclerView.VERTICAL, false)
                binding.recyclerView.adapter = adapter
            }

            adapter.submitList(it)
        })

        binding.swipeRefresh.setOnRefreshListener {
            triggerRefresh()
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.empty_content_retry_button -> {
                triggerRefresh()
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

    private fun triggerRefresh() {
        searchedUsersViewModel.refresh(parentViewModel.input.value ?: "")
    }

}