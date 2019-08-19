package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class IssuesViewModel(
    private val owner: String,
    private val name: String
) : NetworkCacheSourceViewModel<IssueItem>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<IssueItem>>>()
    val initialLoadStatus: LiveData<Resource<List<IssueItem>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<IssueItem>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<IssueItem>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: IssuesDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<IssueItem>> {
        sourceFactory = IssuesDataSourceFactory(owner, name, _initialLoadStatus, _pagedLoadStatus)

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

}