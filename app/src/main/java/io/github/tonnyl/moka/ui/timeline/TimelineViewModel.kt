package io.github.tonnyl.moka.ui.timeline

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import androidx.paging.liveData
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.data.EventOrg
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.EventsService
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.timeline.EventItemEvent.*
import io.github.tonnyl.moka.ui.Event as UIEvent

@ExperimentalPagingApi
class TimelineViewModel(
    app: Application
) : AndroidViewModel(app) {

    var login: String = ""
    var userId: Long = 0L

    private val _initialLoadStatusLiveData = MutableLiveData<Resource<Boolean?>>()
    val initialLoadStatus: LiveData<Resource<Boolean?>>
        get() = _initialLoadStatusLiveData

    private val _event = MutableLiveData<UIEvent<EventItemEvent>>()
    val event: LiveData<UIEvent<EventItemEvent>>
        get() = _event

    val eventResult = liveData {
        emitSource(
            Pager(
                config = MokaApp.defaultPagingConfig,
                remoteMediator = EventRemoteMediator(
                    login,
                    RetrofitClient.createService(EventsService::class.java),
                    MokaDataBase.getInstance(getApplication(), userId),
                    _initialLoadStatusLiveData
                ),
                pagingSourceFactory = {
                    MokaDataBase.getInstance(getApplication(), userId)
                        .eventDao()
                        .eventsByCreatedAt()
                }
            ).liveData
        )
    }.cachedIn(viewModelScope)

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