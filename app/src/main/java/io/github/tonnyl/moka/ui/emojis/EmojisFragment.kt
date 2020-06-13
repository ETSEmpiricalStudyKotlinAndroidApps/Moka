package io.github.tonnyl.moka.ui.emojis

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.data.EmojiCategory
import io.github.tonnyl.moka.databinding.FragmentEmojisBinding
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.emojis.EmojiEvent.EmojiSelected
import io.github.tonnyl.moka.ui.emojis.EmojiEvent.ScrollToPosition
import io.github.tonnyl.moka.ui.emojis.search.SearchEmojiFragment

class EmojisFragment : Fragment() {

    private val emojiAdapter by lazy(LazyThreadSafetyMode.NONE) {
        EmojiAdapter(viewLifecycleOwner, emojisViewModel)
    }

    private val emojisViewModel by viewModels<EmojisViewModel>()
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentEmojisBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEmojisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handle = findNavController().currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<String>(SearchEmojiFragment.RESULT_SEARCH_EMOJI)
            ?.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(RESULT_EMOJI, it)

                    findNavController().navigateUp()
                }
            })

        with(binding) {
            lifecycleOwner = viewLifecycleOwner

            with(appbarLayout.toolbar) {
                inflateMenu(R.menu.fragment_emojis_menu)
                setOnMenuItemClickListener { item ->
                    if (item.itemId == R.id.action_search_emoji) {
                        findNavController().navigate(R.id.search_emoji_fragment)

                        return@setOnMenuItemClickListener true
                    }

                    false
                }
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }

            viewModel = emojisViewModel

            var currentCheckedCategory = EmojiCategory.RecentlyUsed.categoryValue
            emojiRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
                val lastPosition = (emojiRecyclerView.layoutManager as GridLayoutManager)
                    .findLastVisibleItemPosition()

                if ((emojiAdapter.currentList[lastPosition] as? Emoji)?.category == EmojiCategory.GitHubCustomEmoji.categoryValue) {
                    emojisCategoryGithubCustomEmoji.isChecked = true

                    return@setOnScrollChangeListener
                }

                val firstPosition = (emojiRecyclerView.layoutManager as GridLayoutManager)
                    .findFirstVisibleItemPosition()

                val emojiData = emojiAdapter.currentList[firstPosition]
                    ?: return@setOnScrollChangeListener

                if (emojiData is Emoji) {
                    if (emojiData.category == currentCheckedCategory) {
                        return@setOnScrollChangeListener
                    }

                    currentCheckedCategory = emojiData.category
                } else if (emojiData is EmojiCategory) {
                    if (emojiData.categoryValue == currentCheckedCategory) {
                        return@setOnScrollChangeListener
                    }

                    currentCheckedCategory = emojiData.categoryValue
                }

                /**
                 * The reason why we use `MaterialButton.isChecked = true` rather than
                 * `MaterialButtonToggleGroup.check(id)`:
                 *
                 * the checked MaterialButton gets a wrong border on UI.
                 */
                when (currentCheckedCategory) {
                    EmojiCategory.RecentlyUsed.categoryValue -> {
                        emojisCategoryRecentlyUsed.isChecked = true
                    }
                    EmojiCategory.SmileysAndEmotion.categoryValue -> {
                        emojisCategorySmileysAndEmotion.isChecked = true
                    }
                    EmojiCategory.PeopleAndBody.categoryValue -> {
                        emojisCategoryPeopleAndBody.isChecked = true
                    }
                    EmojiCategory.AnimalsAndNature.categoryValue -> {
                        emojisCategoryAnimalsAndNature.isChecked = true
                    }
                    EmojiCategory.FoodAndDrink.categoryValue -> {
                        emojisCategoryFoodAndDrink.isChecked = true
                    }
                    EmojiCategory.TravelAndPlaces.categoryValue -> {
                        emojisCategoryTravelAndPlaces.isChecked = true
                    }
                    EmojiCategory.Activities.categoryValue -> {
                        emojisCategoryActivities.isChecked = true
                    }
                    EmojiCategory.Objects.categoryValue -> {
                        emojisCategoryObjects.isChecked = true
                    }
                    EmojiCategory.Symbols.categoryValue -> {
                        emojisCategorySymbols.isChecked = true
                    }
                    EmojiCategory.Flags.categoryValue -> {
                        emojisCategoryFlags.isChecked = true
                    }
                    EmojiCategory.GitHubCustomEmoji.categoryValue -> {
                        emojisCategoryGithubCustomEmoji.isChecked = true
                    }
                }
            }
        }

        mainViewModel.emojis.observe(viewLifecycleOwner, Observer {
            with(binding.emojiRecyclerView) {
                if (adapter == null) {
                    adapter = emojiAdapter

                    val spanCount =
                        resources.displayMetrics.widthPixels / resources.getDimensionPixelSize(R.dimen.emoji_width)
                    layoutManager = GridLayoutManager(requireContext(), spanCount).apply {
                        spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

                            override fun getSpanSize(position: Int): Int {
                                return when (emojiAdapter.getItemViewType(position)) {
                                    EmojiAdapter.VIEW_TYPE_EMOJI -> {
                                        1
                                    }
                                    EmojiAdapter.VIEW_TYPE_EMOJI_CATEGORY -> {
                                        spanCount
                                    }
                                    else -> {
                                        throw IllegalArgumentException("Unknown item type")
                                    }
                                }
                            }

                        }
                    }
                }
            }

            emojiAdapter.submitList(it)
        })

        emojisViewModel.event.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                is ScrollToPosition -> {
                    val position = mainViewModel.emojis.value?.indexOfFirst {
                        it is EmojiCategory
                                && it == event.category
                    }

                    if (position != null
                        && position >= 0
                    ) {
                        val layoutManager =
                            binding.emojiRecyclerView.layoutManager as? GridLayoutManager
                        layoutManager?.scrollToPositionWithOffset(position, 0)
                    }
                }
                is EmojiSelected -> {
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(RESULT_EMOJI, event.emojiName)

                    findNavController().navigateUp()
                }
            }
        })
    }

    companion object {

        const val RESULT_EMOJI = "result_emoji"

    }

}