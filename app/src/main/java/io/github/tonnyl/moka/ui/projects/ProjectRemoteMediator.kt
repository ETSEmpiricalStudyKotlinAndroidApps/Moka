package io.github.tonnyl.moka.ui.projects

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import io.github.tonnyl.moka.data.RemoteKeys
import io.github.tonnyl.moka.data.extension.checkedEndCursor
import io.github.tonnyl.moka.data.extension.checkedStartCursor
import io.github.tonnyl.moka.data.item.Project
import io.github.tonnyl.moka.data.item.toNonNullProject
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.queries.queryUsersProjects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class ProjectRemoteMediator(
    private val login: String,
    private val isMyself: Boolean,
    private val database: MokaDataBase,
    private val initialLoadStatus: MutableLiveData<Resource<List<Project>>>
) : RemoteMediator<Int, Project>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Project>
    ): MediatorResult {
        return withContext(Dispatchers.IO) {
            try {
                when (loadType) {
                    LoadType.REFRESH -> {
                        initialLoadStatus.postValue(Resource.loading(null))

                        val response = queryUsersProjects(
                            owner = login,
                            perPage = state.config.initialLoadSize
                        )

                        val user = response.data()?.user
                        val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

                        val projects = user?.projects?.nodes?.mapNotNull {
                            it?.fragments?.project?.toNonNullProject()
                        } ?: emptyList()
                        val remoteKeys = projects.map {
                            RemoteKeys(
                                RemoteKeys.PROJECT_PREFIX + it.id,
                                prev = pageInfo.checkedStartCursor,
                                next = pageInfo.checkedEndCursor
                            )
                        }

                        if (isMyself) {
                            database.withTransaction {
                                database.projectsDao().deleteAll()
                                database.remoteKeysDao()
                                    .clearRemoteKeys("${RemoteKeys.PROJECT_PREFIX}%")

                                database.projectsDao().insertAll(projects)
                                database.remoteKeysDao().insertAll(remoteKeys)
                            }
                        }

                        MediatorResult.Success(pageInfo.checkedEndCursor.isNullOrEmpty()).also {
                            initialLoadStatus.postValue(
                                Resource.success(
                                    database.projectsDao().projectListByUpdatedAt()
                                )
                            )
                        }
                    }
                    LoadType.PREPEND -> {
                        val firstItemId = state.firstItemOrNull()?.id
                        if (firstItemId.isNullOrEmpty()) {
                            MediatorResult.Success(true)
                        } else {
                            val prev = database.remoteKeysDao()
                                .remoteKeysId(RemoteKeys.PROJECT_PREFIX + firstItemId)
                                ?.prev
                            if (prev.isNullOrEmpty()) {
                                MediatorResult.Success(true)
                            } else {
                                val user = queryUsersProjects(
                                    owner = login,
                                    before = prev,
                                    perPage = state.config.pageSize
                                ).data()?.user

                                val projects = user?.projects?.nodes?.mapNotNull {
                                    it?.fragments?.project?.toNonNullProject()
                                } ?: emptyList()

                                val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

                                val remoteKeys = projects.map {
                                    RemoteKeys(
                                        RemoteKeys.PROJECT_PREFIX + it.id,
                                        pageInfo?.startCursor,
                                        pageInfo?.endCursor
                                    )
                                }

                                if (isMyself) {
                                    database.withTransaction {
                                        database.projectsDao().insertAll(projects)
                                        database.remoteKeysDao().insertAll(remoteKeys)
                                    }
                                }

                                MediatorResult.Success(pageInfo?.hasPreviousPage != false)
                            }
                        }
                    }
                    LoadType.APPEND -> {
                        val lastItemId = state.lastItemOrNull()?.id
                        if (lastItemId.isNullOrEmpty()) {
                            MediatorResult.Success(true)
                        } else {
                            val next = database.remoteKeysDao()
                                .remoteKeysId(RemoteKeys.EVENT_PREFIX + lastItemId)
                                ?.next
                            if (next.isNullOrEmpty()) {
                                MediatorResult.Success(true)
                            } else {
                                val user = queryUsersProjects(
                                    owner = login,
                                    after = next,
                                    perPage = state.config.pageSize
                                ).data()?.user

                                val projects = user?.projects?.nodes?.mapNotNull {
                                    it?.fragments?.project?.toNonNullProject()
                                } ?: emptyList()

                                val pageInfo = user?.projects?.pageInfo?.fragments?.pageInfo

                                val remoteKeys = projects.map {
                                    RemoteKeys(
                                        RemoteKeys.PROJECT_PREFIX + it.id,
                                        pageInfo?.startCursor,
                                        pageInfo?.endCursor
                                    )
                                }

                                if (isMyself) {
                                    database.withTransaction {
                                        database.projectsDao().insertAll(projects)
                                        database.remoteKeysDao().insertAll(remoteKeys)
                                    }
                                }

                                MediatorResult.Success(pageInfo?.hasNextPage != false)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                MediatorResult.Error(e).also {
                    if (loadType == LoadType.REFRESH) {
                        initialLoadStatus.postValue(
                            Resource.error(
                                e.message,
                                database.projectsDao().projectListByUpdatedAt()
                            )
                        )
                    }
                }
            }
        }
    }

}