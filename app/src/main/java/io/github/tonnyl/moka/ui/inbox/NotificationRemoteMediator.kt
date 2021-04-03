package io.github.tonnyl.moka.ui.inbox

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.RemoteKeys
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.util.PageLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class NotificationRemoteMediator(
    private val notificationsService: NotificationsService,
    private val database: MokaDataBase
) : RemoteMediator<Int, Notification>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Notification>
    ): MediatorResult {
        return withContext(Dispatchers.IO) {
            try {
                when (loadType) {
                    LoadType.REFRESH -> {
                        val response = notificationsService.listNotifications(
                            true,
                            page = 1,
                            perPage = state.config.initialLoadSize
                        ).execute()

                        val notifications = response.body() ?: emptyList()
                        val pl = PageLinks(response)
                        val keys = notifications.map {
                            RemoteKeys(RemoteKeys.NOTIFICATION_PREFIX + it.id, pl.prev, pl.next)
                        }

                        database.withTransaction {
                            database.remoteKeysDao()
                                .clearRemoteKeys("${RemoteKeys.NOTIFICATION_PREFIX}%")
                            database.notificationsDao().deleteAll()

                            database.notificationsDao().insertAll(notifications)
                            database.remoteKeysDao().insertAll(keys)
                        }

                        MediatorResult.Success(endOfPaginationReached = pl.next.isNullOrEmpty())
                    }
                    LoadType.PREPEND -> {
                        val firstItemId = state.firstItemOrNull()?.id
                        if (firstItemId.isNullOrEmpty()) {
                            MediatorResult.Success(endOfPaginationReached = true)
                        } else {
                            val prev = database.remoteKeysDao()
                                .remoteKeysId(RemoteKeys.NOTIFICATION_PREFIX + firstItemId)
                                ?.prev
                            if (prev.isNullOrEmpty()) {
                                MediatorResult.Success(endOfPaginationReached = true)
                            } else {
                                val response = notificationsService.listNotificationsByUrl(prev)
                                    .execute()
                                val notifications = response.body() ?: emptyList()
                                val pl = PageLinks(response)
                                val keys = notifications.map {
                                    RemoteKeys(
                                        RemoteKeys.NOTIFICATION_PREFIX + it.id,
                                        pl.prev,
                                        pl.next
                                    )
                                }

                                database.withTransaction {
                                    database.notificationsDao().insertAll(notifications)
                                    database.remoteKeysDao().insertAll(keys)
                                }

                                MediatorResult.Success(endOfPaginationReached = pl.prev.isNullOrEmpty())
                            }
                        }
                    }
                    LoadType.APPEND -> {
                        val firstItemId = state.lastItemOrNull()?.id
                        if (firstItemId.isNullOrEmpty()) {
                            MediatorResult.Success(endOfPaginationReached = true)
                        } else {
                            val next = database.remoteKeysDao()
                                .remoteKeysId(RemoteKeys.NOTIFICATION_PREFIX + firstItemId)
                                ?.next
                            if (next.isNullOrEmpty()) {
                                MediatorResult.Success(endOfPaginationReached = true)
                            } else {
                                val response = notificationsService.listNotificationsByUrl(next)
                                    .execute()
                                val notifications = response.body() ?: emptyList()
                                val pl = PageLinks(response)
                                val keys = notifications.map {
                                    RemoteKeys(
                                        RemoteKeys.NOTIFICATION_PREFIX + it.id,
                                        pl.prev,
                                        pl.next
                                    )
                                }

                                database.withTransaction {
                                    database.notificationsDao().insertAll(notifications)
                                    database.remoteKeysDao().insertAll(keys)
                                }

                                MediatorResult.Success(endOfPaginationReached = pl.next.isNullOrEmpty())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                MediatorResult.Error(e)
            }
        }
    }

}