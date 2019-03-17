package io.github.tonnyl.moka.ui.common

/**
 * Actions that can be performed on a pr or an issue.
 */
interface IssuePRActions {

    fun openIssueOrPR(number: Int, title: String)

    fun openProfile(login: String)

}