package io.github.tonnyl.moka.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.db.MokaDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class NotificationDismissReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        val currentAccount = (context.applicationContext as MokaApp).loginAccounts
            .value?.firstOrNull() ?: return
        val accountId = intent.getLongExtra(EXTRA_ACCOUNT_ID, 0L)

        if (currentAccount.third.id != accountId) {
            return
        }

        val notificationId = intent.getStringExtra(EXTRA_NOTIFICATION_ID) ?: return
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val dao = MokaDataBase.getInstance(context, accountId).notificationsDao()
                dao.markAsDisplayed(notificationId)
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