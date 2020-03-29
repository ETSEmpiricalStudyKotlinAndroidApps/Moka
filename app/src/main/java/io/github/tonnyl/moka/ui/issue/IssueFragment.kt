package io.github.tonnyl.moka.ui.issue

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
import io.github.tonnyl.moka.databinding.FragmentIssueBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions

class IssueFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private val issueTimelineAdapter by lazy(LazyThreadSafetyMode.NONE) {
        IssueTimelineAdapter(viewLifecycleOwner, viewModel, this@IssueFragment)
    }

    private val viewModel by viewModels<IssueViewModel> {
        ViewModelFactory(args)
    }

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
            viewModel = this@IssueFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = issueTimelineAdapter
                }
            }

            issueTimelineAdapter.submitList(it)
        })

        viewModel.pagedLoadStatus.observe(viewLifecycleOwner, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    issueTimelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.LOADED)
                    )
                }
                Status.ERROR -> {
                    issueTimelineAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.error(it.resource.message))
                    )
                }
                Status.LOADING -> {
                    issueTimelineAdapter.setNetworkState(
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