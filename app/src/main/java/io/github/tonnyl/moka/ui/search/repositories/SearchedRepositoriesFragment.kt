package io.github.tonnyl.moka.ui.search.repositories

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
import io.github.tonnyl.moka.databinding.FragmentSearchedRepositoriesBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.PagedListAdapterWrapper
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoryItemEvent.*

class SearchedRepositoriesFragment : Fragment(), PagingNetworkStateActions, EmptyViewActions {

    private lateinit var binding: FragmentSearchedRepositoriesBinding

    private val parentViewModel by viewModels<SearchViewModel>(
        ownerProducer = {
            requireParentFragment()
        }
    )
    private val searchedRepositoriesViewModel by viewModels<SearchedRepositoriesViewModel>()

    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            SearchedRepositoryAdapter(viewLifecycleOwner, searchedRepositoriesViewModel),
            LoadStateAdapter(this)
        )
    }

    companion object {

        fun newInstance(): SearchedRepositoriesFragment = SearchedRepositoriesFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchedRepositoriesBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            emptyViewActions = this@SearchedRepositoriesFragment
            viewModel = searchedRepositoriesViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        parentViewModel.input.observe(requireParentFragment().viewLifecycleOwner, Observer {
            searchedRepositoriesViewModel.refresh(it)
        })

        searchedRepositoriesViewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = adapterWrapper.mergeAdapter
                }
            }

            adapterWrapper.pagingAdapter.submitList(it)
        })

        binding.swipeRefresh.setOnRefreshListener {
            triggerRefresh()
        }

        searchedRepositoriesViewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(event.login, ProfileType.NOT_SPECIFIED).toBundle()
                    )
                }
                is ViewRepository -> {
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(
                            event.login,
                            event.repoName,
                            ProfileType.NOT_SPECIFIED
                        ).toBundle()
                    )
                }
                is StarRepository -> {
                    adapterWrapper.pagingAdapter.notifyDataSetChanged()
                }
            }
        })

    }

    override fun retryLoadPreviousNext() {
        searchedRepositoriesViewModel.retryLoadPreviousNext()
    }

    override fun retryInitial() {
        searchedRepositoriesViewModel.refresh()
    }

    override fun doAction() {

    }

    private fun triggerRefresh() {
        searchedRepositoriesViewModel.refresh(parentViewModel.input.value ?: "")
    }

}