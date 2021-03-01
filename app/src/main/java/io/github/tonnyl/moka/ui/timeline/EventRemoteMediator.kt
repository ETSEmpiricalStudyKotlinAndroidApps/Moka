package io.github.tonnyl.moka.ui.timeline

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
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.util.PageLinks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalPagingApi::class)
class EventRemoteMediator(
    private var login: String,
    private val eventsService: EventsService,
    private val database: MokaDataBase
) : RemoteMediator<Int, Event>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Event>
    ): MediatorResult {
        return withContext(Dispatchers.IO) {
            try {
                when (loadType) {
                    LoadType.REFRESH -> {
                        val response = eventsService.listPublicEventThatAUserHasReceived(
                            login,
                            page = 1,
                            perPage = state.config.initialLoadSize
                        ).execute()

                        val events = response.body() ?: emptyList()
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
                                    eventsService.listPublicEventThatAUserHasReceivedByUrl(prev)
                                        .execute()
                                val events = response.body() ?: emptyList()
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
                                    eventsService.listPublicEventThatAUserHasReceivedByUrl(next)
                                        .execute()
                                val events = response.body() ?: emptyList()
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
                Error(e)
            }
        }
    }

}