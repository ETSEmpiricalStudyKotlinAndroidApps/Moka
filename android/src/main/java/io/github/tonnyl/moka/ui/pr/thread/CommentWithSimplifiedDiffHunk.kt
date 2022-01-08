package io.github.tonnyl.moka.ui.pr.thread

import io.tonnyl.moka.graphql.fragment.PullRequestReviewCommentFragment

typealias CommentWithSimplifiedDiffHunk = Pair<PullRequestReviewCommentFragment, List<String>>