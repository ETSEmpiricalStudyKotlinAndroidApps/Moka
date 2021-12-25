package io.github.tonnyl.moka.widget.contribution

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.unit.ColorProvider
import androidx.glance.layout.*
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.MainActivity
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.tonnyl.moka.common.store.data.ContributionCalendarDay
import io.tonnyl.moka.common.store.data.ContributionCalendarWeek
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi

class ContributionCalendarGlanceWidget : GlanceAppWidget() {

    @OptIn(ExperimentalSerializationApi::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val account = (context.applicationContext as MokaApp).accountInstancesLiveData
            .value?.firstOrNull()

        if (account == null) {
            EmptyWidget(context = context)

            return
        }

        // may have a better choice.
        val calendar = runBlocking {
            account.contributionCalendarDataStore.data.first()
        }

        if (calendar.months.isEmpty()) {
            EmptyWidget(context = context)

            return
        }

        val last7Weeks = mutableListOf<ContributionCalendarWeek>()
        var index = calendar.weeks.size - 1
        while (index >= 0 && last7Weeks.size < 7) {
            last7Weeks.add(0, calendar.weeks[index])
            index--
        }

        Row(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.size(width = 120.dp, height = 116.dp)
                .cornerRadius(radius = 16.dp)
                .padding(start = 9.dp, end = 9.dp, top = 5.dp)
                .background(colorProvider = WIDGET_BACKGROUND_COLOR_PROVIDER)
        ) {
            last7Weeks.forEach { week ->
                ContributionWeek(week = week, modifier = GlanceModifier.defaultWeight())
            }
        }

    }

}

@ExperimentalSerializationApi
@Composable
private fun EmptyWidget(context: Context) {
    Column(
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = GlanceModifier.size(width = 120.dp, height = 116.dp)
            .cornerRadius(radius = 16.dp)
            .background(colorProvider = WIDGET_BACKGROUND_COLOR_PROVIDER)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = GlanceModifier.fillMaxWidth()
                .height(height = 58.dp)
        ) {
            Image(
                provider = ImageProvider(resId = R.mipmap.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = GlanceModifier.size(size = 24.dp)
            )
            Text(
                text = context.getString(R.string.app_name),
                style = TextStyle(
                    color = ColorProvider(day = Color.Black, night = Color.White),
                    fontSize = 13.sp
                )
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = GlanceModifier.fillMaxWidth()
                .height(height = 58.dp)
        ) {
            Text(
                text = context.getString(R.string.auth_get_started),
                style = TextStyle(
                    color = WIDGET_BACKGROUND_COLOR_PROVIDER,
                    fontSize = 13.sp
                ),
                modifier = GlanceModifier.background(color = R.color.colorPrimary)
                    .cornerRadius(radius = 4.dp)
                    .padding(vertical = 4.dp, horizontal = 8.dp)
                    .clickable(onClick = actionStartActivity<AuthActivity>())
            )
        }
    }
}

@ExperimentalSerializationApi
@Composable
private fun ContributionWeek(
    week: ContributionCalendarWeek,
    modifier: GlanceModifier = GlanceModifier
) {
    LazyColumn(modifier = modifier) {
        items(count = DAYS_OF_A_WEEK) { index ->
            Column {
                ContributionDay(day = week.contributionDays.getOrNull(index = index))
                if (index < DAYS_OF_A_WEEK) {
                    Spacer(modifier = GlanceModifier.height(height = 4.dp))
                }
            }
        }
    }
}

@ExperimentalSerializationApi
@Composable
private fun ContributionDay(day: ContributionCalendarDay?) {
    Box(
        modifier = GlanceModifier.size(size = 10.dp)
            .cornerRadius(radius = 3.dp)
            .background(color = day?.color?.toColorInt()?.let { Color(it) } ?: Color.Transparent)
            .clickable(onClick = actionStartActivity<MainActivity>())
    ) {

    }
}

private const val DAYS_OF_A_WEEK = 7
private val WIDGET_BACKGROUND_COLOR_PROVIDER = ColorProvider(day = Color.White, night = Color.Black)