package io.github.tonnyl.moka.ui.issues

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
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.databinding.FragmentIssuesBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.issue.IssueFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs

class IssuesFragment : Fragment(), IssueItemActions, EmptyViewActions, PagingNetworkStateActions {

    private val args by navArgs<IssuesFragmentArgs>()

    private val issueAdapter: IssueAdapter by lazy {
        IssueAdapter(this@IssuesFragment, this@IssuesFragment)
    }

    private val viewModel by viewModels<IssuesViewModel> {
        ViewModelFactory(args.owner, args.name)
    }
    private lateinit var binding: FragmentIssuesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIssuesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            with(appbarLayout.toolbar) {
                title = getString(R.string.issues)
                setNavigationOnClickListener {
                    parentFragment?.findNavController()?.navigateUp()
                }
            }

            emptyViewActions = this@IssuesFragment
            viewModel = this@IssuesFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = issueAdapter
                }
            }

            issueAdapter.submitList(it)
        })
    }

    override fun openPullRequestItem(data: IssueItem) {
        parentFragment?.findNavController()
            ?.navigate(
                R.id.issue_fragment,
                IssueFragmentArgs(args.owner, args.name, data).toBundle()
            )
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = ProfileFragmentArgs(login)
        findNavController().navigate(R.id.profile_fragment, profileFragmentArgs.toBundle())
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