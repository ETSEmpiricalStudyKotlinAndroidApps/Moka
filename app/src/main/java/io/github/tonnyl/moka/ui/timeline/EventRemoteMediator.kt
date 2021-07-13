package io.github.tonnyl.moka.ui.timeline

import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Error
import androidx.paging.RemoteMediator.MediatorResult.Success
import androidx.room.withTransaction
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.RemoteKeys
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.api.EventApi
import io.github.tonnyl.moka.util.PageLinks
import io.github.tonnyl.moka.util.json
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import timber.log.Timber

@ExperimentalPagingApi
class EventRemoteMediator(
    private val login: String,
    private val eventApi: EventApi,
    private val database: MokaDataBase,
    private val isNeedDisplayPlaceholder: MutableLiveData<Boolean>
) : RemoteMediator<Int, Event>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Event>
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
                        val response = eventApi.listPublicEventThatAUserHasReceived(
                            login,
                            page = 1,
                            perPage = state.config.initialLoadSize
                        )

                        val events =
                            json.decodeFromString<List<Event>>(string = response.readText())
                        val pl = PageLinks(response)
                        val keys = events.map {
                            RemoteKeys(RemoteKeys.EVENT_PREFIX + it.id, pl.prev, pl.next)
                        }

                        database.withTransaction {
                            database.remoteKeysDao()
                                .clearRemoteKeys("${RemoteKeys.EVENT_PREFIX}%")
                            database.eventDao().deleteAll()

                            database.eventDao().insertAll(events)
                            database.remoteKeysDao().insertAll(keys)
                        }

                        Success(endOfPaginationReached = pl.next.isNullOrEmpty())
                    }
                    LoadType.PREPEND -> {
                        val firstItemId = state.firstItemOrNull()?.id
                        if (firstItemId.isNullOrEmpty()) {
                            Success(endOfPaginationReached = true)
                        } else {
                            val prev = database.remoteKeysDao()
                                .remoteKeysId(RemoteKeys.EVENT_PREFIX + firstItemId)
                                ?.prev
                            if (prev.isNullOrEmpty()) {
                                Success(endOfPaginationReached = true)
                            } else {
                                val response =
                                    eventApi.listPublicEventThatAUserHasReceivedByUrl(prev)
                                val events =
                                    json.decodeFromString<List<Event>>(string = response.readText())
                                val pl = PageLinks(response)
                                val keys = events.map {
                                    RemoteKeys(RemoteKeys.EVENT_PREFIX + it.id, pl.prev, pl.next)
                                }

                                database.withTransaction {
                                    database.eventDao().insertAll(events)
                                    database.remoteKeysDao().insertAll(keys)
                                }

                                Success(endOfPaginationReached = pl.prev.isNullOrEmpty())
                            }
                        }
                    }
                    LoadType.APPEND -> {
                        val lastItemId = state.lastItemOrNull()?.id
                        if (lastItemId.isNullOrEmpty()) {
                            Success(endOfPaginationReached = true)
                        } else {
                            val next = database.remoteKeysDao()
                                .remoteKeysId(RemoteKeys.EVENT_PREFIX + lastItemId)
                                ?.next
                            if (next.isNullOrEmpty()) {
                                Success(endOfPaginationReached = true)
                            } else {
                                val response =
                                    eventApi.listPublicEventThatAUserHasReceivedByUrl(next)
                                val events =
                                    json.decodeFromString<List<Event>>(string = response.readText())
                                val pl = PageLinks(response)
                                val keys = events.map {
                                    RemoteKeys(RemoteKeys.EVENT_PREFIX + it.id, pl.prev, pl.next)
                                }

                                database.withTransaction {
                                    database.eventDao().insertAll(events)
                                    database.remoteKeysDao().insertAll(keys)
                                }

                                Success(endOfPaginationReached = pl.next.isNullOrEmpty())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                Error(e)
            } finally {
                updateIsNeedDisplayPlaceholderIfNeeded.invoke()
            }
        }
    }

}