package io.github.tonnyl.moka.ui.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.UserGraphQL

class UsersViewModel(
        private val login: String,
        private val userType: UserType
) : ViewModel() {

    private val sourceFactory = UsersDataSourceFactory(login, userType)

    val usersResults: LiveData<PagedList<UserGraphQL>> by lazy {
        val config = PagedList.Config.Builder()
                .setPageSize(20)
                .setInitialLoadSizeHint(20 * 1)
                .setEnablePlaceholders(false)
                .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

}