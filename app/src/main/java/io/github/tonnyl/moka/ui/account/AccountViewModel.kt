package io.github.tonnyl.moka.ui.account

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.tonnyl.moka.ui.Event

class AccountViewModel : ViewModel() {

    private val _event = MutableLiveData<Event<AccountEvent>>()
    val event: LiveData<Event<AccountEvent>>
        get() = _event

    private val _newSelectedAccount = MutableLiveData<Event<Long>>()
    val newSelectedAccount: LiveData<Event<Long>>
        get() = _newSelectedAccount

    val currentId = MutableLiveData<Long>()

    @MainThread
    fun onViewProfile() {
        _event.value = Event(AccountEvent.VIEW_PROFILE)
    }

    @MainThread
    fun onAddAnotherAccount() {
        _event.value = Event(AccountEvent.ADD_ANOTHER_ACCOUNT)
    }

    @MainThread
    fun onManageGitHubAccount() {
        _event.value = Event(AccountEvent.MANAGER_GITHUB_ACCOUNTS)
    }

    @MainThread
    fun onViewPrivacyPolicy() {
        _event.value = Event(AccountEvent.VIEW_PRIVACY_POLICY)
    }

    @MainThread
    fun onViewTermsOfService() {
        _event.value = Event(AccountEvent.VIEW_TERMS_OF_SERVICE)
    }

    @MainThread
    fun onSelectNewAccount(id: Long) {
        _newSelectedAccount.value = Event(id)
    }

}