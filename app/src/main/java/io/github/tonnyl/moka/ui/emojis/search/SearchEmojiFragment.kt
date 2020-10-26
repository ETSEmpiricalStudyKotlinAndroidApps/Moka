package io.github.tonnyl.moka.ui.emojis.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.FragmentSearchEmojiBinding
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.util.dismissKeyboard
import io.github.tonnyl.moka.util.showKeyboard

class SearchEmojiFragment : Fragment() {

    private lateinit var binding: FragmentSearchEmojiBinding

    private val searchableEmojiAdapter by lazy(LazyThreadSafetyMode.NONE) {
        SearchableEmojiAdapter(viewLifecycleOwner, searchEmojiViewModel)
    }

    private val linearLayoutManager by lazy(LazyThreadSafetyMode.NONE) {
        LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val searchEmojiViewModel by viewModels<SearchEmojiViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchEmojiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            lifecycleOwner = viewLifecycleOwner

            toolbarWithSearchView.toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            toolbarWithSearchView.searchView.apply {
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        this@apply.dismissKeyboard()

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        mainViewModel.filterSearchable(newText)

                        return true
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

        mainViewModel.searchableEmojis.observe(viewLifecycleOwner) {
            with(binding.searchEmojiRecyclerView) {
                if (adapter == null) {
                    adapter = searchableEmojiAdapter
                    layoutManager = linearLayoutManager

                    searchableEmojiAdapter.registerAdapterDataObserver(

                        object : RecyclerView.AdapterDataObserver() {

                            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                                linearLayoutManager.scrollToPositionWithOffset(positionStart, 0)
                            }

                        }
                    )
                }
            }

            searchableEmojiAdapter.submitList(it)
        }

        searchEmojiViewModel.event.observe(viewLifecycleOwner, EventObserver { event ->
            findNavController().previousBackStackEntry
                ?.savedStateHandle
                ?.set(RESULT_SEARCH_EMOJI, event.emojiName)

            findNavController().navigateUp()
        })
    }

    override fun onPause() {
        binding.toolbarWithSearchView.searchView.dismissKeyboard()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mainViewModel.filterSearchable(null)
    }

    companion object {

        const val RESULT_SEARCH_EMOJI = "result_search_emoji"

    }

}