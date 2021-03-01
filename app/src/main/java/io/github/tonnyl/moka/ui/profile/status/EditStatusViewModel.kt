package io.github.tonnyl.moka.ui.profile.status

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.changeUserStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.*
import timber.log.Timber

class EditStatusViewModel(
    initialEmoji: String?,
    initialMessage: String?,
    initialIndicatesLimitedAvailability: Boolean?
) : ViewModel() {

    private val _updateStatusState = MutableLiveData<Resource<UserStatus?>>()
    val updateStatusState: LiveData<Resource<UserStatus?>>
        get() = _updateStatusState

    private val _clearStatusState = MutableLiveData<Resource<UserStatus?>>()
    val clearStatusState: LiveData<Resource<UserStatus?>>
        get() = _clearStatusState

    private val _emojiName = MutableLiveData<String?>(initialEmoji)
    val emojiName: LiveData<String?>
        get() = _emojiName

    private val _message = MutableLiveData<String?>(initialMessage)
    val message: LiveData<String?>
        get() = _message

    private val _limitedAvailability = MutableLiveData<Boolean>(initialIndicatesLimitedAvailability)
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
                    changeUserStatus()
                }

                _clearStatusState.value = Resource.success(null)
            } catch (e: Exception) {
                Timber.e(e)

                _clearStatusState.value = Resource.error(e.message, null)
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
                    changeUserStatus(
                        emoji = _emojiName.value,
                        message = message.value,
                        limitedAvailability = _limitedAvailability.value,
                        expiresAt = instant
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
                Timber.e(e)

                _updateStatusState.value = Resource.error(e.message, null)
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

}