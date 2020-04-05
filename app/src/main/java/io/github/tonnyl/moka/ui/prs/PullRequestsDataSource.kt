package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.data.item.toNonNullPullRequestItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryPullRequests
import timber.log.Timber

class PullRequestsDataSource(
    private val owner: String,
    private val name: String,
    private val loadStatusLiveData: MutableLiveData<Resource<List<PullRequestItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<PullRequestItem>>>
) : PageKeyedDataSource<String, PullRequestItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, PullRequestItem>
    ) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(Resource.loading(null))

        try {
            val response = queryPullRequests(
                owner = owner,
                name = name,
                perPage = params.requestedLoadSize
            )

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository

            repository?.pullRequests?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.toNonNullPullRequestItem())
                }
            }

            val pageInfo = repository?.pullRequests?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                pageInfo.checkedStartCursor,
                pageInfo.checkedEndCursor
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
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = queryPullRequests(
                owner = owner,
                name = name,
                perPage = params.requestedLoadSize,
                after = params.key
            )

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository

            repository?.pullRequests?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.toNonNullPullRequestItem())
                }
            }

            callback.onResult(
                list,
                repository?.pullRequests?.pageInfo?.fragments?.pageInfo.checkedEndCursor
            )

            retry = null

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
        callback: LoadCallback<String, PullRequestItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = queryPullRequests(
                owner = owner,
                name = name,
                perPage = params.requestedLoadSize,
                before = params.key
            )

            val list = mutableListOf<PullRequestItem>()
            val repository = response.data()?.repository

            repository?.pullRequests?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.toNonNullPullRequestItem())
                }
            }

            callback.onResult(
                list,
                repository?.pullRequests?.pageInfo?.fragments?.pageInfo.checkedStartCursor
            )

            retry = null

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

}