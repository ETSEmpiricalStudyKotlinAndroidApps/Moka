package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.data.extension.transformToPullRequestIssueComment
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.data.toNullablePullRequest
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class PullRequestViewModel(
    private val owner: String,
    private val name: String,
    private val number: Int
) : NetworkCacheSourceViewModel<PullRequestTimelineItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<PullRequestTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<PullRequestTimelineItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<PullRequestTimelineItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<PullRequestTimelineItem>>>
        get() = _pagedLoadStatus

    private val _pullRequest = MutableLiveData<Resource<PullRequestTimelineItem?>>()
    val pullRequest: LiveData<Resource<PullRequestTimelineItem?>>
        get() = _pullRequest

    private lateinit var sourceFactory: PullRequestTimelineSourceFactory

    init {
        refreshPullRequestData()
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<PullRequestTimelineItem>> {
        sourceFactory = PullRequestTimelineSourceFactory(
            owner,
            name,
            number,
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
            _pullRequest.postValue(Resource.loading(null))

            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient
                        .query(
                            PullRequestQuery.builder()
                                .owner(owner)
                                .name(name)
                                .number(number)
                                .build()
                        )
                        .execute()
                }

                val data = response.data()?.repository()?.pullRequest().toNullablePullRequest()

                _pullRequest.postValue(Resource.success(data?.transformToPullRequestIssueComment()))
            } catch (e: Exception) {
                Timber.e(e)

                _pullRequest.postValue(Resource.error(e.message, null))
            }
        }
    }

}