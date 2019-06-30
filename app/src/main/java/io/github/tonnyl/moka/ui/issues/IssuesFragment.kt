package io.github.tonnyl.moka.ui.issues

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
import io.github.tonnyl.moka.databinding.FragmentIssuesBinding
import io.github.tonnyl.moka.ui.common.IssuePRActions
import io.github.tonnyl.moka.ui.issue.IssueFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs

class IssuesFragment : Fragment(), IssuePRActions {

    private val adapter: IssueAdapter by lazy {
        IssueAdapter()
    }

    private lateinit var owner: String
    private lateinit var name: String

    private lateinit var viewModel: IssuesViewModel

    private val args: IssuesFragmentArgs by navArgs()

    private lateinit var binding: FragmentIssuesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIssuesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        owner = args.owner
        name = args.name

        viewModel = ViewModelProviders.of(this, ViewModelFactory(owner, name)).get(IssuesViewModel::class.java)

        with(binding.appbarLayout.toolbar) {
            setTitle(R.string.issues)
            setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }
        }

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@IssuesFragment.adapter.apply {
                actions = this@IssuesFragment
            }
        }

        viewModel.issuesResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun openIssueOrPR(number: Int, title: String) {
        val issueFragmentArgs = IssueFragmentArgs(owner, name, number, title)
        parentFragment?.findNavController()?.navigate(R.id.action_to_issue, issueFragmentArgs.toBundle())
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = ProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, profileFragmentArgs.toBundle())
    }

}