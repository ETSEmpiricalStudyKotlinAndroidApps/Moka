package io.github.tonnyl.moka.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.MokaApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

@ExperimentalSerializationApi
@ExperimentalPagingApi
class NotificationDismissReceiver : BroadcastReceiver() {

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
                logcat(priority = LogPriority.ERROR) { "handle received event\n${e.asLog()}" }
            }
        }
    }

    companion object {

        const val EXTRA_ACCOUNT_ID = "extra_account_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

    }

}