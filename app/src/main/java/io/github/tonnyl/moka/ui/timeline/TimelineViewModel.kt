package io.github.tonnyl.moka.ui.timeline

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.EventOrg
import io.github.tonnyl.moka.db.dao.EventDao
import io.github.tonnyl.moka.network.PagedResource
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.ui.NetworkDatabaseSourceViewModel
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.timeline.EventItemEvent.*
import io.github.tonnyl.moka.ui.Event as UIEvent

class TimelineViewModel(
    private val localSource: EventDao
) : NetworkDatabaseSourceViewModel<Event>() {

    private val _initialLoadStatusLiveData = MutableLiveData<Resource<List<Event>>>()
    val initialLoadStatusLiveData: LiveData<Resource<List<Event>>>
        get() = _initialLoadStatusLiveData

    private val _previousNextLoadStatusLiveData = MutableLiveData<PagedResource<List<Event>>>()
    val previousNextLoadStatusLiveData: LiveData<PagedResource<List<Event>>>
        get() = _previousNextLoadStatusLiveData

    private val _event = MutableLiveData<UIEvent<EventItemEvent>>()
    val event: LiveData<UIEvent<EventItemEvent>>
        get() = _event

    private lateinit var sourceFactory: TimelineDataSourceFactory

    override fun initLocalSource(): LiveData<PagedList<Event>> {
        return LivePagedListBuilder(
            localSource.eventsByCreatedAt(),
            pagingConfig
        ).build()
    }

    override fun initRemoteSource(): LiveData<PagedList<Event>> {
        sourceFactory = TimelineDataSourceFactory(
            RetrofitClient.createService(EventsService::class.java),
            localSource,
            login,
            _initialLoadStatusLiveData,
            _previousNextLoadStatusLiveData
        )

        return LivePagedListBuilder(
            sourceFactory,
            pagingConfig
        ).build()
    }

    fun retryLoadPreviousNext() {
        sourceFactory.retryLoadPreviousNext()
    }

    @MainThread
    fun viewProfile(login: String, type: ProfileType) {
        _event.value = UIEvent(ViewProfile(login, type))
    }

    @MainThread
    fun viewEventDetail(event: Event) {
        when (event.type) {
            Event.WATCH_EVENT,
            Event.PUBLIC_EVENT,
            Event.CREATE_EVENT,
            Event.TEAM_ADD_EVENT,
            Event.DELETE_EVENT -> {
                viewRepository(
                    event.repo?.name ?: return,
                    event.org
                )
            }
            Event.COMMIT_COMMENT_EVENT -> {

            }
            Event.FORK_EVENT -> {
                viewRepository(
                    event.payload?.forkee?.fullName ?: return,
                    event.org
                )
            }
            Event.GOLLUM_EVENT -> {

            }
            Event.ISSUE_COMMENT_EVENT,
            Event.ISSUES_EVENT -> {
                val issue = event.payload?.issue ?: return
                val repoFullName = (event.repo?.fullName ?: event.repo?.name ?: return).split("/")

                if (repoFullName.size < 2) {
                    return
                }

                if (event.payload?.comment?.htmlUrl?.contains("pull") == true) { // is a pull request comment

                } else { // is an issue comment
                    viewIssueDetail(repoFullName[0], repoFullName[1], issue.number)
                }
            }
            Event.MEMBER_EVENT -> {
                viewProfile(
                    event.payload?.member?.login ?: return,
                    ProfileType.USER
                )
            }
            Event.PULL_REQUEST_EVENT -> {

            }
            Event.PULL_REQUEST_REVIEW_COMMENT_EVENT -> {

            }
            Event.PULL_REQUEST_REVIEW_EVENT -> {

            }
            Event.REPOSITORY_EVENT -> {
                when (event.payload?.action) {
                    "created",
                    "archived",
                    "publicized",
                    "unarchived" -> {
                        viewRepository(
                            event.repo?.name ?: return,
                            event.org
                        )
                    }
                    "privatized",
                    "deleted" -> {
                        // ignore
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
            Event.PUSH_EVENT -> {

            }
            Event.RELEASE_EVENT -> {

            }
            Event.ORG_BLOCK_EVENT -> {
                viewProfile(
                    event.payload?.blockedUser?.login ?: return,
                    ProfileType.USER
                )
            }
            Event.PROJECT_CARD_EVENT -> {

            }
            Event.PROJECT_COLUMN_EVENT -> {

            }
            Event.ORGANIZATION_EVENT -> {
                viewProfile(
                    event.payload?.organization?.login ?: return,
                    ProfileType.ORGANIZATION
                )
            }
            Event.PROJECT_EVENT -> {

            }
            Event.DOWNLOAD_EVENT,
            Event.FOLLOW_EVENT,
            Event.GIST_EVENT,
            Event.FORK_APPLY_EVENT -> {
                // Events of these types are no longer delivered, just ignore them.
            }
        }
    }

    @MainThread
    fun viewRepository(fullName: String, org: EventOrg?) {
        _event.value = UIEvent(ViewRepository(fullName, org))
    }

    @MainThread
    fun viewIssueDetail(
        repoOwner: String,
        repoName: String,
        number: Int
    ) {
        _event.value = UIEvent(ViewIssueDetail(repoOwner, repoName, number))
    }

}