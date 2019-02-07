package io.github.tonnyl.moka.ui.pr

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.type.CommentAuthorAssociation
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_issue_pr.*
import kotlinx.android.synthetic.main.item_issue_timeline_comment.*

class PullRequestFragment : Fragment() {

    private lateinit var viewModel: PullRequestViewModel

    private val repositoryOwner: String by lazy {
        PullRequestFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).owner
    }
    private val repositoryName: String by lazy {
        requireNotNull(arguments)
        PullRequestFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).name
    }
    private val prNumber: Int by lazy {
        requireNotNull(arguments)
        PullRequestFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).number
    }
    private val prTitle: String by lazy {
        requireNotNull(arguments)
        PullRequestFragmentArgs.fromBundle(arguments
                ?: throw IllegalArgumentException("Missing arguments")).title
    }

    private val adapter: PullRequestTimelineAdapter by lazy {
        PullRequestTimelineAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)

        return inflater.inflate(R.layout.fragment_issue_pr, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        issue_title.text = prTitle

        toolbar.title = getString(R.string.pull_request)
        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        swipe_refresh.setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.indigo, null),
                ResourcesCompat.getColor(resources, R.color.teal, null),
                ResourcesCompat.getColor(resources, R.color.lightBlue, null),
                ResourcesCompat.getColor(resources, R.color.yellow, null),
                ResourcesCompat.getColor(resources, R.color.orange, null)
        )

        viewModel = ViewModelProviders.of(this, ViewModelFactory(repositoryOwner, repositoryName, prNumber)).get(PullRequestViewModel::class.java)

        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        issue_timeline_recycler_view.layoutManager = layoutManager
        issue_timeline_recycler_view.adapter = adapter

        viewModel.pullRequestTimelineResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.pullRequestLiveData.observe(viewLifecycleOwner, Observer { data ->
            data?.let { pullRequest ->
                if (pullRequest.body.isNotEmpty()) {
                    GlideLoader.loadAvatar(pullRequest.author?.avatarUrl?.toString(), issue_timeline_comment_avatar)

                    issue_timeline_comment_username.text = pullRequest.author?.login
                    issue_timeline_comment_created_at.text = DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                    issue_timeline_comment_content.text = data.body

                    val stringResId = when (pullRequest.authorAssociation) {
                        CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
                        CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
                        CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
                        CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
                        CommentAuthorAssociation.MEMBER -> R.string.author_association_member
                        CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                        else -> -1
                    }
                    issue_timeline_comment_author_association.text = if (stringResId != -1) getString(stringResId) else ""
                } else {
                    issue_timeline_comment_layout.visibility = View.GONE
                }

                val numberString = getString(R.string.issue_pr_number, pullRequest.number)
                val byString = getString(R.string.issue_pr_by, pullRequest.author?.login)
                val createdString = DateUtils.getRelativeTimeSpanString(data.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                issue_info.text = getString(R.string.issue_pr_info_format, numberString, byString, createdString)
            }
        })
    }

}