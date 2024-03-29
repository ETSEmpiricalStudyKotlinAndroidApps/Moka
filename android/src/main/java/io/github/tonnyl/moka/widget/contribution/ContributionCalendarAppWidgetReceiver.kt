package io.github.tonnyl.moka.widget.contribution

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import io.github.tonnyl.moka.work.ContributionCalendarWorker

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