package io.github.tonnyl.moka.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.data.ReactionGroup
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.mutations.addReaction
import io.github.tonnyl.moka.network.mutations.removeReaction
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.ui.UserEvent.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {

    val currentUser = MutableLiveData<AuthenticatedUser>()

    private val _event = MutableLiveData<Event<UserEvent>>()
    val event: LiveData<Event<UserEvent>>
        get() = _event

    private val _reactEvent = MutableLiveData<Event<React>>()
    val reactEvent: LiveData<Event<React>>
        get() = _reactEvent

    fun getUserProfile() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = runBlocking {
//                    GraphQLClient.apolloClient
//                        .query(
//                            ViewerQuery.builder()
//                                .build()
//                        )
//                        .execute()
//                }
//
//                Timber.d("get viewer info call success, resp = $response")
//
//                loginUserProfile.postValue(response.data())
//            } catch (e: Exception) {
//                Timber.e(e, "get viewer info call error: ${e.message}")
//
//            }
//        }
    }

    fun showSearch() {
        _event.value = Event(ShowSearch)
    }

    fun showAccounts() {
        _event.value = Event(ShowAccounts)
    }

    fun showReactionDialog(
        reactableId: String,
        currentReactionGroups: List<ReactionGroup>?
    ) {
        val userHasReactedContents = currentReactionGroups?.filter {
            it.viewerHasReacted
        }?.map {
            it.content.rawValue
        }?.toTypedArray()
        _event.value = Event(ShowReactionDialog(reactableId, userHasReactedContents))
    }

    fun react(
        content: ReactionContent,
        reactableId: String,
        isSelected: Boolean
    ) {
        _event.value = Event(DismissReactionDialog)

        viewModelScope.launch(Dispatchers.IO) {
            _reactEvent.postValue(
                Event(
                    React(
                        Resource.loading(null),
                        content,
                        reactableId,
                        isSelected
                    )
                )
            )

            try {
                if (isSelected) {
                    addReaction(reactableId, content)
                } else {
                    removeReaction(reactableId, content)
                }

                _reactEvent.postValue(
                    Event(
                        React(
                            Resource.success(null),
                            content,
                            reactableId,
                            isSelected
                        )
                    )
                )
            } catch (e: Exception) {
                Timber.e(e)

                _reactEvent.postValue(
                    Event(
                        React(
                            Resource.error(e.message, null),
                            content,
                            reactableId,
                            isSelected
                        )
                    )
                )
            }
        }
    }

}