package io.github.tonnyl.moka

import android.accounts.Account
import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.util.mapToAccountTokenUserTriple
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MokaApp : Application() {

    private val accountManager by lazy(LazyThreadSafetyMode.NONE) {
        AccountManager.get(this)
    }

    val loginAccounts = MutableLiveData<List<Triple<Account, String, AuthenticatedUser>>>()

    companion object {
        const val PER_PAGE = 16
        const val MAX_SIZE_OF_PAGED_LIST = 1024
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        accountManager.addOnAccountsUpdatedListener(
            { accounts ->
                GlobalScope.launch(Dispatchers.IO) {
                    val gson = Gson()
                    loginAccounts.postValue(
                        accounts.sortedByDescending {
                            accountManager.getPassword(it).toLong()
                        }.map {
                            it.mapToAccountTokenUserTriple(gson, accountManager)
                        }.toMutableList()
                    )
                }
            },
            null,
            true
        )
    }

}