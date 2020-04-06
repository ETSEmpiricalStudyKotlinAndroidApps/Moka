package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedOrganizationItem
import io.github.tonnyl.moka.data.item.toNonNullSearchedUserItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.querySearchUsers
import io.github.tonnyl.moka.queries.SearchUsersQuery
import io.github.tonnyl.moka.ui.*

class SearchedUsersItemDataSource(
    var keywords: String,
    override val initial: MutableLiveData<Resource<List<SearchedUserOrOrgItem>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>
) : PageKeyedDataSourceWithLoadState<SearchedUserOrOrgItem>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<SearchedUserOrOrgItem> {
        val response = querySearchUsers(
            queryWords = keywords,
            first = params.requestedLoadSize
        )

        val list = mutableListOf<SearchedUserOrOrgItem>()
        val search = response.data()?.search

        search?.nodes?.forEach { node ->
            node?.let {
                initSearchedUserOrOrgItemWithRawData(node)?.let {
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

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<SearchedUserOrOrgItem> {
        val response = querySearchUsers(
            queryWords = keywords,
            first = params.requestedLoadSize,
            after = params.key
        )

        val list = mutableListOf<SearchedUserOrOrgItem>()
        val search = response.data()?.search

        search?.nodes?.forEach { node ->
            node?.let {
                initSearchedUserOrOrgItemWithRawData(node)?.let {
                    list.add(it)
                }
            }
        }

        return AfterLoadResponse(
            list,
            NextPageKey(search?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<SearchedUserOrOrgItem> {
        val response = querySearchUsers(
            queryWords = keywords,
            first = params.requestedLoadSize,
            before = params.key
        )

        val list = mutableListOf<SearchedUserOrOrgItem>()
        val search = response.data()?.search

        search?.nodes?.forEach { node ->
            node?.let {
                initSearchedUserOrOrgItemWithRawData(node)?.let {
                    list.add(it)
                }
            }
        }

        return BeforeLoadResponse(
            list,
            PreviousPageKey(search?.pageInfo?.fragments?.pageInfo.checkedStartCursor)
        )
    }

    private fun initSearchedUserOrOrgItemWithRawData(node: SearchUsersQuery.Node): SearchedUserOrOrgItem? {
        return when {
            node.fragments.userListItemFragment != null -> {
                node.fragments.userListItemFragment.toNonNullSearchedUserItem()
            }
            node.fragments.organizationListItemFragment != null -> {
                node.fragments.organizationListItemFragment.toNonNullSearchedOrganizationItem()
            }
            else -> {
                null
            }
        }
    }

}