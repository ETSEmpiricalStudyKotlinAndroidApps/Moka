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
import io.github.tonnyl.moka.databinding.FragmentIssuesBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.PagedListAdapterWrapper
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.issue.IssueFragmentArgs
import io.github.tonnyl.moka.ui.issues.IssueItemEvent.ViewIssueTimeline
import io.github.tonnyl.moka.ui.issues.IssueItemEvent.ViewUserProfile
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType

class IssuesFragment : Fragment(), EmptyViewActions, PagingNetworkStateActions {

    private val args by navArgs<IssuesFragmentArgs>()

    private val adapterWrapper by lazy(LazyThreadSafetyMode.NONE) {
        PagedListAdapterWrapper(
            LoadStateAdapter(this),
            IssueAdapter(viewLifecycleOwner, viewModel),
            LoadStateAdapter(this)
        )
    }

    private val viewModel by viewModels<IssuesViewModel> {
        ViewModelFactory(args)
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

        viewModel.data.observe(viewLifecycleOwner, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = adapterWrapper.mergeAdapter
                }
            }

            adapterWrapper.pagingAdapter.submitList(it)
        })

        viewModel.pagedLoadStatus.observe(viewLifecycleOwner, adapterWrapper.observer)

        viewModel.event.observe(viewLifecycleOwner, Observer {
            when (val event = it.getContentIfNotHandled()) {
                is ViewUserProfile -> {
                    val profileFragmentArgs = ProfileFragmentArgs(event.login, ProfileType.USER)
                    findNavController().navigate(
                        R.id.profile_fragment,
                        profileFragmentArgs.toBundle()
                    )
                }
                is ViewIssueTimeline -> {
                    parentFragment?.findNavController()
                        ?.navigate(
                            R.id.issue_fragment,
                            IssueFragmentArgs(args.owner, args.name, event.number).toBundle()
                        )
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