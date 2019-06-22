package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.apollographql.apollo.coroutines.toDeferred
import io.github.tonnyl.moka.NetworkClient
import io.github.tonnyl.moka.OwnedRepositoriesQuery
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.data.RepositoryAbstract
import io.github.tonnyl.moka.network.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class OwnedRepositoriesDataSource(
    private val coroutineScope: CoroutineScope,
    private val login: String,
    private val loadStatusLiveData: MutableLiveData<PagedResource<List<RepositoryAbstract>>>
) : PageKeyedDataSource<String, RepositoryAbstract>() {

    var retry: (() -> Any)? = null

    override fun loadInitial(
        params: LoadInitialParams<String>,
        callback: LoadInitialCallback<String, RepositoryAbstract>
    ) {
        Timber.d("loadInitial")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(initial = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val repositoriesQuery = OwnedRepositoriesQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .build()

                    NetworkClient.apolloClient.query(repositoriesQuery).toDeferred()
                }.await()

                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.repositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                }

                val pageInfo = user?.repositories()?.pageInfo()

                callback.onResult(
                    list,
                    if (pageInfo?.hasPreviousPage() == true) user.repositories().pageInfo().startCursor() else null,
                    if (pageInfo?.hasNextPage() == true) user.repositories().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(initial = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadInitial(params, callback)
                }

                loadStatusLiveData.value = PagedResource(initial = Resource.error(e.message, null))
            }

        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadAfter")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(after = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val repositoriesQuery = OwnedRepositoriesQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .after(params.key)
                        .build()

                    NetworkClient.apolloClient.query(repositoriesQuery).toDeferred()
                }.await()

                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.repositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                }

                callback.onResult(
                    list,
                    if (user?.repositories()?.pageInfo()?.hasNextPage() == true) user.repositories().pageInfo().endCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(after = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadAfter(params, callback)
                }

                loadStatusLiveData.value = PagedResource(after = Resource.error(e.message, null))
            }
        }
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, RepositoryAbstract>) {
        Timber.d("loadBefore")

        coroutineScope.launch(Dispatchers.Main) {
            loadStatusLiveData.value = PagedResource(before = Resource.loading(null))

            try {
                val response = withContext(Dispatchers.IO) {
                    val repositoriesQuery = OwnedRepositoriesQuery.builder()
                        .login(login)
                        .perPage(params.requestedLoadSize)
                        .before(params.key)
                        .build()

                    NetworkClient.apolloClient.query(repositoriesQuery).toDeferred()
                }.await()

                val list = mutableListOf<RepositoryAbstract>()
                val user = response.data()?.user()

                user?.repositories()?.nodes()?.forEach { node ->
                    list.add(RepositoryAbstract.createFromOwnedRepositoryDataNode(node))
                }

                callback.onResult(
                    list,
                    if (user?.repositories()?.pageInfo()?.hasPreviousPage() == true) user.repositories().pageInfo().startCursor() else null
                )

                retry = null

                loadStatusLiveData.value = PagedResource(before = Resource.success(list))
            } catch (e: Exception) {
                Timber.e(e)

                retry = {
                    loadBefore(params, callback)
                }

                loadStatusLiveData.value = PagedResource(before = Resource.error(e.message, null))
            }
        }
    }

}