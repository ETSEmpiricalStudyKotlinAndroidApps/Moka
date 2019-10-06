package io.github.tonnyl.moka.data.extension

import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.item.IssueComment
import java.util.*

fun Issue.transformToIssueCommentEvent(): IssueComment {
    return IssueComment(
        author,
        authorAssociation,
        createdAt,
        body,
        UUID.randomUUID().toString(),
        editor,
        false,
        viewerCanReact,
        viewerDidAuthor,
        viewerCanUpdate,
        false,
        viewerCannotUpdateReasons
    )
}

fun PullRequest.transformToPullRequestIssueComment(): IssueComment {
    return IssueComment(
        author,
        authorAssociation,
        createdAt,
        body,
        UUID.randomUUID().toString(),
        editor,
        false,
        viewerCanReact,
        viewerDidAuthor,
        viewerCanUpdate,
        false,
        viewerCannotUpdateReasons
    )
}