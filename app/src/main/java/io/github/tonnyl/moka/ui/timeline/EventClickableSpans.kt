package io.github.tonnyl.moka.ui.timeline

import android.content.res.Resources
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.ui.profile.ProfileType

@ExperimentalPagingApi
abstract class EventClickableSpans(
    val resources: Resources,
    val viewModel: TimelineViewModel
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.run {
            color = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
            isUnderlineText = false
        }
    }

}

@ExperimentalPagingApi
class ActorClickableSpan(
    val actor: EventActor,
    val type: ProfileType,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {
        viewModel.viewProfile(actor.login, type)
    }

}

@ExperimentalPagingApi
class RepositoryClickableSpan(
    private val fullName: String,
    private val org: EventOrg?,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {
        viewModel.viewRepository(fullName, org)
    }

}

@ExperimentalPagingApi
class IssueNumberClickableSpan(
    val issue: EventIssue,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class PullRequestNumberClickableSpan(
    val pullRequest: EventPullRequest,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class ProjectClickableSpan(
    val project: EventProject,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class ProjectColumnClickableSpan(
    val projectColumn: EventProjectColumn,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class ProjectCardClickableSpan(
    val projectCard: EventProjectCard,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class ReleaseClickableSpan(
    val release: EventRelease,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class TeamClickableSpan(
    val team: EventTeam,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}

@ExperimentalPagingApi
class GistClickableSpan(
    val gist: Gist,
    resources: Resources,
    viewModel: TimelineViewModel
) : EventClickableSpans(resources, viewModel) {

    override fun onClick(v: View) {

    }

}