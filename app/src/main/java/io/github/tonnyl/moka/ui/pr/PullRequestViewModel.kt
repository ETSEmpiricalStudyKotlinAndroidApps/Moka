package io.github.tonnyl.moka.ui.pr

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.PullRequestQuery
import io.github.tonnyl.moka.data.PullRequestGraphQL
import io.github.tonnyl.moka.data.item.PullRequestTimelineItem
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PullRequestViewModel(
    private val owner: String,
    private val name: String,
    private val number: Int
) : ViewModel() {

    private val _loadStatusLiveData = MutableLiveData<PagedResource<List<PullRequestTimelineItem>>>()
    val loadStatusLiveData: LiveData<PagedResource<List<PullRequestTimelineItem>>>
        get() = _loadStatusLiveData

    private val _pullRequestLiveData = MutableLiveData<Resource<PullRequestGraphQL?>>()
    val pullRequestLiveData: LiveData<Resource<PullRequestGraphQL?>>
        get() = _pullRequestLiveData

    private val sourceFactory =
        PullRequestTimelineSourceFactory(viewModelScope, owner, name, number, _loadStatusLiveData)

    val pullRequestTimelineResults: LiveData<PagedList<PullRequestTimelineItem>> by lazy {
        val config = PagedList.Config.Builder()
            .setPageSize(20)
            .setInitialLoadSizeHint(20 * 1)
            .setEnablePlaceholders(false)
            .build()

        LivePagedListBuilder(sourceFactory, config).build()
    }

    init {
        refreshPullRequestData()
    }

    fun refresh() {
        sourceFactory.invalidate()
    }

    fun refreshPullRequestData() {
        viewModelScope.launch(Dispatchers.Main) {
            _pullRequestLiveData.value = Resource.loading(null)

            try {
                val response = withContext(Dispatchers.IO) {
                    NetworkClient.apolloClient
                        .query(
                            PullRequestQuery.builder()
                                .owner(owner)
                                .name(name)
                                .number(number)
                                .build()
                        ).toDeferred()
                }.await()

                Timber.d("response: $response")

                val data = PullRequestGraphQL.createFromRaw(response.data()?.repository()?.pullRequest())

                _pullRequestLiveData.value = Resource.success(data)
            } catch (e: Exception) {
                Timber.e(e)

                _pullRequestLiveData.value = Resource.error(e.message, null)
            }
        }
    }

}