package io.github.tonnyl.moka.ui.search.repositories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentSearchedRepositoriesBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoryItemEvent.*

class SearchedRepositoriesFragment : Fragment(), EmptyViewActions {

    private lateinit var binding: FragmentSearchedRepositoriesBinding

    private val parentViewModel by viewModels<SearchViewModel>(
        ownerProducer = {
            requireParentFragment()
        }
    )
    private val searchedRepositoriesViewModel by viewModels<SearchedRepositoriesViewModel>()

    private val searchedRepositoryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = SearchedRepositoryAdapter(
            viewLifecycleOwner,
            searchedRepositoriesViewModel
        )
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
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

        with(binding) {
            emptyViewActions = this@SearchedRepositoriesFragment
            viewModel = searchedRepositoriesViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        parentViewModel.input.observe(requireParentFragment().viewLifecycleOwner) {
            searchedRepositoriesViewModel.refresh(it)
        }

        binding.swipeRefresh.setOnRefreshListener {
            searchedRepositoryAdapter.refresh()
        }

        searchedRepositoriesViewModel.event.observe(viewLifecycleOwner) {
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
                    searchedRepositoryAdapter.notifyDataSetChanged()
                }
            }
        }

        searchedRepositoriesViewModel.repositoryResult.observe(viewLifecycleOwner) {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = searchedRepositoryAdapter
                }
            }

            searchedRepositoryAdapter.submitData(lifecycle, it)
        }

    }

    override fun retryInitial() {
        searchedRepositoryAdapter.refresh()
    }

    override fun doAction() {

    }

    companion object {

        fun newInstance(): SearchedRepositoriesFragment = SearchedRepositoriesFragment()

    }

}