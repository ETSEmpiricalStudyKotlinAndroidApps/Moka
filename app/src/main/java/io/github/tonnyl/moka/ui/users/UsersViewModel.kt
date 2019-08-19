package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.UserGraphQL
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.ui.NetworkCacheSourceViewModel

class UsersViewModel(
    private val login: String,
    private val usersType: UsersType
) : NetworkCacheSourceViewModel<UserGraphQL>() {

    private val _initialLoadStatus = MutableLiveData<Resource<List<UserGraphQL>>>()
    val initialLoadStatus: LiveData<Resource<List<UserGraphQL>>>
        get() = _initialLoadStatus

    private val _pagedLoadStatus = MutableLiveData<PagedResource2<List<UserGraphQL>>>()
    val pagedLoadStatus: LiveData<PagedResource2<List<UserGraphQL>>>
        get() = _pagedLoadStatus

    private lateinit var sourceFactory: UsersDataSourceFactory

    init {
        refresh()
    }

    override fun initRemoteSource(): LiveData<PagedList<UserGraphQL>> {
        sourceFactory = UsersDataSourceFactory(
            login,
            usersType,
            _initialLoadStatus,
            _pagedLoadStatus
        )

        return LivePagedListBuilder(sourceFactory, pagingConfig)
            .build()
    }

    override fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

}