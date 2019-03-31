package io.github.tonnyl.moka.ui.timeline

import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import io.github.tonnyl.moka.MokaApp.Companion.MAX_SIZE_OF_PAGED_LIST
import io.github.tonnyl.moka.MokaApp.Companion.PER_PAGE
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.net.RetrofitClient
import io.github.tonnyl.moka.net.service.EventsService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TimelineViewModel(
        initialState: TimelineState
) : BaseMvRxViewModel<TimelineState>(initialState) {

    private lateinit var userLogin: String
    private val sourceFactory: TimelineDataSourceFactory by lazy {
        TimelineDataSourceFactory(RetrofitClient.createService(EventsService::class.java), userLogin)
    }

    private val pagingConfig: PagedList.Config by lazy {
        PagedList.Config.Builder()
                .setPageSize(PER_PAGE)
                .setMaxSize(MAX_SIZE_OF_PAGED_LIST)
                .setInitialLoadSizeHint(PER_PAGE)
                .setEnablePlaceholders(false)
                .build()
    }

    private lateinit var observablePagedList: Observable<PagedList<Event>>

    companion object : MvRxViewModelFactory<TimelineViewModel, TimelineState> {

        override fun create(viewModelContext: ViewModelContext, state: TimelineState): TimelineViewModel? = TimelineViewModel(state)

    }

    fun refreshEventsData(login: String) = withState { state ->
        if (state.eventRequest is Loading) {
            return@withState
        }

        if (!this::userLogin.isInitialized || userLogin != login) {
            userLogin = login

            observablePagedList = RxPagedListBuilder(sourceFactory, pagingConfig)
                    .setFetchScheduler(Schedulers.io())
                    .setNotifyScheduler(AndroidSchedulers.mainThread())
                    .buildObservable()
                    .doOnSubscribe {
                        setState { copy(isInitialLoading = true) }
                    }
                    .doOnEach {
                        setState { copy(isInitialLoading = false) }
                    }
        }

        observablePagedList
                .execute {
                    copy(eventRequest = it)
                }
    }

}