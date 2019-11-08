package io.github.tonnyl.moka.ui.timeline

import android.content.res.Resources
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.res.ResourcesCompat
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.ui.profile.ProfileType

abstract class EventClickableSpans(
    val resources: Resources,
    val actions: EventActions?
) : ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.run {
            color = ResourcesCompat.getColor(resources, R.color.colorTextAccent, null)
            isUnderlineText = false
        }
    }

}

class ActorClickableSpan(
    val actor: EventActor,
    val type: ProfileType,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {
        actions?.openProfile(
            actor.login,
            type
        )
    }

}

class RepositoryClickableSpan(
    private val fullName: String,
    private val org: EventOrg?,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {
        actions?.viewRepository(fullName, org)
    }

}

class IssueNumberClickableSpan(
    val issue: EventIssue,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class PullRequestNumberClickableSpan(
    val pullRequest: EventPullRequest,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class ProjectClickableSpan(
    val project: EventProject,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class ProjectColumnClickableSpan(
    val projectColumn: EventProjectColumn,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class ProjectCardClickableSpan(
    val projectCard: EventProjectCard,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class ReleaseClickableSpan(
    val release: EventRelease,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class TeamClickableSpan(
    val team: EventTeam,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}

class GistClickableSpan(
    val gist: Gist,
    resources: Resources,
    actions: EventActions?
) : EventClickableSpans(resources, actions) {

    override fun onClick(v: View) {

    }

}