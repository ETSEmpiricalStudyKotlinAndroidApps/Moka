package io.github.tonnyl.moka.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.databinding.FragmentSearchBinding
import io.github.tonnyl.moka.util.dismissKeyboard
import io.github.tonnyl.moka.util.showKeyboard

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val viewModel by viewModels<SearchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            toolbarWithSearchView.toolbar.setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }

            toolbarWithSearchView.searchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        this@apply.dismissKeyboard()

                        val input = query?.trim()
                        if (!input.isNullOrEmpty()) {
                            viewModel.input.value = input.toString()
                        }

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        return false
                    }

                })

                setOnQueryTextFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        view.findFocus().showKeyboard()
                    }
                }

                requestFocus()
            }
        }

        val pagerAdapter = SearchPagerAdapter(requireContext(), childFragmentManager)
        binding.viewPager.adapter = pagerAdapter

        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onPause() {
        binding.toolbarWithSearchView.searchView.dismissKeyboard()
        super.onPause()
    }

}