package io.github.tonnyl.moka.ui.search.users

import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.SearchUserQuery
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import timber.log.Timber
import java.io.IOException

class SearchedUsersItemDataSource(
        var keywords: String
) : PageKeyedDataSource<String, SearchedUserOrOrgItem>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadInitial keywords: $keywords")

        if (keywords.isEmpty()) {
            return
        }

        val userQuery = SearchUserQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(userQuery)

        try {
            val response = Rx2Apollo.from(call)
                    .blockingFirst()

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

        } catch (ioe: IOException) {
            Timber.e(ioe)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadAfter keywords: $keywords")

        val searchUserQuery = SearchUserQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(searchUserQuery)
                .enqueue(object : ApolloCall.Callback<SearchUserQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e)
                    }

                    override fun onResponse(response: Response<SearchUserQuery.Data>) {
                        val list = mutableListOf<SearchedUserOrOrgItem>()
                        val search = response.data()?.search()

                        search?.nodes()?.forEach { node ->
                            list.add(initSearchedUserOrOrgItemWithRawData(node))
                        }

                        callback.onResult(list, if (search?.pageInfo()?.hasNextPage() == true) search.pageInfo().endCursor() else null)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadBefore keywords: $keywords")

        val searchUserQuery = SearchUserQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .before(params.key)
                .build()

        NetworkClient.apolloClient
                .query(searchUserQuery)
                .enqueue(object : ApolloCall.Callback<SearchUserQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e)
                    }

                    override fun onResponse(response: Response<SearchUserQuery.Data>) {
                        val list = mutableListOf<SearchedUserOrOrgItem>()
                        val search = response.data()?.search()

                        search?.nodes()?.forEach { node ->
                            list.add(initSearchedUserOrOrgItemWithRawData(node))
                        }

                        callback.onResult(list, if (search?.pageInfo()?.hasPreviousPage() == true) search.pageInfo().startCursor() else null)
                    }

                })
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