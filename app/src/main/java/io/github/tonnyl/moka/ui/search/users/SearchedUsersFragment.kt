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
import io.github.tonnyl.moka.databinding.FragmentSearchPageBinding
import io.github.tonnyl.moka.ui.search.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search_page.*
import kotlinx.android.synthetic.main.layout_empty_content.*
import io.github.tonnyl.moka.ui.search.ViewModelFactory as ParentViewModelFactory

class SearchedUsersFragment : Fragment() {

    private lateinit var binding: FragmentSearchPageBinding

    private val parentViewModel: SearchViewModel by lazy {
        ViewModelProviders.of(requireParentFragment(), ParentViewModelFactory()).get(SearchViewModel::class.java)
    }
    private val searchedUsersViewModel: SearchedUsersViewModel by lazy {
        ViewModelProviders.of(this, ViewModelFactory()).get(SearchedUsersViewModel::class.java)
    }

    private val adapter: SearchedUserAdapter by lazy {
        SearchedUserAdapter()
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

        parentViewModel.input.observe(requireParentFragment(), Observer {
            searchedUsersViewModel.refresh(it)
        })

        searchedUsersViewModel.searchedUsersResult.observe(this, Observer {
            if (it.isEmpty()) {
                empty_content_layout.visibility = View.VISIBLE
            } else {
                empty_content_layout.visibility = View.GONE
            }

            if (recycler_view.adapter == null) {
                recycler_view.setHasFixedSize(false)
                recycler_view.layoutManager = LinearLayoutManager(context
                        ?: return@Observer, RecyclerView.VERTICAL, false)
                recycler_view.adapter = adapter
            }

            adapter.submitList(it)
        })

    }

}