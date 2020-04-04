package io.github.tonnyl.moka.ui.prs

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
import io.github.tonnyl.moka.databinding.FragmentPrsBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.PagedListAdapterWrapper
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.pr.PullRequestFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.prs.PullRequestItemEvent.ViewProfile
import io.github.tonnyl.moka.ui.prs.PullRequestItemEvent.ViewPullRequest

class PullRequestsFragment : Fragment(), PagingNetworkStateActions, EmptyViewActions {

    private val args by navArgs<PullRequestsFragmentArgs>()

    private val pullRequestsViewModel by viewModels<PullRequestsViewModel> {
        ViewModelFactory(args)
    }

    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            PullRequestAdapter(viewLifecycleOwner, pullRequestsViewModel),
            LoadStateAdapter(this)
        )
    }

    private lateinit var binding: FragmentPrsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPrsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            with(appbarLayout.toolbar) {
                setTitle(R.string.pull_requests)
                setNavigationOnClickListener {
                    parentFragment?.findNavController()?.navigateUp()
                }
            }

            viewModel = this@PullRequestsFragment.pullRequestsViewModel
            emptyViewActions = this@PullRequestsFragment
            lifecycleOwner = viewLifecycleOwner
        }


        pullRequestsViewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = adapterWrapper.mergeAdapter
                }
            }

            adapterWrapper.pagingAdapter.submitList(it)
        })

        pullRequestsViewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is ViewPullRequest -> {
                    findNavController().navigate(
                        R.id.pr_fragment,
                        PullRequestFragmentArgs(event.number, args.owner, args.name).toBundle()
                    )
                }
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(event.login).toBundle()
                    )
                }
            }
        })
    }

    override fun retryLoadPreviousNext() {

    }

    override fun retryInitial() {

    }

    override fun doAction() {

    }

}