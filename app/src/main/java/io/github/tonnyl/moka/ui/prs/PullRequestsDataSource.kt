package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.PullRequestsQuery
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.network.GraphQLClient
import io.github.tonnyl.moka.network.PagedResource2
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.util.execute
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class PullRequestsDataSource(
    private val owner: String,
    private val name: String,
    private val loadStatusLiveData: MutableLiveData<Resource<List<PullRequestItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource2<List<PullRequestItem>>>
) : PageKeyedDataSource<String, PullRequestItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PullRequestItem>
    ) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(Resource.loading(null))

        try {
            val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(issuesQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository()

            repository?.pullRequests()?.nodes()?.forEach { node ->
                list.add(PullRequestItem.createFromRaw(node))
            }

            val pageInfo = repository?.pullRequests()?.pageInfo()

            callback.onResult(
                list,
                if (pageInfo?.hasPreviousPage() == true) {
                    repository.pullRequests().pageInfo().startCursor()
                } else {
                    null
                },
                if (pageInfo?.hasNextPage() == true) {
                    repository.pullRequests().pageInfo().endCursor()
                } else {
                    null
                }
            )

            retry = null

            loadStatusLiveData.postValue(Resource.success(list))
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadInitial(params, callback)
            }

            loadStatusLiveData.postValue(Resource.error(e.message, null))
        }
    }

    override fun loadAfter(
        params: LoadParams<String>,
        callback: LoadCallback<String, PullRequestItem>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .after(params.key)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(issuesQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository()

            repository?.pullRequests()?.nodes()?.forEach { node ->
                list.add(PullRequestItem.createFromRaw(node))
            }

            callback.onResult(
                list,
                if (repository?.pullRequests()?.pageInfo()?.hasNextPage() == true) {
                    repository.pullRequests().pageInfo().endCursor()
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadAfter(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.AFTER, Resource.error(e.message, null))
            )
        }
    }

    override fun loadBefore(
        params: LoadParams<String>,
        callback: LoadCallback<String, PullRequestItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource2(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val issuesQuery = PullRequestsQuery.builder()
                .owner(owner)
                .name(name)
                .perPage(params.requestedLoadSize)
                .before(params.key)
                .build()

            val response = runBlocking {
                GraphQLClient.apolloClient
                    .query(issuesQuery)
                    .execute()
            }

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository()

            repository?.pullRequests()?.nodes()?.forEach { node ->
                list.add(PullRequestItem.createFromRaw(node))
            }

            callback.onResult(
                list,
                if (repository?.pullRequests()?.pageInfo()?.hasPreviousPage() == true) {
                    repository.pullRequests().pageInfo().startCursor()
                } else {
                    null
                }
            )

            retry = null

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.success(list))
            )
        } catch (e: Exception) {
            Timber.e(e)

            retry = {
                loadBefore(params, callback)
            }

            pagedLoadStatus.postValue(
                PagedResource2(PagedResourceDirection.BEFORE, Resource.error(e.message, null))
            )
        }
    }

}