package io.github.tonnyl.moka.ui.repositories

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.RepositoryItem
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.toNonNullRepositoryItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryOwnedRepositories
import io.github.tonnyl.moka.ui.*

class OwnedRepositoriesDataSource(
    private val login: String,
    override val initial: MutableLiveData<Resource<List<RepositoryItem>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<RepositoryItem>>>
) : PageKeyedDataSourceWithLoadState<RepositoryItem>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<RepositoryItem> {
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

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<RepositoryItem> {
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

        return AfterLoadResponse(
            list,
            NextPageKey(user?.repositories?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )

    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<RepositoryItem> {
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

        return BeforeLoadResponse(
            list,
            PreviousPageKey(user?.repositories?.pageInfo?.fragments?.pageInfo.checkedStartCursor)
        )
    }

}