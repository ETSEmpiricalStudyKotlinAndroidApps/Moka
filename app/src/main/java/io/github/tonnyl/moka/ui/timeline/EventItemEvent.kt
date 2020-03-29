package io.github.tonnyl.moka.ui.timeline

import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.EventOrg
import io.github.tonnyl.moka.ui.profile.ProfileType

sealed class EventItemEvent {

    data class ViewProfile(
        val login: String,
        val type: ProfileType
    ) : EventItemEvent()

    data class ViewEventDetails(val event: Event) : EventItemEvent()

    data class ViewRepository(
        val fullName: String,
        val org: EventOrg?
    ) : EventItemEvent()

    data class ViewIssueDetail(
        val repoOwner: String,
        val repoName: String,
        val number: Int
    ) : EventItemEvent()

}