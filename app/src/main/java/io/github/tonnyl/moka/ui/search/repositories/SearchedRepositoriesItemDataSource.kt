package io.github.tonnyl.moka.ui.search.repositories

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedRepositoryItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.querySearchRepositories
import io.github.tonnyl.moka.queries.SearchRepositoriesQuery
import io.github.tonnyl.moka.ui.*

class SearchedRepositoriesItemDataSource(
    var keywords: String,
    override val initial: MutableLiveData<Resource<List<SearchedRepositoryItem>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<SearchedRepositoryItem>>>
) : PageKeyedDataSourceWithLoadState<SearchedRepositoryItem>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<SearchedRepositoryItem> {
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

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<SearchedRepositoryItem> {
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

        return AfterLoadResponse(
            list,
            NextPageKey(search?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<SearchedRepositoryItem> {
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

        return BeforeLoadResponse(
            list,
            PreviousPageKey(search?.pageInfo?.fragments?.pageInfo.checkedStartCursor)
        )
    }

    private fun convertRawDataToSearchedRepositoryItem(node: SearchRepositoriesQuery.Node): SearchedRepositoryItem? {
        return node.fragments.repositoryListItemFragment?.toNonNullSearchedRepositoryItem()
    }

}