package io.github.tonnyl.moka.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavDeepLinkBuilder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Notification
import io.github.tonnyl.moka.data.toDisplayContentText
import io.github.tonnyl.moka.network.GlideApp
import io.github.tonnyl.moka.ui.MainActivity
import timber.log.Timber
import android.app.Notification as AndroidNotification

object NotificationsCenter {

    private const val NOTIFICATION_CHANNEL_ID = "notification_channel_id"

    private const val NOTIFICATION_GROUP = "io.github.tonnyl.moka.GITHUB_NOTIFICATION"
    private const val SUMMARY_ID = 0

    @WorkerThread
    fun showNotifications(
        context: Context,
        notifications: List<Notification>,
        accountId: Long
    ) {
        notifications.forEach {
            showNotification(context, it, accountId)
        }

        if (notifications.size > 1) {
            showSummaryNotification(context, notifications)
        }
    }

    fun cancelNotification(
        context: Context,
        notificationId: Int
    ) {
        NotificationManagerCompat.from(context)
            .cancel(notificationId)
    }

    @WorkerThread
    private fun showNotification(
        context: Context,
        n: Notification,
        accountId: Long
    ): Int {
        val manager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, manager)
        }

        // build intents start
        val markAsReadIntent = Intent(context, NotificationCallbackReceiver::class.java).apply {
            action = NotificationCallbackReceiver.ACTION_MARK_AS_READ
            putExtra(NotificationCallbackReceiver.EXTRA_ACCOUNT_ID, accountId)
            putExtra(NotificationCallbackReceiver.EXTRA_NOTIFICATION, n)
        }
        val markAsReadPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            markAsReadIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val readAction = NotificationCompat.Action.Builder(
            R.drawable.ic_check_24,
            context.getString(R.string.notification_mark_as_read),
            markAsReadPendingIntent
        ).setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
            .setShowsUserInterface(false)
            .build()

        val unsubscribeIntent = Intent(context, NotificationCallbackReceiver::class.java).apply {
            action = NotificationCallbackReceiver.ACTION_UNSUBSCRIBE
            putExtra(NotificationCallbackReceiver.EXTRA_ACCOUNT_ID, accountId)
            putExtra(NotificationCallbackReceiver.EXTRA_NOTIFICATION, n)
        }
        val unsubscribePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            unsubscribeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val unsubscribeAction = NotificationCompat.Action.Builder(
            R.drawable.ic_notifications_off,
            context.getString(R.string.notification_unsubscribe),
            unsubscribePendingIntent
        ).setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MUTE)
            .setShowsUserInterface(false)
            .build()

        val dismissIntent = Intent(context, NotificationDismissReceiver::class.java).apply {
            putExtra(NotificationDismissReceiver.EXTRA_ACCOUNT_ID, accountId)
            putExtra(NotificationDismissReceiver.EXTRA_NOTIFICATION, n)
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val contentPendingIntent = NavDeepLinkBuilder(context)
            .setComponentName(MainActivity::class.java)
            .setGraph(R.navigation.navigation_activity_main)
            .setDestination(R.id.nav_notifications)
            .setArguments(null)
            .createPendingIntent()
        // build intents end

        val content = n.toDisplayContentText(context)

        val notificationBuilder = createCommonNotificationBuilder(context)
            .setContentTitle(n.repository.fullName)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setWhen(n.updatedAt.time)
            .addAction(readAction)
            .addAction(unsubscribeAction)
            .setContentIntent(contentPendingIntent)
            .setDeleteIntent(dismissPendingIntent)

        n.repository.owner.avatarUrl.let { avatarUrl ->
            if (avatarUrl.isNotEmpty()) {
                try {
                    val bitmap = GlideApp.with(context)
                        .asBitmap()
                        .load(avatarUrl)
                        .circleCrop()
                        .submit()
                        .get()
                    notificationBuilder.setLargeIcon(bitmap)
                } catch (e: Exception) {
                    Timber.e(e, "get avatar error")
                }
            }
        }

        val notificationId = n.id.hashCode()
        manager.notify(notificationId, notificationBuilder.build())

        return notificationId
    }

    /**
     * https://developer.android.com/training/notify-user/group.html#set_a_group_summary
     */
    private fun showSummaryNotification(
        context: Context,
        notifications: List<Notification>
    ) {
        val reposCount = notifications.map { it.repository.id }.toSet().size
        val subtext = context.getString(
            R.string.notification_subtext_format,
            context.resources.getQuantityString(
                R.plurals.new_notifications_count,
                notifications.size,
                notifications.size
            ),
            context.resources.getQuantityString(
                R.plurals.repositories_count_of_notifications,
                reposCount,
                reposCount
            )
        )
        val summaryNotification = createCommonNotificationBuilder(context)
            .setContentTitle(context.getString(R.string.notification_channel_github_notifications))
            .setContentText(subtext)
            .setStyle(
                // Should use `apply` instead but `NotificationCompat.Style` class has a function
                // named `apply` too, and using import alias will get some side-effects.
                NotificationCompat.InboxStyle().run {
                    for (i in notifications.indices) {
                        if (i >= 4) {
                            break
                        }

                        addLine(notifications[i].toDisplayContentText(context))
                    }

                    setSummaryText(subtext)
                }
            )
            .setGroupSummary(true)
            .build()

        NotificationManagerCompat.from(context)
            .notify(SUMMARY_ID, summaryNotification)
    }

    private fun createCommonNotificationBuilder(
        context: Context
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_logo)
            .setDefaults(AndroidNotification.DEFAULT_ALL)
            .setAutoCancel(false)
            .setShowWhen(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SOCIAL)
            .setColor(ResourcesCompat.getColor(context.resources, R.color.colorAccent, null))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setGroup(NOTIFICATION_GROUP)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(
        context: Context,
        notificationManager: NotificationManagerCompat
    ) {
        notificationManager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_github_notifications),
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

}