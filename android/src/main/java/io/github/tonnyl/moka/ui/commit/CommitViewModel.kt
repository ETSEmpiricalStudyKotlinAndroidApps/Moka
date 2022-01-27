package io.github.tonnyl.moka.ui.commit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.cachedIn
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.CommitResponse
import io.tonnyl.moka.common.ui.defaultPagingConfig
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
data class CommitViewModelExtra(
    val accountInstance: AccountInstance,
    val owner: String,
    val repo: String,
    val ref: String
)

@ExperimentalSerializationApi
class CommitViewModel(extra: CommitViewModelExtra) : ViewModel() {

    private val _commitResp = MutableLiveData<CommitResponse>()
    val commitResp: LiveData<CommitResponse>
        get() = _commitResp

    val commitFilesFlow by lazy(LazyThreadSafetyMode.NONE) {
        Pager(
            config = defaultPagingConfig,
            pagingSourceFactory = {
                CommitDataSource(
                    initialResp = _commitResp,
                    commitApi = extra.accountInstance.commitApi,
                    owner = extra.owner,
                    repo = extra.repo,
                    ref = extra.ref
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    companion object {

        private object CommitViewModelExtraKeyImpl : CreationExtras.Key<CommitViewModelExtra>

        val COMMIT_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<CommitViewModelExtra> =
            CommitViewModelExtraKeyImpl

    }

}