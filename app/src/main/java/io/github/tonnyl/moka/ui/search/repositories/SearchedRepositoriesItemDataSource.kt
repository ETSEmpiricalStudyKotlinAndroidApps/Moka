package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedRepositoryItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.querySearchRepositories
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import timber.log.Timber

class SearchedRepositoriesItemDataSource(
    var keywords: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<SearchedRepositoryItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<SearchedRepositoryItem>>>
) : PageKeyedDataSource<String, SearchedRepositoryItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadInitial keywords: $keywords")

        if (keywords.isEmpty()) {
            return
        }

        initialLoadStatus.postValue(Resource.loading(null))

        try {
            val response = querySearchRepositories(
                queryWords = keywords,
                first = params.requestedLoadSize
            )

            val list = mutableListOf<SearchedRepositoryItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    convertRawDataToSearchedRepositoryItem(node)?.let {
                        list.add(it)
                    }
                }
            }

            val pageInfo = search?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                pageInfo.checkedStartCursor,
                pageInfo.checkedEndCursor
            )

            retry = null

            initialLoadStatus.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            initialLoadStatus.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadAfter keywords: $keywords")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = querySearchRepositories(
                queryWords = keywords,
                first = params.requestedLoadSize,
                after = params.key
            )

            val list = mutableListOf<SearchedRepositoryItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    convertRawDataToSearchedRepositoryItem(node)?.let {
                        list.add(it)
                    }
                }
            }

            retry = null

            callback.onResult(
                list,
                search?.pageInfo?.fragments?.pageInfo.checkedEndCursor
            )

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, SearchedRepositoryItem>
    ) {
        Timber.d("loadBefore keywords: $keywords")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = querySearchRepositories(
                queryWords = keywords,
                first = params.requestedLoadSize,
                before = params.key
            )

            val list = mutableListOf<SearchedRepositoryItem>()
            val search = response.data()?.search

            search?.nodes?.forEach { node ->
                node?.let {
                    convertRawDataToSearchedRepositoryItem(node)?.let {
                        list.add(it)
                    }
                }
            }

            retry = null

            callback.onResult(
                list,
                search?.pageInfo?.fragments?.pageInfo.checkedStartCursor
            )

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

    private fun convertRawDataToSearchedRepositoryItem(node: SearchRepositoriesQuery.Node): SearchedRepositoryItem? {
        return node.fragments.repositoryListItemFragment?.toNonNullSearchedRepositoryItem()
    }

}