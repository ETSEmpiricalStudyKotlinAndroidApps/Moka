package io.github.tonnyl.moka.ui.commit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.github.tonnyl.moka.AccountInstance
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.CommitResponse
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
class CommitViewModel(
    accountInstance: AccountInstance,
    owner: String,
    repo: String,
    ref: String
) : ViewModel() {

    private val _commitResp = MutableLiveData<CommitResponse>()
    val commitResp: LiveData<CommitResponse>
        get() = _commitResp

    val commitFilesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = MokaApp.defaultPagingConfig,
            pagingSourceFactory = {
                CommitDataSource(
                    initialResp = _commitResp,
                    commitApi = accountInstance.commitApi,
                    owner = owner,
                    repo = repo,
                    ref = ref
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

}