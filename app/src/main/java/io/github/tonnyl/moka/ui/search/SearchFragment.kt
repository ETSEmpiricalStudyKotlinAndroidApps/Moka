package io.github.tonnyl.moka.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.databinding.FragmentSearchBinding
import io.github.tonnyl.moka.util.hideKeyboard
import io.github.tonnyl.moka.util.showKeyboard

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this@SearchFragment, ViewModelFactory()).get(SearchViewModel::class.java)

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.toolbarInput.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(v)

                val input = v.text?.trim()
                if (!input.isNullOrEmpty()) {
                    viewModel.input.value = input.toString()
                }
            }

            false
        }

        val pagerAdapter = SearchPagerAdapter(requireContext(), childFragmentManager)
        binding.viewPager.adapter = pagerAdapter

        binding.tabLayout.setupWithViewPager(binding.viewPager)

        showKeyboard(binding.toolbarInput)
    }

    override fun onDestroyView() {
        hideKeyboard(binding.toolbarInput)

        super.onDestroyView()
    }

}