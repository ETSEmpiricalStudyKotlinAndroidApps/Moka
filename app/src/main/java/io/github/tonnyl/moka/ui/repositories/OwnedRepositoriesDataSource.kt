package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.PagedResourceDirection
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryOwnedRepositories
import timber.log.Timber

class OwnedRepositoriesDataSource(
    private val login: String,
    private val loadStatusLiveData: MutableLiveData<Resource<List<RepositoryItem>>>,
    private val pagedLoadStatus: MutableLiveData<PagedResource<List<RepositoryItem>>>
) : PageKeyedDataSource<String, RepositoryItem>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, RepositoryItem>
    ) {
        Timber.d("loadInitial")

        loadStatusLiveData.postValue(Resource.loading(null))

        try {
            val response = queryOwnedRepositories(
                login = login,
                perPage = params.requestedLoadSize
            )

            val list = mutableListOf<RepositoryItem>()
            val user = response.data()?.user

            user?.repositories?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                }
            }

            val pageInfo = user?.repositories?.pageInfo?.fragments?.pageInfo

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
        callback: LoadCallback<String, RepositoryItem>
    ) {
        Timber.d("loadAfter")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.AFTER, Resource.loading(null))
        )

        try {
            val response = queryOwnedRepositories(
                login = login,
                perPage = params.requestedLoadSize,
                after = params.key
            )

            val list = mutableListOf<RepositoryItem>()
            val user = response.data()?.user

            user?.repositories?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                }
            }

            callback.onResult(
                list,
                user?.repositories?.pageInfo?.fragments?.pageInfo.checkedEndCursor
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
        callback: LoadCallback<String, RepositoryItem>
    ) {
        Timber.d("loadBefore")

        pagedLoadStatus.postValue(
            PagedResource(PagedResourceDirection.BEFORE, Resource.loading(null))
        )

        try {
            val response = queryOwnedRepositories(
                login = login,
                perPage = params.requestedLoadSize,
                before = params.key
            )

            val list = mutableListOf<RepositoryItem>()
            val user = response.data()?.user

            user?.repositories?.nodes?.forEach { node ->
                node?.let {
                    list.add(node.fragments.repositoryListItemFragment.toNonNullRepositoryItem())
                }
            }

            callback.onResult(
                list,
                user?.repositories?.pageInfo?.fragments?.pageInfo.checkedStartCursor
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