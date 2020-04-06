package io.github.tonnyl.moka.ui.prs

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.PullRequestItem
import io.github.tonnyl.moka.data.item.toNonNullPullRequestItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryPullRequests
import io.github.tonnyl.moka.ui.*

class PullRequestsDataSource(
    private val owner: String,
    private val name: String,
    override val initial: MutableLiveData<Resource<List<PullRequestItem>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<PullRequestItem>>>
) : PageKeyedDataSourceWithLoadState<PullRequestItem>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<PullRequestItem> {
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

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<PullRequestItem> {
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

        return AfterLoadResponse(
            list,
            NextPageKey(repository?.pullRequests?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<PullRequestItem> {
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

        return BeforeLoadResponse(
            list,
            PreviousPageKey(repository?.pullRequests?.pageInfo?.fragments?.pageInfo.checkedStartCursor)
        )
    }

}