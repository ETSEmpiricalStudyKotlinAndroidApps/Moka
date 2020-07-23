package io.github.tonnyl.moka.ui.pr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.map
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.extension.updateByReactionEventIfNeeded
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.databinding.FragmentPrBinding
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.UserEvent.React
import io.github.tonnyl.moka.ui.reaction.ReactionChangePayload

class PullRequestFragment : Fragment(), EmptyViewActions {

    private val args by navArgs<PullRequestFragmentArgs>()

    private val pullRequestViewModel by viewModels<PullRequestViewModel> {
        ViewModelFactory(args)
    }

    private val reactionsViewPool by lazy(LazyThreadSafetyMode.NONE) {
        RecyclerView.RecycledViewPool()
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentPrBinding

    private val pullRequestTimelineAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = PullRequestTimelineAdapter(
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
            PullRequestDetailsAdapter(
                viewLifecycleOwner,
                mainViewModel,
                pullRequestViewModel,
                reactionsViewPool
            ),
            pullRequestTimelineAdapter
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(appbarLayout.toolbar) {
                title = getString(R.string.pull_request)
                setNavigationOnClickListener {
                    parentFragment?.findNavController()?.navigateUp()
                }
            }

            viewModel = this@PullRequestFragment.pullRequestViewModel
            emptyViewActions = this@PullRequestFragment
            lifecycleOwner = viewLifecycleOwner
        }

        pullRequestViewModel.prTimelineResult.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = concatAdapter
                }
            }

            pullRequestTimelineAdapter.submitData(lifecycle, it)
        })

        mainViewModel.fragmentScopedEvent.observe(viewLifecycleOwner, EventObserver { event ->
            if (event is React
                && event.resource.status == Status.SUCCESS
            ) {
                val currentValue =
                    pullRequestViewModel.prTimelineResult.value ?: return@EventObserver
                var index = -1
                var payload: ReactionChangePayload? = null
                val data = currentValue.map { item ->
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

                pullRequestTimelineAdapter.submitData(lifecycle, data)

                payload?.let {
                    if (it.index >= 0) {
                        pullRequestTimelineAdapter.notifyItemChanged(it.index, it.change)
                    }
                }
            }
        })
    }

    override fun retryInitial() {
        pullRequestTimelineAdapter.refresh()
    }

    override fun doAction() {

    }

}