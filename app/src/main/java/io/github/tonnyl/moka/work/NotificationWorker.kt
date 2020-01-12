package io.github.tonnyl.moka.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.service.NotificationsService
import io.github.tonnyl.moka.notifications.NotificationsCenter
import timber.log.Timber
import java.util.*

class NotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val notificationsService by lazy {
        RetrofitClient.createService(NotificationsService::class.java)
    }

    override suspend fun doWork(): Result {
        Timber.i("NotificationWorker do work")

        val accounts = (applicationContext as MokaApp).loginAccounts.value
        if (accounts.isNullOrEmpty()) {
            Timber.i("NotificationWorker no account, do nothing")

            return Result.success()
        }

        val dao = MokaDataBase.getInstance(applicationContext, accounts.first().third.id)
            .notificationsDao()

        val result = try {
            val notifications = notificationsService.listNotificationsSuspended(
                all = false, // setting false indicates unread notifications only
                page = 1,
                perPage = MAX_ITEM_SIZE
            ).map {
                it.hasDisplayed = false
                it
            }

            if (notifications.isNotEmpty()) {
                dao.insert(notifications)
            }

            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "pull data from server error")

            Result.failure()
        }

        // Westworld ‎Season 2; Episode 1
        val journeyIntoNight = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).let {
            it >= 23 || it <= 6
        }

        if (journeyIntoNight) {
            return result
        }

        try {
            dao.notificationsToDisplayWithLimit(MAX_NOTIFICATION_SIZE).let {
                if (it.isNotEmpty()) {
                    NotificationsCenter.showNotifications(
                        applicationContext,
                        it,
                        accounts.first().third.id
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "query data from db error")
        }

        return result
    }

    companion object {

        const val WORKER_TAG = "NotificationWorker"

        private const val MAX_ITEM_SIZE = 100

        private const val MAX_NOTIFICATION_SIZE = 5

    }

}