package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.SearchRepositoriesQuery
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchedRepositoriesItemDataSource(
    private val coroutineScope: CoroutineScope,
    var keywords: String,
    val loadStatusLiveData: MutableLiveData<PagedResource<List<SearchedRepositoryItem>>>
) : PageKeyedDataSource<String, SearchedRepositoryItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadInitial keywords: $keywords")

        coroutineScope.launch(Dispatchers.Main) {
            if (keywords.isEmpty()) {
                loadStatusLiveData.value = PagedResource()

                return@launch
            }

            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val userQuery = SearchRepositoriesQuery.builder()
                        .queryWords(keywords)
                        .first(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(userQuery).toDeferred()
                }.await()

                val list = mutableListOf<SearchedRepositoryItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(convertRawDataToSearchedRepositoryItem(node))
                }

                val pageInfo = search?.pageInfo()
                callback.onResult(
                    list,
                    if (pageInfo?.hasPreviousPage() == true) pageInfo.startCursor() else null,
                    if (pageInfo?.hasNextPage() == true) pageInfo.endCursor() else null
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchedRepositoryItem>) {
        Timber.d("loadAfter keywords: $keywords")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val searchUserQuery = SearchRepositoriesQuery.builder()
                        .queryWords(keywords)
                        .first(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(searchUserQuery).toDeferred()
                }.await()

                val list = mutableListOf<SearchedRepositoryItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(convertRawDataToSearchedRepositoryItem(node))
                }

                retry = null

                callback.onResult(
                    list,
                    if (search?.pageInfo()?.hasNextPage() == true) search.pageInfo().endCursor() else null
                )

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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchedRepositoryItem>) {
        Timber.d("loadBefore keywords: $keywords")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val searchUserQuery = SearchRepositoriesQuery.builder()
                        .queryWords(keywords)
                        .first(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(searchUserQuery).toDeferred()
                }.await()

                val list = mutableListOf<SearchedRepositoryItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(convertRawDataToSearchedRepositoryItem(node))
                }

                retry = null

                callback.onResult(
                    list,
                    if (search?.pageInfo()?.hasPreviousPage() == true) search.pageInfo().startCursor() else null
                )

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

    private fun convertRawDataToSearchedRepositoryItem(node: SearchRepositoriesQuery.Node): SearchedRepositoryItem =
        SearchedRepositoryItem.createFromRaw(node.fragments().repositoryFragment())!!

}