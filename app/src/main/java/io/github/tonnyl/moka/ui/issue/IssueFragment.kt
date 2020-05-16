package io.github.tonnyl.moka.ui.issue

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
import io.github.tonnyl.moka.databinding.FragmentIssueBinding
import io.github.tonnyl.moka.ui.*

class IssueFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private val reactionsViewPool by lazy(LazyThreadSafetyMode.NONE) {
        RecyclerView.RecycledViewPool()
    }
    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            IssueTimelineAdapter(
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
            IssueDetailsAdapter(
                viewLifecycleOwner,
                issueViewModel,
                mainViewModel,
                reactionsViewPool
            ),
            adapterWrapper.pagingAdapter,
            adapterWrapper.footerAdapter
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

        issueViewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = mergeAdapter
                }
            }

            adapterWrapper.pagingAdapter.submitList(it)
        })

        issueViewModel.pagedLoadStatus.observe(viewLifecycleOwner, adapterWrapper.observer)

        mainViewModel.reactEvent.observe(viewLifecycleOwner, EventObserver { event ->
            val payload = issueViewModel.data.value?.updateByReactionEventIfNeeded(event)
            if (payload != null
                && payload.index >= 0
            ) {
                adapterWrapper.pagingAdapter.notifyItemChanged(payload.index, payload.change)
            }
        })
    }

    override fun retryInitial() {
        issueViewModel.refresh()
    }

    override fun doAction() {

    }

    override fun retryLoadPreviousNext() {
        issueViewModel.retryLoadPreviousNext()
    }

}