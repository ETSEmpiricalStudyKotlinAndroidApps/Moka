package io.github.tonnyl.moka.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.tonnyl.moka.MokaApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber

class NotificationCallbackReceiver : BroadcastReceiver() {

    @ExperimentalSerializationApi
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        val app = context.applicationContext as MokaApp
        app.applicationScope.launch(Dispatchers.IO) {
            try {
                val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID) ?: return@launch

                val accountInstances = app.accountInstancesLiveData.value

                if (accountInstances.isNullOrEmpty()) {
                    return@launch
                }

                accountInstances.forEach { accountInstance ->
                    val accountId = intent.getLongExtra(EXTRA_ACCOUNT_ID, 0L)
                    if (accountId != accountInstance.signedInAccount.account.id) {
                        return@forEach
                    }

                    val dao = accountInstance.database.notificationsDao()
                    dao.markAsDisplayed(notificationId)

                    val notification = dao.notificationById(notificationId) ?: return@forEach

                    when (intent.action) {
                        ACTION_MARK_AS_READ -> {
                            accountInstance.notificationApi.markAsRead(notification.updatedAt.toString())
                        }
                        ACTION_UNSUBSCRIBE -> {
                            val threadId = notification.url.split("/").lastOrNull()
                            if (threadId.isNullOrEmpty()) {
                                Timber.i("thread id is null or empty")
                            } else {
                                accountInstance.notificationApi.deleteAThreadSubscription(threadId)
                            }
                        }
                    }

                    NotificationsCenter.cancelNotification(context, notification.id.hashCode())
                }
            } catch (e: Exception) {
                Timber.e(e, "perform ${intent.action} error")
            }
        }
    }

    companion object {

        const val EXTRA_ACCOUNT_ID = "extra_account_id"
        const val EXTRA_NOTIFICATION_ID = "extra_notification_id"

        const val ACTION_MARK_AS_READ = "io.github.tonnyl.moka.ACTION_MARK_AS_READ"
        const val ACTION_UNSUBSCRIBE = "io.github.tonnyl.moka.ACTION_UNSUBSCRIBE"

    }

}