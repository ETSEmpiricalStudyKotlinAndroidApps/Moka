package io.github.tonnyl.moka.ui.profile.status

import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.mutations.changeUserStatus
import io.github.tonnyl.moka.ui.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*

class EditStatusViewModel(
    val args: EditStatusFragmentArgs
) : ViewModel() {

    private val _event = MutableLiveData<Event<EditStatusEvent>>()
    val event: LiveData<Event<EditStatusEvent>>
        get() = _event

    private val _updateStatusState = MutableLiveData<Resource<UserStatus?>>()
    val updateStatusState: LiveData<Resource<UserStatus?>>
        get() = _updateStatusState

    private val _clearStatusState = MutableLiveData<Resource<UserStatus?>>()
    val clearStatusState: LiveData<Resource<UserStatus?>>
        get() = _clearStatusState

    private val _emojiName = MutableLiveData<String?>(args.userStatus?.emoji)
    val emojiName: LiveData<String?>
        get() = _emojiName

    private val _message = MutableLiveData<String?>(args.userStatus?.message)
    val message: LiveData<String?>
        get() = _message

    private val _limitedAvailability =
        MutableLiveData<Boolean>(args.userStatus?.indicatesLimitedAvailability)
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

                val date: Date? = when (_expiresAt.value) {
                    ExpireAt.In30Minutes -> {
                        Date(System.currentTimeMillis() + DateUtils.MINUTE_IN_MILLIS * 30)
                    }
                    ExpireAt.In1Hour -> {
                        Date(System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS)
                    }
                    ExpireAt.Today -> {
                        Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, 23)
                            set(Calendar.MINUTE, 59)
                            set(Calendar.SECOND, 59)
                        }.time
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
                        expiresAt = date
                    )
                }

                _updateStatusState.value = Resource.success(
                    UserStatus(
                        createdAt = Date(),
                        emoji = _emojiName.value,
                        id = "",
                        indicatesLimitedAvailability = _limitedAvailability.value == true,
                        message = message.value,
                        expiresAt = date,
                        updatedAt = Date()
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

    fun updateEmojiAndMessage(emojiName: String?, msg: String?) {
        updateEmoji(emojiName)
        updateMessage(msg)
    }

    fun updateLimitedAvailability(limited: Boolean) {
        _limitedAvailability.value = limited
    }

    fun updateExpireAt(expireAt: ExpireAt) {
        _expiresAt.value = expireAt
    }

    fun showClearStatusMenu() {
        _event.value = Event(EditStatusEvent.ShowClearStatusMenu)
    }

    fun showEmojis() {
        _event.value = Event(EditStatusEvent.ShowEmojis)
    }

}