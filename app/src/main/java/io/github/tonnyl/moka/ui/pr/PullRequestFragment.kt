package io.github.tonnyl.moka.ui.pr

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

class PullRequestFragment : Fragment() {

    private lateinit var viewModel: PullRequestViewModel

    private lateinit var binding: FragmentIssuePrBinding

    private val args: PullRequestFragmentArgs by navArgs()

    private lateinit var repositoryOwner: String
    private lateinit var repositoryName: String
    private var prNumber: Int = 0
    private lateinit var prTitle: String

    private val adapter: PullRequestTimelineAdapter by lazy {
        PullRequestTimelineAdapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentIssuePrBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repositoryOwner = args.owner
        repositoryName = args.name
        prNumber = args.number
        prTitle = args.title

        binding.issueTitle.text = prTitle

        with(binding.appbarLayout.toolbar) {
            title = getString(R.string.pull_request)
            setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }
        }

        viewModel = ViewModelProviders.of(this, ViewModelFactory(repositoryOwner, repositoryName, prNumber)).get(PullRequestViewModel::class.java)

        with(binding.issueTimelineRecyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = this@PullRequestFragment.adapter
        }

        viewModel.pullRequestTimelineResults.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        viewModel.pullRequestLiveData.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val pullRequest = resource.data ?: return@Observer

                    with(binding.itemIssueTimelineComment) {
                        if (pullRequest.body.isNotEmpty()) {
                            GlideLoader.loadAvatar(pullRequest.author?.avatarUrl?.toString(), binding.itemIssueTimelineComment.issueTimelineCommentAvatar)

                            issueTimelineCommentUsername.text = pullRequest.author?.login
                            issueTimelineCommentCreatedAt.text = DateUtils.getRelativeTimeSpanString(pullRequest.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
                            issueTimelineCommentContent.text = pullRequest.body

                            val stringResId = when (pullRequest.authorAssociation) {
                                CommentAuthorAssociation.COLLABORATOR -> R.string.author_association_collaborator
                                CommentAuthorAssociation.CONTRIBUTOR -> R.string.author_association_contributor
                                CommentAuthorAssociation.FIRST_TIMER -> R.string.author_association_first_timer
                                CommentAuthorAssociation.FIRST_TIME_CONTRIBUTOR -> R.string.author_association_first_timer_contributor
                                CommentAuthorAssociation.MEMBER -> R.string.author_association_member
                                CommentAuthorAssociation.OWNER -> R.string.author_association_owner
                                else -> -1
                            }
                            issueTimelineCommentAuthorAssociation.text = if (stringResId != -1) getString(stringResId) else ""
                        } else {
                            root.visibility = View.GONE
                        }
                    }

                    val numberString = getString(R.string.issue_pr_number, pullRequest.number)
                    val byString = getString(R.string.issue_pr_by, pullRequest.author?.login)
                    val createdString = DateUtils.getRelativeTimeSpanString(pullRequest.createdAt.time, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS)
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