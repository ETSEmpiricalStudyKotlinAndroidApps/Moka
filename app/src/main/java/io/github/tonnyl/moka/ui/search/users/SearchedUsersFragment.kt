package io.github.tonnyl.moka.ui.search.users

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentSearchedUsersBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.users.SearchedUserItemEvent.*

class SearchedUsersFragment : Fragment(), PagingNetworkStateActions, EmptyViewActions {

    private lateinit var binding: FragmentSearchedUsersBinding

    private val parentViewModel by viewModels<SearchViewModel>(
        ownerProducer = {
            requireParentFragment()
        }
    )
    private val searchedUsersViewModel by viewModels<SearchedUsersViewModel>()

    private val searchedUserAdapter: SearchedUserAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchedUserAdapter(viewLifecycleOwner, searchedUsersViewModel, this@SearchedUsersFragment)
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

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            this.viewModel = searchedUsersViewModel
            emptyViewActions = this@SearchedUsersFragment
            lifecycleOwner = viewLifecycleOwner
        }

        parentViewModel.input.observe(requireParentFragment(), Observer {
            searchedUsersViewModel.refresh(it)
        })

        searchedUsersViewModel.pagedLoadStatus.observe(viewLifecycleOwner, Observer {
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

        searchedUsersViewModel.data.observe(viewLifecycleOwner, Observer {
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

        searchedUsersViewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is ViewUserProfile -> {
                    gotoProfile(event.login, ProfileType.USER)
                }
                is ViewOrganizationProfile -> {
                    gotoProfile(event.login, ProfileType.ORGANIZATION)
                }
                is FollowUserEvent -> {
                    searchedUserAdapter.notifyDataSetChanged()
                }
            }
        })

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

    private fun gotoProfile(
        login: String,
        type: ProfileType
    ) {
        findNavController().navigate(
            R.id.profile_fragment,
            ProfileFragmentArgs(login, type).toBundle()
        )
    }

}