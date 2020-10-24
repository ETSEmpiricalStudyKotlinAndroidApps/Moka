package io.github.tonnyl.moka.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class NotificationCallbackReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        val user = (context.applicationContext as MokaApp).loginAccounts
            .value?.firstOrNull() ?: return

        val accountId = intent.getLongExtra(EXTRA_ACCOUNT_ID, 0L)
        if (accountId != user.third.id) {
            return
        }

        val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID) ?: return

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val dao = MokaDataBase.getInstance(context, accountId).notificationsDao()
                dao.markAsDisplayed(notificationId)

                val notification = dao.notificationById(notificationId) ?: return@launch

                val service = RetrofitClient.createService(NotificationsService::class.java)

                when (intent.action) {
                    ACTION_MARK_AS_READ -> {
                        service.markAsRead(notification.updatedAt.toString())
                    }
                    ACTION_UNSUBSCRIBE -> {
                        val threadId = notification.url.split("/").lastOrNull()
                        if (threadId.isNullOrEmpty()) {
                            Timber.i("thread id is null or empty")
                        } else {
                            service.deleteAThreadSubscription(threadId)
                        }
                    }
                }

                NotificationsCenter.cancelNotification(context, notification.id.hashCode())
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