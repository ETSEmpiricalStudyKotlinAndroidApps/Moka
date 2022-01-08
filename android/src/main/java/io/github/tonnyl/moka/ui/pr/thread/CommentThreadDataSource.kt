package io.github.tonnyl.moka.ui.pr.thread

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import io.tonnyl.moka.common.data.extension.checkedEndCursor
import io.tonnyl.moka.common.data.extension.checkedStartCursor
import io.tonnyl.moka.graphql.PullRequestReviewCommentsQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

class CommentThreadDataSource(
    private val apolloClient: ApolloClient,
    private val nodeId: String
) : PagingSource<String, CommentWithSimplifiedDiffHunk>() {

    override fun getRefreshKey(state: PagingState<String, CommentWithSimplifiedDiffHunk>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CommentWithSimplifiedDiffHunk> {
        val list = mutableListOf<CommentWithSimplifiedDiffHunk>()

        return withContext(Dispatchers.IO) {
            try {
                val review = apolloClient.query(
                    query = PullRequestReviewCommentsQuery(
                        id = nodeId,
                        after = Optional.presentIfNotNull(params.key),
                        before = Optional.presentIfNotNull(params.key),
                        perPage = params.loadSize
                    )
                ).execute().data?.node?.onPullRequestReview

                val timeline = review?.comments

                list.addAll(
                    timeline?.nodes.orEmpty().mapNotNull { node ->
                        node?.pullRequestReviewCommentFragment?.let { comment ->
                            val diffHunk = comment.diffHunk.split("\n")
                            CommentWithSimplifiedDiffHunk(
                                comment,
                                if (diffHunk.size >= 4) {
                                    diffHunk.subList(diffHunk.size - 4, diffHunk.size)
                                } else {
                                    diffHunk
                                }
                            )
                        }
                    }
                )

                val pageInfo = timeline?.pageInfo?.pageInfo

                LoadResult.Page(
                    data = list,
                    prevKey = pageInfo.checkedStartCursor,
                    nextKey = pageInfo.checkedEndCursor
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                LoadResult.Error(e)
            }
        }
    }

}