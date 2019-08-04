package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.SearchUserQuery
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.NetworkClient
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class SearchedUsersItemDataSource(
    val coroutineScope: CoroutineScope,
    var keywords: String,
    val loadStatusLiveData: MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>
) : PageKeyedDataSource<String, SearchedUserOrOrgItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, SearchedUserOrOrgItem>
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
                    val userQuery = SearchUserQuery.builder()
                        .queryWords(keywords)
                        .first(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(userQuery).toDeferred()
                }.await()

                val list = mutableListOf<SearchedUserOrOrgItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(initSearchedUserOrOrgItemWithRawData(node))
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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadAfter keywords: $keywords")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val searchUserQuery = SearchUserQuery.builder()
                        .queryWords(keywords)
                        .first(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(searchUserQuery).toDeferred()
                }.await()

                val list = mutableListOf<SearchedUserOrOrgItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(initSearchedUserOrOrgItemWithRawData(node))
                }

                callback.onResult(
                    list,
                    if (search?.pageInfo()?.hasNextPage() == true) search.pageInfo().endCursor() else null
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadBefore keywords: $keywords")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val searchUserQuery = SearchUserQuery.builder()
                        .queryWords(keywords)
                        .first(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(searchUserQuery).toDeferred()
                }.await()

                val list = mutableListOf<SearchedUserOrOrgItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(initSearchedUserOrOrgItemWithRawData(node))
                }

                callback.onResult(
                    list,
                    if (search?.pageInfo()?.hasPreviousPage() == true) search.pageInfo().startCursor() else null
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

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUserQuery.Node): SearchedUserOrOrgItem = when {
        node.fragments().userFragment() != null -> {
            SearchedUserItem.createFromRaw(node.fragments().userFragment())!!
        }
        node.fragments().orgFragment() != null -> {
            SearchedOrganizationItem.createFromRaw(node.fragments().orgFragment())!!
        }
        else -> {
            SearchedOrganizationItem.createFromRaw(node.fragments().orgFragment())!!
        }
    }

}