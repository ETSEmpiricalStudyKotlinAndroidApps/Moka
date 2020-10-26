package io.github.tonnyl.moka.ui.issue

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.extension.updateByReactionEventIfNeeded
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.databinding.FragmentIssueBinding
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.UserEvent.React
import io.github.tonnyl.moka.ui.reaction.ReactionChangePayload

class IssueFragment : Fragment(), EmptyViewActions {

    private val reactionsViewPool by lazy(LazyThreadSafetyMode.NONE) {
        RecyclerView.RecycledViewPool()
    }
    private val issueTimelineAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = IssueTimelineAdapter(
            viewLifecycleOwner,
            mainViewModel,
            reactionsViewPool
        )
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
    }

    private val concatAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ConcatAdapter(
            IssueDetailsAdapter(
                viewLifecycleOwner,
                issueViewModel,
                mainViewModel,
                reactionsViewPool
            ),
            issueTimelineAdapter
        )
    }

    private val issueViewModel by viewModels<IssueViewModel> {
        ViewModelFactory(args)
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val args: IssueFragmentArgs by navArgs()

    private lateinit var binding: FragmentIssueBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIssueBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(appbarLayout.toolbar) {
                title = getString(R.string.issue)
                setNavigationOnClickListener {
                    parentFragment?.findNavController()?.navigateUp()
                }
            }

            emptyViewActions = this@IssueFragment
            viewModel = issueViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        issueViewModel.issueTimelineResult.observe(viewLifecycleOwner) {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = concatAdapter
                }
            }

            issueTimelineAdapter.submitData(lifecycle, it)
        }

        mainViewModel.fragmentScopedEvent.observe(viewLifecycleOwner, EventObserver { event ->
            if (event is React
                && event.resource.status == Status.SUCCESS
            ) {
                val currentValue = issueViewModel.issueTimelineResult.value ?: return@EventObserver
                var index = -1
                var payload: ReactionChangePayload? = null

                val updatedValue = currentValue.map { item ->
                    index += 1
                    if (item is IssueComment
                        && item.id == event.reactableId
                    ) {
                        val reactionGroups = item.reactionGroups ?: mutableListOf()
                        val change = reactionGroups.updateByReactionEventIfNeeded(event)

                        payload = ReactionChangePayload(index, change)
                    }
                    item
                }

                issueTimelineAdapter.submitData(lifecycle, updatedValue)

                payload?.let {
                    if (it.index >= 0) {
                        issueTimelineAdapter.notifyItemChanged(it.index, it.change)
                    }
                }
            }
        })
    }

    override fun retryInitial() {
        issueTimelineAdapter.refresh()
    }

    override fun doAction() {

    }

}