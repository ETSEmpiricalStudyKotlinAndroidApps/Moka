package io.github.tonnyl.moka.ui.issues

import io.github.tonnyl.moka.data.item.IssueItem

interface IssueItemActions {

    fun openPullRequestItem(data: IssueItem)

    fun openProfile(login: String)

}