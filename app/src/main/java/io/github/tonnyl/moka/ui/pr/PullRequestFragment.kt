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
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.extension.updateByReactionEventIfNeeded
import io.github.tonnyl.moka.databinding.FragmentPrBinding
import io.github.tonnyl.moka.ui.*
import io.github.tonnyl.moka.ui.UserEvent.React

class PullRequestFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private val args by navArgs<PullRequestFragmentArgs>()

    private val pullRequestViewModel by viewModels<PullRequestViewModel> {
        ViewModelFactory(args)
    }

    private val reactionsViewPool by lazy(LazyThreadSafetyMode.NONE) {
        RecyclerView.RecycledViewPool()
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentPrBinding

    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            PullRequestTimelineAdapter(
                viewLifecycleOwner,
                mainViewModel,
                reactionsViewPool
            ),
            LoadStateAdapter(this)
        )
    }
    private val mergeAdapter by lazy(LazyThreadSafetyMode.NONE) {
        MergeAdapter(
            adapterWrapper.headerAdapter,
            PullRequestDetailsAdapter(
                viewLifecycleOwner,
                mainViewModel,
                pullRequestViewModel,
                reactionsViewPool
            ),
            adapterWrapper.pagingAdapter,
            adapterWrapper.footerAdapter
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

        pullRequestViewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = mergeAdapter
                }
            }

            adapterWrapper.pagingAdapter.submitList(it)
        })

        pullRequestViewModel.pagedLoadStatus.observe(viewLifecycleOwner, adapterWrapper.observer)

        mainViewModel.fragmentScopedEvent.observe(viewLifecycleOwner, EventObserver { event ->
            if (event is React) {
                val payload = pullRequestViewModel.data.value?.updateByReactionEventIfNeeded(event)
                if (payload != null && payload.index >= 0) {
                    adapterWrapper.pagingAdapter.notifyItemChanged(payload.index, payload.change)
                }
            }
        })
    }

    override fun retryInitial() {
        pullRequestViewModel.refresh()
    }

    override fun doAction() {

    }

    override fun retryLoadPreviousNext() {
        pullRequestViewModel.retryLoadPreviousNext()
    }

}