package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.data.item.toNonNullIssueItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryIssues
import timber.log.Timber

class IssuesDataSource(
    private val owner: String,
    private val name: String,
    private val initialLoadStatus: MutableLiveData<Resource<List<IssueItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<IssueItem>>>
) : PageKeyedDataSource<String, IssueItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, IssueItem>
    ) {
        Timber.d("loadInitial")

        initialLoadStatus.postValue(Resource.loading(null))
        try {
            val response = queryIssues(
                owner = owner,
                name = name,
                perPage = params.requestedLoadSize
            )

            val list = mutableListOf<IssueItem>()
            val repository = response.data()?.repository

            repository?.issues?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.toNonNullIssueItem())
                }
            }

            val pageInfo = repository?.issues?.pageInfo?.fragments?.pageInfo

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

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = queryIssues(
                owner = owner,
                name = name,
                perPage = params.requestedLoadSize,
                after = params.key
            )

            val list = mutableListOf<IssueItem>()
            val repository = response.data()?.repository

            repository?.issues?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.toNonNullIssueItem())
                }
            }

            callback.onResult(
                list,
                repository?.issues?.pageInfo?.fragments?.pageInfo.checkedEndCursor
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

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, IssueItem>) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = queryIssues(
                owner = owner,
                name = name,
                perPage = params.requestedLoadSize,
                before = params.key
            )

            val list = mutableListOf<IssueItem>()
            val repository = response.data()?.repository

            repository?.issues?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.toNonNullIssueItem())
                }
            }

            val pageInfo = repository?.issues?.pageInfo?.fragments?.pageInfo

            callback.onResult(
                list,
                pageInfo.checkedStartCursor
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