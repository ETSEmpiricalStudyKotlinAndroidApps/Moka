package io.github.tonnyl.moka.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Event(
        val id: String,
        val type: String,
        val public: Boolean,
        val actor: EventActor,
        val repo: EventRepo,
        val payload: EventPayload,
        val org: EventOrg?,
        @SerializedName("created_at")
        val createdAt: Date
) : Parcelable {

    companion object {
        // The WatchEvent is related to starring a repository, not watching.
        // The event’s actor is the user who starred a repository, and the event’s repository is the repository that was starred.
        const val WATCH_EVENT = "WatchEvent"
        // Triggered when a user forks a repository.
        const val FORK_EVENT = "ForkEvent"
        // Represents a created repository, branch, or tag.
        const val CREATE_EVENT = "CreateEvent"
        // Triggered when a private repository is open sourced.
        const val PUBLIC_EVENT = "PublicEvent"
        // Triggered when an issue is assigned, unassigned, labeled, unlabeled, opened, edited, milestoned, demilestoned, closed, or reopened.
        const val ISSUES_EVENT = "IssuesEvent"
        // Triggered when a pull request is assigned, unassigned, labeled, unlabeled, opened, edited, closed, reopened, or synchronized.
        // Also triggered when a pull request review is requested, or when a review request is removed.
        const val PULL_REQUEST_EVENT = "PullRequestEvent"
    }

}