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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentPrsBinding
import io.github.tonnyl.moka.ui.common.IssuePRActions
import io.github.tonnyl.moka.ui.pr.PullRequestFragmentArgs
import io.github.tonnyl.moka.ui.profile.UserProfileFragmentArgs

class PullRequestsFragment : Fragment(), IssuePRActions {

    private val adapter by lazy {
        PullRequestAdapter()
    }

    private lateinit var owner: String
    private lateinit var name: String

    private lateinit var viewModel: PullRequestsViewModel

    private val args: PullRequestsFragmentArgs by navArgs()

    private lateinit var binding: FragmentPrsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPrsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        owner = args.owner
        name = args.name

        binding.appbarLayout.toolbar.setTitle(R.string.pull_requests)
        binding.appbarLayout.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory(owner, name)).get(PullRequestsViewModel::class.java)

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@PullRequestsFragment.adapter
        }
        adapter.actions = this@PullRequestsFragment

        viewModel.issuesResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun openIssueOrPR(number: Int, title: String) {
        val pullRequestFragmentArgs = PullRequestFragmentArgs(owner, name, number, title)
        parentFragment?.findNavController()?.navigate(R.id.action_to_pr, pullRequestFragmentArgs.toBundle())
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = UserProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, profileFragmentArgs.toBundle())
    }

}