package io.github.tonnyl.moka.ui.timeline

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.service.EventsService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TimelineViewModel(
        initialState: TimelineState
) : BaseMvRxViewModel<TimelineState>(initialState) {

    private val sourceFactory: TimelineDataSourceFactory by lazy {
        TimelineDataSourceFactory(RetrofitClient.createService(EventsService::class.java, null), initialState.login)
    }

    private val pagingConfig: PagedList.Config by lazy {
        PagedList.Config.Builder()
                .setPageSize(PER_PAGE)
                .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
                .setInitialLoadSizeHint(PER_PAGE)
                .setEnablePlaceholders(false)
                .build()
    }

    companion object : MvRxViewModelFactory<TimelineViewModel, TimelineState> {

        override fun create(viewModelContext: ViewModelContext, state: TimelineState): TimelineViewModel? = TimelineViewModel(state)

    }

    init {
        refreshEventsData()
    }

    fun refreshEventsData() = withState { state ->
        if (state.eventRequest is Loading) {
            return@withState
        }

        sourceFactory.invalidate()

        RxPagedListBuilder(sourceFactory, pagingConfig)
                .setFetchScheduler(Schedulers.io())
                .setNotifyScheduler(AndroidSchedulers.mainThread())
                .buildObservable()
                .doOnSubscribe {
                    setState { copy(isInitialLoading = true) }
                }
                .doOnEach {
                    setState { copy(isInitialLoading = false) }
                }
                .execute {
                    copy(eventRequest = it)
                }
    }

}