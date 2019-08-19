package io.github.tonnyl.moka.ui.search.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.github.tonnyl.moka.databinding.FragmentSearchedRepositoriesBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.search.SearchViewModel
import io.github.tonnyl.moka.ui.search.ViewModelFactory as ParentViewModelFactory

class SearchedRepositoriesFragment : Fragment(), PagingNetworkStateActions, EmptyViewActions {

    private lateinit var binding: FragmentSearchedRepositoriesBinding

    private lateinit var parentViewModel: SearchViewModel
    private lateinit var viewModel: SearchedRepositoriesViewModel

    private val searchedRepositoryAdapter: SearchedRepositoryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchedRepositoryAdapter(this@SearchedRepositoriesFragment)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentViewModel = ViewModelProviders.of(requireParentFragment(), ParentViewModelFactory())
            .get(SearchViewModel::class.java)
        viewModel = ViewModelProviders.of(this, ViewModelFactory())
            .get(SearchedRepositoriesViewModel::class.java)

        binding.apply {
            emptyViewActions = this@SearchedRepositoriesFragment
            this.viewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        parentViewModel.input.observe(requireParentFragment(), Observer {
            viewModel.refresh(it)
        })

        viewModel.initialLoadStatus.observe(this, Observer {

        })

        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = searchedRepositoryAdapter
                }
            }

            searchedRepositoryAdapter.submitList(it)
        })

        binding.swipeRefresh.setOnRefreshListener {
            triggerRefresh()
        }
    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

    override fun retryInitial() {
        viewModel.refresh()
    }

    override fun doAction() {

    }

    private fun triggerRefresh() {
        viewModel.refresh(parentViewModel.input.value ?: "")
    }

}