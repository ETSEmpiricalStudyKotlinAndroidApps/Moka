package io.github.tonnyl.moka.data.extension

import io.github.tonnyl.moka.data.IssueGraphQL
import io.github.tonnyl.moka.data.PullRequestGraphQL
import io.github.tonnyl.moka.data.item.IssueCommentEvent
import io.github.tonnyl.moka.data.item.PullRequestIssueComment
import java.util.*

fun IssueGraphQL.transformToIssueCommentEvent(): IssueCommentEvent {
    return IssueCommentEvent(
        author?.avatarUrl,
        author?.login,
        authorAssociation,
        createdAt,
        body,
        UUID.randomUUID().toString(),
        editor?.avatarUrl,
        editor?.login
    )
}

fun PullRequestGraphQL.transformToPullRequestIssueComment(): PullRequestIssueComment {
    return PullRequestIssueComment(
        author?.avatarUrl,
        author?.login,
        authorAssociation,
        createdAt,
        body,
        UUID.randomUUID().toString(),
        editor?.avatarUrl,
        editor?.login
    )
}