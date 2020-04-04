package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.PullRequest
import io.github.tonnyl.moka.data.extension.transformToPullRequestIssueComment
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.data.toNullablePullRequest
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.PullRequestQuery
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class PullRequestViewModel(
    private val args: PullRequestFragmentArgs
) : NetworkCacheSourceViewModel<PullRequestTimelineItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestTimelineItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<PullRequestTimelineItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<PullRequestTimelineItem>>>
        get() = _pagedLoadStatus

    private val _pullRequest = MutableLiveData<PullRequest?>()
    val pullRequest: LiveData<PullRequest?>
        get() = _pullRequest

    private val _pullRequestToCommentLiveData = MutableLiveData<Resource<IssueComment?>>()
    val pullRequestToCommentLiveData: LiveData<Resource<IssueComment?>>
        get() = _pullRequestToCommentLiveData

    private lateinit var sourceFactory: PullRequestTimelineSourceFactory

    init {
        refreshPullRequestData()
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<PullRequestTimelineItem>> {
        sourceFactory = PullRequestTimelineSourceFactory(
            args.repositoryOwner,
            args.repositoryName,
            args.number,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

    override fun refresh() {
        super.refresh()

        refreshPullRequestData()
    }

    private fun refreshPullRequestData() {
        viewModelScope.launch(Dispatchers.IO) {
            _pullRequestToCommentLiveData.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(
                            PullRequestQuery(args.repositoryOwner, args.repositoryName, args.number)
                        )
                        .execute()
                }

                val data = response.data()?.repository?.pullRequest.toNullablePullRequest()

                _pullRequestToCommentLiveData.postValue(
                    Resource.success(
                        data?.transformToPullRequestIssueComment(
                            args.repositoryOwner, args.repositoryName
                        )
                    )
                )

                _pullRequest.postValue(data)
            } catch (e: Exception) {
                Timber.e(e)

                _pullRequestToCommentLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

}