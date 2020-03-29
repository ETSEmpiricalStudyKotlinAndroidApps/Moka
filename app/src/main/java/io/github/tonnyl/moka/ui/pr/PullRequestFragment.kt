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
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentPrBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class PullRequestFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private val args by navArgs<PullRequestFragmentArgs>()

    private val viewModel by viewModels<PullRequestViewModel> {
        ViewModelFactory(args)
    }

    private lateinit var binding: FragmentPrBinding

    private val pullRequestTimelineAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PullRequestTimelineAdapter(
            viewLifecycleOwner,
            viewModel,
            this@PullRequestFragment
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
                    adapter = pullRequestTimelineAdapter
                }
            }

            pullRequestTimelineAdapter.submitList(it)
        })

        viewModel.pagedLoadStatus.observe(viewLifecycleOwner, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    pullRequestTimelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.LOADED)
                    )
                }
                Status.ERROR -> {
                    pullRequestTimelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.error(it.resource.message))
                    )
                }
                Status.LOADING -> {
                    pullRequestTimelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.LOADING)
                    )
                }
                null -> {

                }
            }
        })
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