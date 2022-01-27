package io.github.tonnyl.moka.ui.profile.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.apollographql.apollo3.api.Optional
import io.github.tonnyl.moka.data.UserStatus
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.graphql.ChangeUserStatusMutation
import io.tonnyl.moka.graphql.type.ChangeUserStatusInput
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
data class EditStatusViewModelExtra(
    val accountInstance: AccountInstance,
    val initialEmoji: String?,
    val initialMessage: String?,
    val initialIndicatesLimitedAvailability: Boolean?
)

@ExperimentalSerializationApi
class EditStatusViewModel(private val extra: EditStatusViewModelExtra) : ViewModel() {

    private val _updateStatusState = MutableLiveData<Resource<UserStatus?>>()
    val updateStatusState: LiveData<Resource<UserStatus?>>
        get() = _updateStatusState

    private val _clearStatusState = MutableLiveData<Resource<UserStatus?>>()
    val clearStatusState: LiveData<Resource<UserStatus?>>
        get() = _clearStatusState

    private val _emojiName = MutableLiveData<String?>(extra.initialEmoji)
    val emojiName: LiveData<String?>
        get() = _emojiName

    private val _message = MutableLiveData<String?>(extra.initialMessage)
    val message: LiveData<String?>
        get() = _message

    private val _limitedAvailability =
        MutableLiveData<Boolean>(extra.initialIndicatesLimitedAvailability)
    val limitedAvailability: LiveData<Boolean>
        get() = _limitedAvailability

    private val _expiresAt = MutableLiveData<ExpireAt>(null)
    val expiresAt: LiveData<ExpireAt>
        get() = _expiresAt

    fun clearStatus() {
        if (_clearStatusState.value?.status == Status.LOADING) {
            return
        }

        viewModelScope.launch {
            try {
                _clearStatusState.value = Resource.loading(null)

                withContext(Dispatchers.IO) {
                    extra.accountInstance.apolloGraphQLClient.apolloClient
                        .mutation(
                            mutation = ChangeUserStatusMutation(
                                ChangeUserStatusInput()
                            )
                        )
                }

                _clearStatusState.value = Resource.success(null)
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _clearStatusState.value = Resource.error(e, null)
            }
        }
    }

    fun updateStatus() {
        if (_updateStatusState.value?.status == Status.LOADING) {
            return
        }

        viewModelScope.launch {
            try {
                _updateStatusState.value = Resource.loading(null)

                val instant: Instant? = when (_expiresAt.value) {
                    ExpireAt.In30Minutes -> {
                        val now = Clock.System.now()
                            .toLocalDateTime(TimeZone.UTC)
                            .toInstant(TimeZone.UTC)
                        now.plus(30, DateTimeUnit.MINUTE, TimeZone.UTC)
                    }
                    ExpireAt.In1Hour -> {
                        val now = Clock.System.now()
                            .toLocalDateTime(TimeZone.UTC)
                            .toInstant(TimeZone.UTC)
                        now.plus(1, DateTimeUnit.HOUR, TimeZone.UTC)
                    }
                    ExpireAt.Today -> {
                        val todayAt = Clock.System.todayAt(TimeZone.UTC)
                        val localDate = LocalDateTime(
                            todayAt.year,
                            todayAt.month,
                            todayAt.dayOfMonth,
                            23,
                            59,
                            59
                        )
                        localDate.toInstant(TimeZone.UTC)
                    }
                    ExpireAt.Never,
                    null -> {
                        null
                    }
                }
                withContext(Dispatchers.IO) {
                    extra.accountInstance.apolloGraphQLClient.apolloClient
                        .mutation(
                            mutation = ChangeUserStatusMutation(
                                input = ChangeUserStatusInput(
                                    emoji = Optional.Present(_emojiName.value),
                                    message = Optional.Present(message.value),
                                    limitedAvailability = Optional.Present(_limitedAvailability.value),
                                    expiresAt = Optional.Present(instant)
                                )
                            )
                        )
                }

                _updateStatusState.value = Resource.success(
                    UserStatus(
                        createdAt = Clock.System.now(),
                        emoji = _emojiName.value,
                        id = "",
                        indicatesLimitedAvailability = _limitedAvailability.value == true,
                        message = message.value,
                        expiresAt = instant,
                        updatedAt = Clock.System.now()
                    )
                )
            } catch (e: Exception) {
                logcat(priority = LogPriority.ERROR) { e.asLog() }

                _updateStatusState.value = Resource.error(e, null)
            }
        }
    }

    fun updateEmoji(emojiName: String?) {
        _emojiName.value = emojiName
    }

    fun updateMessage(message: String?) {
        _message.value = message
    }

    fun updateLimitedAvailability(limited: Boolean) {
        _limitedAvailability.value = limited
    }

    fun updateExpireAt(expireAt: ExpireAt) {
        _expiresAt.value = expireAt
    }

    companion object {

        private object EditStatusViewModelExtraKeyImpl :
            CreationExtras.Key<EditStatusViewModelExtra>

        val EDIT_STATUS_VIEW_MODEL_EXTRA_KEY: CreationExtras.Key<EditStatusViewModelExtra> =
            EditStatusViewModelExtraKeyImpl

    }

}