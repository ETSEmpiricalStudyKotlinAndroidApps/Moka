package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.IssuesQuery
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class IssuesDataSource(
    private val coroutineScope: CoroutineScope,
    private val owner: String,
    private val name: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<IssueItem>>>
) : PageKeyedDataSource<String, IssueItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, IssueItem>) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {

                    val issuesQuery = IssuesQuery.builder()
                        .owner(owner)
                        .name(name)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(issuesQuery).toDeferred()
                }.await()

                val list = mutableListOf<IssueItem>()
                val repository = response.data()?.repository()

                repository?.issues()?.nodes()?.forEach { node ->
                    list.add(IssueItem.createFromRaw(node))
                }

                val pageInfo = repository?.issues()?.pageInfo()

                callback.onResult(
                    list,
                    if (pageInfo?.hasPreviousPage() == true) {
                        repository.issues().pageInfo().startCursor()
                    } else {
                        null
                    },
                    if (pageInfo?.hasNextPage() == true) {
                        repository.issues().pageInfo().endCursor()
                    } else {
                        null
                    }
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val issuesQuery = IssuesQuery.builder()
                        .owner(owner)
                        .name(name)
                        .perPage(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(issuesQuery).toDeferred()
                }.await()

                val list = mutableListOf<IssueItem>()
                val repository = response.data()?.repository()

                repository?.issues()?.nodes()?.forEach { node ->
                    list.add(IssueItem.createFromRaw(node))
                }

                callback.onResult(
                    list,
                    if (repository?.issues()?.pageInfo()?.hasNextPage() == true) {
                        repository.issues().pageInfo().endCursor()
                    } else {
                        null
                    }
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val issuesQuery = IssuesQuery.builder()
                        .owner(owner)
                        .name(name)
                        .perPage(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(issuesQuery).toDeferred()
                }.await()

                val list = mutableListOf<IssueItem>()
                val repository = response.data()?.repository()

                repository?.issues()?.nodes()?.forEach { node ->
                    list.add(IssueItem.createFromRaw(node))
                }

                callback.onResult(
                    list,
                    if (repository?.issues()?.pageInfo()?.hasPreviousPage() == true) {
                        repository.issues().pageInfo().startCursor()
                    } else {
                        null
                    }
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