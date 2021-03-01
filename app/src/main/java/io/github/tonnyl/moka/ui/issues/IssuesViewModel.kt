package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.MokaApp

class IssuesViewModel(
    owner: String,
    name: String
) : ViewModel() {

    val issuesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                IssuesDataSource(owner, name)
            }
        ).flow.cachedIn(viewModelScope)
    }

}