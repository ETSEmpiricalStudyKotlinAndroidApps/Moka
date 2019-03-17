package io.github.tonnyl.moka.ui.issues

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
import io.github.tonnyl.moka.databinding.FragmentIssuesBinding
import io.github.tonnyl.moka.ui.common.IssuePRActions
import io.github.tonnyl.moka.ui.issue.IssueFragmentArgs
import io.github.tonnyl.moka.ui.profile.UserProfileFragmentArgs
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_issues.*

class IssuesFragment : Fragment(), IssuePRActions {

    private val adapter: IssueAdapter by lazy {
        IssueAdapter()
    }

    private lateinit var owner: String
    private lateinit var name: String

    private lateinit var viewModel: IssuesViewModel

    private lateinit var binding: FragmentIssuesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIssuesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = IssuesFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments"))
        owner = args.owner
        name = args.name

        toolbar.setTitle(R.string.issues)
        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory(owner, name)).get(IssuesViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager
        adapter.actions = this@IssuesFragment
        recycler_view.adapter = adapter

        viewModel.issuesResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    override fun openIssueOrPR(number: Int, title: String) {
        val issueFragmentArgs = IssueFragmentArgs.Builder(owner, name, number, title)
        parentFragment?.findNavController()?.navigate(R.id.action_to_issue, issueFragmentArgs.build().toBundle())
    }

    override fun openProfile(login: String) {
        val profileFragmentArgs = UserProfileFragmentArgs.Builder(login)
        findNavController()?.navigate(R.id.action_to_profile, profileFragmentArgs.build().toBundle())
    }

}