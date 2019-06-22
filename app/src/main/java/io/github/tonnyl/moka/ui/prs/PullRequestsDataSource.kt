package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.PullRequestsQuery
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PullRequestsDataSource(
    private val coroutineScope: CoroutineScope,
    private val owner: String,
    private val name: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<PullRequestItem>>>
) : PageKeyedDataSource<String, PullRequestItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PullRequestItem>
    ) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.IO) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val issuesQuery = PullRequestsQuery.builder()
                        .owner(owner)
                        .name(name)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(issuesQuery).toDeferred()
                }.await()

                val list = mutableListOf<PullRequestItem>()
                val repository = response.data()?.repository()

                repository?.pullRequests()?.nodes()?.forEach { node ->
                    list.add(PullRequestItem.createFromRaw(node))
                }

                val pageInfo = repository?.pullRequests()?.pageInfo()

                callback.onResult(
                    list,
                    if (pageInfo?.hasPreviousPage() == true) repository.pullRequests().pageInfo().startCursor() else null,
                    if (pageInfo?.hasNextPage() == true) repository.pullRequests().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(initial = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.value = PagedResource(initial = Resource.error(e.message, null))
            }
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, PullRequestItem>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val issuesQuery = PullRequestsQuery.builder()
                        .owner(owner)
                        .name(name)
                        .perPage(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(issuesQuery).toDeferred()
                }.await()

                val list = mutableListOf<PullRequestItem>()
                val repository = response.data()?.repository()

                repository?.pullRequests()?.nodes()?.forEach { node ->
                    list.add(PullRequestItem.createFromRaw(node))
                }

                callback.onResult(
                    list,
                    if (repository?.pullRequests()?.pageInfo()?.hasNextPage() == true) repository.pullRequests().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.value = PagedResource(after = Resource.error(e.message, null))
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, PullRequestItem>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val issuesQuery = PullRequestsQuery.builder()
                        .owner(owner)
                        .name(name)
                        .perPage(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(issuesQuery).toDeferred()
                }.await()

                val list = mutableListOf<PullRequestItem>()
                val repository = response.data()?.repository()

                repository?.pullRequests()?.nodes()?.forEach { node ->
                    list.add(PullRequestItem.createFromRaw(node))
                }

                callback.onResult(
                    list,
                    if (repository?.pullRequests()?.pageInfo()?.hasPreviousPage() == true) repository.pullRequests().pageInfo().startCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(before = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.value = PagedResource(before = Resource.error(e.message, null))
            }
        }
    }

}