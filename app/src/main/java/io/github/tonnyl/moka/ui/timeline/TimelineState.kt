package io.github.tonnyl.moka.ui.timeline

import androidx.paging.PagedList
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.tonnyl.moka.data.Event

data class TimelineState(
        val eventRequest: Async<PagedList<Event>?> = Uninitialized,
        var isInitialLoading: Boolean = true
) : MvRxState