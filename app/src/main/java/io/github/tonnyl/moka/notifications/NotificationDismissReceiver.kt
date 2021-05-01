package io.github.tonnyl.moka.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.tonnyl.moka.MokaApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

class NotificationDismissReceiver : BroadcastReceiver() {

    @ExperimentalSerializationApi
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID) ?: return
        val app = context.applicationContext as MokaApp
        app.applicationScope.launch(Dispatchers.IO) {
            try {
                val accountInstances = app.accountInstancesLiveData.value

                if (accountInstances.isNullOrEmpty()) {
                    return@launch
                }

                val accountId = intent.getLongExtra(EXTRA_ACCOUNT_ID, 0L)
                accountInstances.forEach { accountInstance ->
                    if (accountInstance.signedInAccount.account.id != accountId) {
                        return@forEach
                    }

                    accountInstance.database.notificationsDao()
                        .markAsDisplayed(notificationId)
                }
            } catch (e: Exception) {
                Timber.e(e, "handle received event")
            }
        }
    }

    companion object {

        const val EXTRA_ACCOUNT_ID = "extra_account_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    }

}