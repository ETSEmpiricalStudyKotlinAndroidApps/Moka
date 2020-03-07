package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.extension.transformToIssueCommentEvent
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.data.toNonNullIssue
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class IssueViewModel(
    private val owner: String,
    private val name: String,
    private val number: Int
) : NetworkCacheSourceViewModel<IssueTimelineItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueTimelineItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<IssueTimelineItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<IssueTimelineItem>>>
        get() = _pagedLoadStatus

    private val _issueLiveData = MutableLiveData<Resource<IssueComment?>>()
    val issueLiveData: LiveData<Resource<IssueComment?>>
        get() = _issueLiveData

    private lateinit var sourceFactory: IssueTimelineSourceFactory

    init {
        refreshIssueData()
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<IssueTimelineItem>> {
        sourceFactory = IssueTimelineSourceFactory(
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

        refreshIssueData()
    }

    private fun refreshIssueData() {
        viewModelScope.launch(Dispatchers.IO) {
            _issueLiveData.postValue(Resource.loading(null))
            try {
                val response = runBlocking {
                    GraphQLClient.apolloClient.query(
                        IssueQuery(owner, name, number)
                    ).execute()
                }

                val data = response.data()?.repository?.issue?.toNonNullIssue()

                _issueLiveData.postValue(
                    Resource.success(data?.transformToIssueCommentEvent(owner, name))
                )
            } catch (e: Exception) {
                Timber.e(e)

                _issueLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

}