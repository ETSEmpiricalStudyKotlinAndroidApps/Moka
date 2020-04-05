package io.github.tonnyl.moka.ui.issue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Issue
import io.github.tonnyl.moka.data.extension.transformToIssueCommentEvent
import io.github.tonnyl.moka.data.item.IssueComment
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.data.toNonNullIssue
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryIssue
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class IssueViewModel(
    val args: IssueFragmentArgs
) : NetworkCacheSourceViewModel<IssueTimelineItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueTimelineItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueTimelineItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource<List<IssueTimelineItem>>>()
    val pagedLoadStatus: LiveData<PagedResource<List<IssueTimelineItem>>>
        get() = _pagedLoadStatus

    private val _issueToCommentLiveData = MutableLiveData<Resource<IssueComment?>>()
    val issueToCommentLiveData: LiveData<Resource<IssueComment?>>
        get() = _issueToCommentLiveData

    private val _issueLiveData = MutableLiveData<Issue>()
    val issueLiveData: LiveData<Issue>
        get() = _issueLiveData

    private lateinit var sourceFactory: IssueTimelineSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<IssueTimelineItem>> {
        sourceFactory = IssueTimelineSourceFactory(
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

        refreshIssueData()
    }

    private fun refreshIssueData() {
        viewModelScope.launch(Dispatchers.IO) {
            _issueToCommentLiveData.postValue(Resource.loading(null))
            try {
                val response = queryIssue(args.repositoryOwner, args.repositoryName, args.number)

                val data = response.data()?.repository?.issue?.toNonNullIssue()

                _issueToCommentLiveData.postValue(
                    Resource.success(
                        data?.transformToIssueCommentEvent(
                            args.repositoryOwner,
                            args.repositoryName
                        )
                    )
                )

                _issueLiveData.postValue(data)
            } catch (e: Exception) {
                Timber.e(e)

                _issueToCommentLiveData.postValue(Resource.error(e.message, null))
            }
        }
    }

}