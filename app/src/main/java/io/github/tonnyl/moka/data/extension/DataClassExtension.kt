package io.github.tonnyl.moka.data.extension

import androidx.annotation.WorkerThread
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.util.HtmlHandler
import java.util.*

@WorkerThread
fun Issue.transformToIssueCommentEvent(
    login: String,
    repoName: String
): IssueComment {
    return IssueComment(
        author,
        authorAssociation,
        createdAt,
        HtmlHandler.toHtml(body, login, repoName),
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

@WorkerThread
fun PullRequest.transformToPullRequestIssueComment(
    login: String,
    repoName: String
): IssueComment {
    return IssueComment(
        author,
        authorAssociation,
        createdAt,
        HtmlHandler.toHtml(body, login, repoName),
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