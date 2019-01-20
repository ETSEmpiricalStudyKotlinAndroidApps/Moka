package io.github.tonnyl.moka.ui.timeline

import androidx.paging.PagedList
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import io.github.tonnyl.moka.data.Event

data class TimelineState(
        val login: String,
        val eventRequest: Async<PagedList<Event>?> = Uninitialized,
        var isInitialLoading: Boolean = true
) : MvRxState {

    /**
     * This secondary constructor will automatically called if Fragment has
     * a parcelable in its arguments at key [com.airbnb.mvrx.MvRx.KEY_ARG].
     */
    constructor(args: TimelineArgs) : this(login = args.username)

}