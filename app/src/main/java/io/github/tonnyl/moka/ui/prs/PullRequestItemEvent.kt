package io.github.tonnyl.moka.ui.prs

sealed class PullRequestItemEvent {

    data class ViewPullRequest(val number: Int) : PullRequestItemEvent()

    data class ViewProfile(val login: String) : PullRequestItemEvent()

}