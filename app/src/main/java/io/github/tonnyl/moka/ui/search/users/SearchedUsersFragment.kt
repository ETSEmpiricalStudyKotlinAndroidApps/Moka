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
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.users.SearchedUserItemEvent.*

class SearchedUsersFragment : Fragment(), EmptyViewActions {

    private lateinit var binding: FragmentSearchedUsersBinding

    private val parentViewModel by viewModels<SearchViewModel>(
        ownerProducer = {
            requireParentFragment()
        }
    )
    private val searchedUsersViewModel by viewModels<SearchedUsersViewModel>()

    private val searchedUserAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = SearchedUserAdapter(viewLifecycleOwner, searchedUsersViewModel)
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
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

        parentViewModel.input.observe(requireParentFragment().viewLifecycleOwner, Observer {
            searchedUsersViewModel.refresh(it)
        })

        searchedUsersViewModel.usersResult.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = searchedUserAdapter
                }
            }

            searchedUserAdapter.submitData(lifecycle, it)
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

    override fun retryInitial() {
        searchedUserAdapter.refresh()
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