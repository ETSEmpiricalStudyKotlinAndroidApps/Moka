package io.github.tonnyl.moka.ui.pr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentPrBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.PagedListAdapterWrapper
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class PullRequestFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private val args by navArgs<PullRequestFragmentArgs>()

    private val viewModel by viewModels<PullRequestViewModel> {
        ViewModelFactory(args)
    }

    private lateinit var binding: FragmentPrBinding

    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            PullRequestTimelineAdapter(
                viewLifecycleOwner,
                viewModel,
                RecyclerView.RecycledViewPool()
            ),
            LoadStateAdapter(this)
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

            viewModel = this@PullRequestFragment.viewModel
            emptyViewActions = this@PullRequestFragment
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = adapterWrapper.mergeAdapter
                }
            }

            adapterWrapper.pagingAdapter.submitList(it)
        })

        viewModel.pagedLoadStatus.observe(viewLifecycleOwner, adapterWrapper.observer)
    }

    override fun retryInitial() {
        viewModel.refresh()
    }

    override fun doAction() {

    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

}