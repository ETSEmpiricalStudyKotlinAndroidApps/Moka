package io.github.tonnyl.moka.ui.prs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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

    private val pullRequestAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PullRequestAdapter(this@PullRequestsFragment, this@PullRequestsFragment)
    }

    private lateinit var viewModel: PullRequestsViewModel

    private val args: PullRequestsFragmentArgs by navArgs()

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

        viewModel = ViewModelProviders.of(this, ViewModelFactory(args.owner, args.name))
            .get(PullRequestsViewModel::class.java)

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
                R.id.action_to_pr,
                PullRequestFragmentArgs(data, args.owner, args.name).toBundle()
            )
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = ProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, profileFragmentArgs.toBundle())
    }

    override fun retryLoadPreviousNext() {

    }

    override fun retryInitial() {

    }

    override fun doAction() {

    }

}