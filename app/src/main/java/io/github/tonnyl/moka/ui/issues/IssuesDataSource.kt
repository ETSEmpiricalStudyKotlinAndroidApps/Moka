package io.github.tonnyl.moka.ui.issues

import androidx.lifecycle.MutableLiveData
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.IssueItem
import io.github.tonnyl.moka.data.item.toNonNullIssueItem
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryIssues
import io.github.tonnyl.moka.ui.*

class IssuesDataSource(
    private val owner: String,
    private val name: String,
    override val initial: MutableLiveData<Resource<List<IssueItem>>>,
    override val previousOrNext: MutableLiveData<PagedResource<List<IssueItem>>>
) : PageKeyedDataSourceWithLoadState<IssueItem>() {

    override fun doLoadInitial(params: LoadInitialParams<String>): InitialLoadResponse<IssueItem> {
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

        return InitialLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor),
            NextPageKey(pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadAfter(params: LoadParams<String>): AfterLoadResponse<IssueItem> {
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

        return AfterLoadResponse(
            list,
            NextPageKey(repository?.issues?.pageInfo?.fragments?.pageInfo.checkedEndCursor)
        )
    }

    override fun doLoadBefore(params: LoadParams<String>): BeforeLoadResponse<IssueItem> {
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

        return BeforeLoadResponse(
            list,
            PreviousPageKey(pageInfo.checkedStartCursor)
        )
    }

}