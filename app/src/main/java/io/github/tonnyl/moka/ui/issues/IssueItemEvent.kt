package io.github.tonnyl.moka.ui.issues

sealed class IssueItemEvent {

    data class ViewUserProfile(val login: String) : IssueItemEvent()

    data class ViewIssueTimeline(val number: Int) : IssueItemEvent()

}