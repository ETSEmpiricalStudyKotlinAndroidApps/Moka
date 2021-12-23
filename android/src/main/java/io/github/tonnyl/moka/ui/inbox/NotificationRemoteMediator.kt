package io.github.tonnyl.moka.ui.inbox

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import io.ktor.client.statement.*
import io.tonnyl.moka.common.db.MokaDataBase
import io.tonnyl.moka.common.db.data.Notification
import io.tonnyl.moka.common.db.data.RemoteKeys
import io.tonnyl.moka.common.db.data.dbModel
import io.tonnyl.moka.common.network.PageLinks
import io.tonnyl.moka.common.network.api.NotificationApi
import io.tonnyl.moka.common.serialization.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import io.tonnyl.moka.common.data.Notification as SerializableNotification

@ExperimentalPagingApi
class NotificationRemoteMediator(
    private val notificationsApi: NotificationApi,
    private val database: MokaDataBase,
    private val isNeedDisplayPlaceholder: MutableLiveData<Boolean>
) : RemoteMediator<Int, Notification>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Notification>
    ): MediatorResult {
        val updateIsNeedDisplayPlaceholderIfNeeded = {
            val newValue = loadType == LoadType.REFRESH
                    && database.notificationsDao().notificationsCount() == 0
            if (newValue != isNeedDisplayPlaceholder.value) {
                isNeedDisplayPlaceholder.postValue(newValue)
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                updateIsNeedDisplayPlaceholderIfNeeded.invoke()

                when (loadType) {
                    LoadType.REFRESH -> {
                        val response = notificationsApi.listNotifications(
                            true,
                            page = 1,
                            perPage = state.config.initialLoadSize
                        )

                        val notifications =
                            json.decodeFromString<List<SerializableNotification>>(string = response.bodyAsText())
                                .map {
                                    it.dbModel
                                }
                        val pl = PageLinks(response)
                        val keys = notifications.map {
                            RemoteKeys(RemoteKeys.NOTIFICATION_PREFIX + it.id, pl.prev, pl.next)
                        }

                        database.withTransaction {
                            database.remoteKeysDao()
                                .clearRemoteKeys("${RemoteKeys.NOTIFICATION_PREFIX}%")
                            database.notificationsDao().deleteAll()

                            database.notificationsDao()
                                .insertAll(notifications)
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
                                val response = notificationsApi.listNotificationsByUrl(prev)
                                val notifications =
                                    json.decodeFromString<List<SerializableNotification>>(string = response.bodyAsText())
                                        .map {
                                            it.dbModel
                                        }
                                val pl = PageLinks(response)
                                val keys = notifications.map {
                                    RemoteKeys(
                                        RemoteKeys.NOTIFICATION_PREFIX + it.id,
                                        pl.prev,
                                        pl.next
                                    )
                                }

                                database.withTransaction {
                                    database.notificationsDao()
                                        .insertAll(notifications)
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
                                val response = notificationsApi.listNotificationsByUrl(next)
                                val notifications =
                                    json.decodeFromString<List<SerializableNotification>>(string = response.bodyAsText())
                                        .map {
                                            it.dbModel
                                        }
                                val pl = PageLinks(response)
                                val keys = notifications.map {
                                    RemoteKeys(
                                        RemoteKeys.NOTIFICATION_PREFIX + it.id,
                                        pl.prev,
                                        pl.next
                                    )
                                }

                                database.withTransaction {
                                    database.notificationsDao()
                                        .insertAll(notifications)
                                    database.remoteKeysDao().insertAll(keys)
                                }

                                MediatorResult.Success(endOfPaginationReached = pl.next.isNullOrEmpty())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                MediatorResult.Error(e)
            } finally {
                updateIsNeedDisplayPlaceholderIfNeeded.invoke()
            }
        }
    }

}