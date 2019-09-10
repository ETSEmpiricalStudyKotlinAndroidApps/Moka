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
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.databinding.FragmentPrsBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.pr.PullRequestFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs

class PullRequestsFragment : Fragment(), PullRequestItemActions, PagingNetworkStateActions,
    EmptyViewActions {

    private val args by navArgs<PullRequestsFragmentArgs>()

    private val viewModel by viewModels<PullRequestsViewModel> {
        ViewModelFactory(args.owner, args.name)
    }

    private val pullRequestAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PullRequestAdapter(this@PullRequestsFragment, this@PullRequestsFragment)
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

            viewModel = this@PullRequestsFragment.viewModel
            emptyViewActions = this@PullRequestsFragment
            lifecycleOwner = viewLifecycleOwner
        }


        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = pullRequestAdapter
                }
            }

            pullRequestAdapter.submitList(it)
        })
    }

    override fun openPullRequestItem(data: PullRequestItem) {
        parentFragment?.findNavController()
            ?.navigate(
                R.id.pr_fragment,
                PullRequestFragmentArgs(data, args.owner, args.name).toBundle()
            )
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = ProfileFragmentArgs(login)
        findNavController().navigate(R.id.profile_fragment, profileFragmentArgs.toBundle())
    }

    override fun retryLoadPreviousNext() {

    }

    override fun retryInitial() {

    }

    override fun doAction() {

    }

}