package io.github.tonnyl.moka.ui.issue

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.IssueQuery
import io.github.tonnyl.moka.data.IssueGraphQL
import io.github.tonnyl.moka.data.item.IssueTimelineItem
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class IssueViewModel(
    private val owner: String,
    private val name: String,
    private val number: Int
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<IssueTimelineItem>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<IssueTimelineItem>>>
        get() = _loadStatusLiveData

    private val _issueLiveData = MutableLiveData<Resource<IssueGraphQL?>>()
    val issueLiveData: LiveData<Resource<IssueGraphQL?>>
        get() = _issueLiveData

    private val sourceFactory = IssueTimelineSourceFactory(viewModelScope, owner, name, number, _loadStatusLiveData)

    val issueTimelineResults: LiveData<PagedList<IssueTimelineItem>> by lazy {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20 * 1)
            .setEnablePlaceholders(false)
            .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    init {
        refreshIssueData()
    }

    fun refresh() {
        sourceFactory.invalidate()
    }

    @MainThread
    fun refreshIssueData() {
        _issueLiveData.value = Resource.loading(null)

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apolloClient.query(
                        IssueQuery.builder()
                            .owner(owner)
                            .name(name)
                            .number(number)
                            .build()
                    ).toDeferred()
                }.await()

                val data = IssueGraphQL.createFromRaw(response.data()?.repository()?.issue())

                _issueLiveData.value = Resource.success(data)
            } catch (e: Exception) {
                Timber.e(e)

                _issueLiveData.value = Resource.error(e.message, null)
            }


        }
    }

}