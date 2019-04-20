package io.github.tonnyl.moka.ui.search.users

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloQueryCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.SearchUserQuery
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.Resource
import timber.log.Timber

class SearchedUsersItemDataSource(
        var keywords: String,
        val loadStatusLiveData: MutableLiveData<PagedResource<List<SearchedUserOrOrgItem>>>
) : PageKeyedDataSource<String, SearchedUserOrOrgItem>() {

    var retry: (() -> Any)? = null

    private var initialCall: ApolloQueryCall<SearchUserQuery.Data>? = null
    private var afterCall: ApolloQueryCall<SearchUserQuery.Data>? = null
    private var beforeCall: ApolloQueryCall<SearchUserQuery.Data>? = null

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadInitial keywords: $keywords")

        if (keywords.isEmpty()) {
            loadStatusLiveData.postValue(PagedResource())

            return
        }

        loadStatusLiveData.postValue(PagedResource(initial = Resource.loading(null)))

        val userQuery = SearchUserQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .build()

        initialCall = NetworkClient.apolloClient
                .query(userQuery)

        initialCall?.enqueue(object : ApolloCall.Callback<SearchUserQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(initial = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<SearchUserQuery.Data>) {
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

                loadStatusLiveData.postValue(PagedResource(initial = Resource.success(list)))
            }

        })
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadAfter keywords: $keywords")

        loadStatusLiveData.postValue(PagedResource(after = Resource.loading(null)))

        val searchUserQuery = SearchUserQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .after(params.key)
                .build()

        afterCall = NetworkClient.apolloClient
                .query(searchUserQuery)

        afterCall?.enqueue(object : ApolloCall.Callback<SearchUserQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(after = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<SearchUserQuery.Data>) {
                val list = mutableListOf<SearchedUserOrOrgItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(initSearchedUserOrOrgItemWithRawData(node))
                }

                callback.onResult(list, if (search?.pageInfo()?.hasNextPage() == true) search.pageInfo().endCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(after = Resource.success(list)))
            }

        })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, SearchedUserOrOrgItem>) {
        Timber.d("loadBefore keywords: $keywords")

        loadStatusLiveData.postValue(PagedResource(before = Resource.loading(null)))

        val searchUserQuery = SearchUserQuery.builder()
                .queryWords(keywords)
                .first(params.requestedLoadSize)
                .before(params.key)
                .build()

        beforeCall = NetworkClient.apolloClient
                .query(searchUserQuery)


        beforeCall?.enqueue(object : ApolloCall.Callback<SearchUserQuery.Data>() {

            override fun onFailure(e: ApolloException) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.postValue(PagedResource(before = Resource.error(e.message, null)))
            }

            override fun onResponse(response: Response<SearchUserQuery.Data>) {
                val list = mutableListOf<SearchedUserOrOrgItem>()
                val search = response.data()?.search()

                search?.nodes()?.forEach { node ->
                    list.add(initSearchedUserOrOrgItemWithRawData(node))
                }

                callback.onResult(list, if (search?.pageInfo()?.hasPreviousPage() == true) search.pageInfo().startCursor() else null)

                retry = null

                loadStatusLiveData.postValue(PagedResource(before = Resource.success(list)))
            }

        })
    }

    override fun invalidate() {
        super.invalidate()

        fun cancelCallIfNeeded(call: ApolloCall<SearchUserQuery.Data>) {
            if (!call.isCanceled) {
                call.cancel()
            }
        }

        initialCall?.let {
            cancelCallIfNeeded(it)
        }

        beforeCall?.let {
            cancelCallIfNeeded(it)
        }

        afterCall?.let {
            cancelCallIfNeeded(it)
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