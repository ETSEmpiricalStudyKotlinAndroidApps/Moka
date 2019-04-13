package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.PagedResource
import io.github.tonnyl.moka.data.UserGraphQL

class UsersViewModel(
        private val login: String,
        private val userType: UserType
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<UserGraphQL>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<UserGraphQL>>>
        get() = _loadStatusLiveData

    private val sourceFactory = UsersDataSourceFactory(login, userType, _loadStatusLiveData)

    val usersResults: LiveData<PagedList<UserGraphQL>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    fun refresh() {
        sourceFactory.invalidate()
    }

}