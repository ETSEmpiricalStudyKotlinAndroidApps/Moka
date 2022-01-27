package io.github.tonnyl.moka.widget.contribution

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.work.ContributionCalendarWorker
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@OptIn(ExperimentalSerializationApi::class)
class ContributionCalendarAppWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget
        get() = ContributionCalendarGlanceWidget()

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

        context ?: return

        ContributionCalendarWorker.startOrCancelWorker(context = context)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        context ?: return

        ContributionCalendarWorker.startOrCancelWorker(context = context)
    }

}