package io.github.tonnyl.moka.work

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.work.*
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.widget.contribution.ContributionCalendarAppWidgetReceiver
import io.tonnyl.moka.common.store.data.ContributionCalendarDay
import io.tonnyl.moka.common.store.data.ContributionCalendarMonth
import io.tonnyl.moka.common.store.data.ContributionCalendarWeek
import io.tonnyl.moka.graphql.UsersContributionsCollectionQuery
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import java.util.concurrent.TimeUnit

class ContributionCalendarWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun doWork(): Result {
        logcat(priority = LogPriority.INFO, tag = TAG) { "start work" }

        val account = (applicationContext as MokaApp).accountInstancesLiveData.value?.firstOrNull()
        if (account == null) {
            logcat(priority = LogPriority.INFO, tag = TAG) { "no account, do nothing" }

            sendUpdateWidgetBroadcast()

            return Result.success()
        }

        return try {
            val calendar = account.apolloGraphQLClient.apolloClient.query(
                query = UsersContributionsCollectionQuery(login = account.signedInAccount.account.login)
            )
                .execute().data?.user?.contributionsCollection?.contributionsCollection?.contributionCalendar
            if (calendar != null) {
                account.contributionCalendarDataStore.updateData {
                    it.copy(
                        colors = calendar.colors,
                        isHalloween = calendar.isHalloween,
                        months = calendar.months.map { month ->
                            ContributionCalendarMonth(
                                firstDay = month.firstDay,
                                name = month.name,
                                totalWeeks = month.totalWeeks,
                                year = month.year
                            )
                        },
                        totalContributions = calendar.totalContributions,
                        weeks = calendar.weeks.map { week ->
                            ContributionCalendarWeek(
                                contributionDays = week.contributionDays.map { day ->
                                    ContributionCalendarDay(
                                        color = day.color,
                                        contributionCount = day.contributionCount,
                                        contributionLevel = day.contributionLevel.rawValue,
                                        date = day.date,
                                        weekday = day.weekday
                                    )
                                },
                                firstDay = week.firstDay
                            )
                        }
                    )
                }
            }

            sendUpdateWidgetBroadcast()

            Result.success()
        } catch (e: Exception) {
            logcat(
                priority = LogPriority.INFO,
                tag = TAG
            ) { "failed to fetch remote data\n${e.asLog()}" }

            sendUpdateWidgetBroadcast()

            Result.failure()
        }
    }

    private fun sendUpdateWidgetBroadcast() {
        val ids = AppWidgetManager.getInstance(applicationContext)
            .getAppWidgetIds(
                ComponentName(
                    applicationContext,
                    ContributionCalendarAppWidgetReceiver::class.java
                )
            )

        val intent = Intent(applicationContext, ContributionCalendarAppWidgetReceiver::class.java)
            .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)

        applicationContext.sendBroadcast(intent)
    }

    companion object {

        const val TAG = "ContributionCalendarWorker"

        @ExperimentalSerializationApi
        fun startOrCancelWorker(context: Context) {
            val app = context.applicationContext as MokaApp
            app.applicationScope.launch {
                try {
                    val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                        ComponentName(context, ContributionCalendarAppWidgetReceiver::class.java)
                    )
                    val userHasAddedWidget = ids.isNotEmpty()

                    if (userHasAddedWidget) {
                        WorkManager.getInstance(app)
                            .enqueueUniquePeriodicWork(
                                TAG,
                                ExistingPeriodicWorkPolicy.REPLACE,
                                PeriodicWorkRequestBuilder<ContributionCalendarWorker>(
                                    15L,
                                    TimeUnit.MINUTES
                                ).setConstraints(
                                    Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .setRequiresBatteryNotLow(true)
                                        .build()
                                ).addTag(TAG)
                                    .build()
                            )
                    } else {
                        WorkManager.getInstance(app)
                            .cancelAllWorkByTag(TAG)
                    }
                } catch (e: Exception) {
                    logcat(
                        priority = LogPriority.ERROR,
                        tag = TAG
                    ) { "startOrCancelWorker failed: ${e.asLog()}" }
                }
            }
        }

    }

}