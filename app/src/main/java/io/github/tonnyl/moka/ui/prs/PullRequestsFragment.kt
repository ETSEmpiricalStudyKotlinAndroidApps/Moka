package io.github.tonnyl.moka.ui.prs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentPrsBinding
import io.github.tonnyl.moka.ui.common.IssuePRActions
import io.github.tonnyl.moka.ui.pr.PullRequestFragmentArgs
import io.github.tonnyl.moka.ui.profile.UserProfileFragmentArgs
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_prs.*

class PullRequestsFragment : Fragment(), IssuePRActions {

    private val adapter by lazy {
        PullRequestAdapter()
    }

    private lateinit var owner: String
    private lateinit var name: String

    private lateinit var viewModel: PullRequestsViewModel

    private lateinit var binding: FragmentPrsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPrsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = PullRequestsFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments"))
        owner = args.owner
        name = args.name

        toolbar.setTitle(R.string.pull_requests)
        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory(owner, name)).get(PullRequestsViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        recycler_view.adapter = adapter
        adapter.actions = this@PullRequestsFragment

        viewModel.issuesResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun openIssueOrPR(number: Int, title: String) {
        val pullRequestFragmentArgs = PullRequestFragmentArgs.Builder(owner, name, number, title)
        parentFragment?.findNavController()?.navigate(R.id.action_to_pr, pullRequestFragmentArgs.build().toBundle())
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = UserProfileFragmentArgs.Builder(login)
        findNavController()?.navigate(R.id.action_to_profile, profileFragmentArgs.build().toBundle())
    }

}