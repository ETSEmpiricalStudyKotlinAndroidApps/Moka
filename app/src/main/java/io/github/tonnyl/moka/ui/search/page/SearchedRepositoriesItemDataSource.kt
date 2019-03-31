package io.github.tonnyl.moka.ui.search.page

import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.SearchRepositoriesQuery
import io.github.tonnyl.moka.data.item.SearchedRepositoryItem
import timber.log.Timber
import java.io.IOException

class SearchedRepositoriesItemDataSource(
        var keywords: String
) : PageKeyedDataSource<String, SearchedRepositoryItem>() {

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SearchedRepositoryItem>) {
        Timber.d("loadInitial keywords: $keywords")

        if (keywords.isEmpty()) {
            return
        }

        val userQuery = SearchRepositoriesQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .build()

        val call = NetworkClient.apolloClient
                .query(userQuery)

        try {
            val response = Rx2Apollo.from(call)
                    .blockingFirst()

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

        } catch (ioe: IOException) {
            Timber.e(ioe)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchedRepositoryItem>) {
        Timber.d("loadAfter keywords: $keywords")

        val searchUserQuery = SearchRepositoriesQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .after(params.key)
                .build()

        NetworkClient.apolloClient
                .query(searchUserQuery)
                .enqueue(object : ApolloCall.Callback<SearchRepositoriesQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e)
                    }

                    override fun onResponse(response: Response<SearchRepositoriesQuery.Data>) {
                        val list = mutableListOf<SearchedRepositoryItem>()
                        val search = response.data()?.search()

                        search?.nodes()?.forEach { node ->
                            list.add(convertRawDataToSearchedRepositoryItem(node))
                        }

                        callback.onResult(list, if (search?.pageInfo()?.hasNextPage() == true) search.pageInfo().endCursor() else null)
                    }

                })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchedRepositoryItem>) {
        Timber.d("loadBefore keywords: $keywords")

        val searchUserQuery = SearchRepositoriesQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .before(params.key)
                .build()

        NetworkClient.apolloClient
                .query(searchUserQuery)
                .enqueue(object : ApolloCall.Callback<SearchRepositoriesQuery.Data>() {

                    override fun onFailure(e: ApolloException) {
                        Timber.e(e)
                    }

                    override fun onResponse(response: Response<SearchRepositoriesQuery.Data>) {
                        val list = mutableListOf<SearchedRepositoryItem>()
                        val search = response.data()?.search()

                        search?.nodes()?.forEach { node ->
                            list.add(convertRawDataToSearchedRepositoryItem(node))
                        }

                        callback.onResult(list, if (search?.pageInfo()?.hasPreviousPage() == true) search.pageInfo().startCursor() else null)
                    }

                })
    }

    private fun convertRawDataToSearchedRepositoryItem(node: SearchRepositoriesQuery.Node): SearchedRepositoryItem = SearchedRepositoryItem.createFromRaw(node.fragments().repositoryFragment())!!

}