package io.github.tonnyl.moka.ui.search.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.tonnyl.moka.databinding.FragmentSearchedUsersBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.ViewModelFactory as ParentViewModelFactory

class SearchedUsersFragment : Fragment(), PagingNetworkStateActions, EmptyViewActions {

    private lateinit var binding: FragmentSearchedUsersBinding

    private lateinit var parentViewModel: SearchViewModel

    private lateinit var searchedUsersViewModel: SearchedUsersViewModel

    private val searchedUserAdapter: SearchedUserAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchedUserAdapter(this@SearchedUsersFragment)
    }

    companion object {

        fun newInstance(): SearchedUsersFragment = SearchedUsersFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchedUsersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentViewModel = ViewModelProviders.of(requireParentFragment(), ParentViewModelFactory())
            .get(SearchViewModel::class.java)
        searchedUsersViewModel = ViewModelProviders.of(this, ViewModelFactory())
            .get(SearchedUsersViewModel::class.java)

        binding.apply {
            this.viewModel = searchedUsersViewModel
            emptyViewActions = this@SearchedUsersFragment
            lifecycleOwner = viewLifecycleOwner
        }

        parentViewModel.input.observe(requireParentFragment(), Observer {
            searchedUsersViewModel.refresh(it)
        })

        searchedUsersViewModel.pagedLoadStatus.observe(this, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    searchedUserAdapter.setNetworkState(Pair(it.direction, NetworkState.LOADED))
                }
                Status.ERROR -> {
                    searchedUserAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.error(it.resource.message))
                    )
                }
                Status.LOADING -> {
                    searchedUserAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.LOADING)
                    )
                }
                null -> {

                }
            }
        })

        searchedUsersViewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = searchedUserAdapter
                }
            }

            searchedUserAdapter.submitList(it)
        })

        binding.swipeRefresh.setOnRefreshListener {
            triggerRefresh()
        }

    }

    override fun retryLoadPreviousNext() {
        searchedUsersViewModel.retryLoadPreviousNext()
    }

    override fun retryInitial() {
        searchedUsersViewModel.refresh()
    }

    override fun doAction() {

    }

    private fun triggerRefresh() {
        searchedUsersViewModel.refresh(parentViewModel.input.value ?: "")
    }

}