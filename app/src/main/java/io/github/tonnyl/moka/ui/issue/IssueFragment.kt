package io.github.tonnyl.moka.ui.issue

import android.os.Bundle
import android.text.format.DateUtils
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
import io.github.tonnyl.moka.data.CommentAuthorAssociation
import io.github.tonnyl.moka.databinding.FragmentIssuePrBinding
import io.github.tonnyl.moka.network.GlideLoader
import io.github.tonnyl.moka.network.Status

class IssueFragment : Fragment() {

    private val adapter: IssueTimelineAdapter by lazy {
        IssueTimelineAdapter()
    }

    private lateinit var repositoryOwner: String
    private lateinit var repositoryName: String
    private var issueNumber: Int = 0
    private lateinit var issueTitle: String

    private lateinit var viewModel: IssueViewModel

    private val args: IssueFragmentArgs by navArgs()

    private lateinit var binding: FragmentIssuePrBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIssuePrBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repositoryOwner = args.owner
        repositoryName = args.name
        issueNumber = args.number
        issueTitle = args.title

        viewModel = ViewModelProviders.of(this, ViewModelFactory(repositoryOwner, repositoryName, issueNumber)).get(IssueViewModel::class.java)

        binding.issueTitle.text = issueTitle

        with(binding.appbarLayout.toolbar) {
            title = ""
            setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }
        }

        with(binding.issueTimelineRecyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@IssueFragment.adapter
        }

        viewModel.issueTimelineResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.issueLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val issue = resource.data ?: return@Observer
                    with(binding.itemIssueTimelineComment) {
                        if (issue.body.isNotEmpty()) {
                            GlideLoader.loadAvatar(issue.author?.avatarUrl?.toString(), issueTimelineCommentAvatar)

                            issueTimelineCommentUsername.text = issue.author?.login
                            issueTimelineCommentCreatedAt.text = DateUtils.getRelativeTimeSpanString(issue.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                            issueTimelineCommentContent.text = issue.body

                            val stringResId = when (issue.authorAssociation) {
                                CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
                                CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
                                CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
                                CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
                                CommentAuthorAssociation.MEMBER -> R.string.author_association_member
                                CommentAuthorAssociation.NONE -> -1
                                CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                            }
                            issueTimelineCommentAuthorAssociation.text = if (stringResId != -1) getString(stringResId) else ""
                        } else {
                            root.visibility = View.GONE
                        }
                    }

                    val numberString = getString(R.string.issue_pr_number, issueNumber)
                    val byString = getString(R.string.issue_pr_by, issue.author?.login)
                    val createdString = DateUtils.getRelativeTimeSpanString(issue.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    binding.issueInfo.text = getString(R.string.issue_pr_info_format, numberString, byString, createdString)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

    }

}