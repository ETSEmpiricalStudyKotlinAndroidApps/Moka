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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.CommentAuthorAssociation
import io.github.tonnyl.moka.databinding.FragmentIssuePrBinding
import io.github.tonnyl.moka.net.GlideLoader
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_issue_pr.*
import kotlinx.android.synthetic.main.item_issue_timeline_comment.*

class IssueFragment : Fragment() {

    private val adapter: IssueTimelineAdapter by lazy {
        IssueTimelineAdapter()
    }

    private lateinit var repositoryOwner: String
    private lateinit var repositoryName: String
    private var issueNumber: Int = 0
    private lateinit var issueTitle: String

    private lateinit var viewModel: IssueViewModel

    private lateinit var binding: FragmentIssuePrBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIssuePrBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = IssueFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments"))

        repositoryOwner = args.owner
        repositoryName = args.name
        issueNumber = args.number
        issueTitle = args.title

        issue_title.text = issueTitle

        toolbar.title = ""
        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory(repositoryOwner, repositoryName, issueNumber)).get(IssueViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        issue_timeline_recycler_view.layoutManager = layoutManager
        issue_timeline_recycler_view.adapter = adapter

        viewModel.issueTimelineResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.issueLiveData.observe(viewLifecycleOwner, Observer { data ->
            data?.let { issue ->
                if (issue.body.isNotEmpty()) {
                    GlideLoader.loadAvatar(issue.author?.avatarUrl?.toString(), issue_timeline_comment_avatar)

                    issue_timeline_comment_username.text = issue.author?.login
                    issue_timeline_comment_created_at.text = DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    issue_timeline_comment_content.text = data.body

                    val stringResId = when (issue.authorAssociation) {
                        CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
                        CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
                        CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
                        CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
                        CommentAuthorAssociation.MEMBER -> R.string.author_association_member
                        CommentAuthorAssociation.NONE -> -1
                        CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                    }
                    issue_timeline_comment_author_association.text = if (stringResId != -1) getString(stringResId) else ""
                } else {
                    issue_timeline_comment_layout.visibility = View.GONE
                }

                val numberString = getString(R.string.issue_pr_number, issueNumber)
                val byString = getString(R.string.issue_pr_by, issue.author?.login)
                val createdString = DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                issue_info.text = getString(R.string.issue_pr_info_format, numberString, byString, createdString)
            }
        })

    }

}