package io.github.tonnyl.moka.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.notifications.NotificationsCenter
import io.github.tonnyl.moka.util.json
import io.ktor.client.statement.*
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.decodeFromString
import timber.log.Timber

class NotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        Timber.i("NotificationWorker do work")

        val accountInstances = (applicationContext as MokaApp).accountInstancesLiveData.value

        if (accountInstances.isNullOrEmpty()) {
            Timber.i("NotificationWorker no account, do nothing")

            return Result.success()
        }

        accountInstances.forEach { accountInstance ->
            val dao = accountInstance.database.notificationsDao()
            try {
                val response = accountInstance.notificationApi.listNotifications(
                    all = false, // setting false indicates unread notifications only
                    page = 1,
                    perPage = MAX_ITEM_SIZE
                )

                val notifications = json.decodeFromString<List<Notification>>(response.readText())
                    .map {
                        it.hasDisplayed = false
                        it
                    }

                if (notifications.isNotEmpty()) {
                    dao.insertAll(notifications)
                }

                Result.success()
            } catch (e: Exception) {
                Timber.e(e, "pull data from server error")

                Result.failure()
            }

            val now = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
            // Westworld Season 2; Episode 1
            val journeyIntoNight = now.hour >= 23 || now.hour <= 6

            if (journeyIntoNight) {
                try {
                    dao.notificationsToDisplayWithLimit(MAX_NOTIFICATION_SIZE).let { notifations ->
                        if (notifations.isNotEmpty()) {
                            NotificationsCenter.showNotifications(
                                context = applicationContext,
                                notifications = notifations,
                                accountId = accountInstance.signedInAccount.account.id
                            )
                        }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "query data from db error")
                }
            }
        }

        return Result.success()
    }

    companion object {

        const val WORKER_TAG = "NotificationWorker"

        private const val MAX_ITEM_SIZE = 100

        private const val MAX_NOTIFICATION_SIZE = 5

    }

}