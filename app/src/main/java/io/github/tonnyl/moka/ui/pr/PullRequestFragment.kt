package io.github.tonnyl.moka.ui.pr

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentPrBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class PullRequestFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private lateinit var viewModel: PullRequestViewModel

    private lateinit var binding: FragmentPrBinding

    private val args: PullRequestFragmentArgs by navArgs()

    private val pullRequestTimelineAdapter by lazy {
        PullRequestTimelineAdapter(
            getString(
                R.string.issue_pr_title_format,
                args.pullRequestItem.number,
                args.pullRequestItem.title
            ),
            getString(
                R.string.issue_pr_info_format,
                getString(
                    when {
                        args.pullRequestItem.closed -> R.string.issue_pr_status_closed
                        args.pullRequestItem.merged -> R.string.issue_pr_status_merged
                        else -> R.string.issue_pr_status_open
                    }
                ),
                getString(R.string.issue_pr_by, args.pullRequestItem.login),
                DateUtils.getRelativeTimeSpanString(
                    args.pullRequestItem.createdAt.time,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                )
            ),
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

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(args.repositoryOwner, args.repositoryName, args.pullRequestItem.number)
        ).get(PullRequestViewModel::class.java)

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

        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = pullRequestTimelineAdapter
                }
            }

            pullRequestTimelineAdapter.submitList(it)
        })

        viewModel.pagedLoadStatus.observe(this, Observer {
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