package io.github.tonnyl.moka.ui.prs

import io.github.tonnyl.moka.data.item.PullRequestItem

interface PullRequestItemActions {

    fun openPullRequestItem(data: PullRequestItem)

    fun openProfile(login: String)

}