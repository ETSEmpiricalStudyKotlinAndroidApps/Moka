package io.github.tonnyl.moka.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.util.ContributionCalendarProvider
import io.tonnyl.moka.graphql.fragment.User

@Composable
fun ContributionCalendar(
    calendar: User.ContributionCalendar,
    enablePlaceholder: Boolean
) {
    val contributionWeekPlaceholder = remember {
        ContributionCalendarProvider().values.first().weeks.first()
    }
    LazyRow(contentPadding = PaddingValues(all = ContentPaddingLargeSize)) {
        if (enablePlaceholder) {
            val totalWeeks = 365 / 7
            items(count = totalWeeks) { index ->
                ContributionWeek(
                    week = contributionWeekPlaceholder,
                    enablePlaceholder = true
                )
                if (index != totalWeeks - 1) {
                    Spacer(modifier = Modifier.size(size = 2.dp))
                }
            }
        } else {
            itemsIndexed(items = calendar.weeks) { index, week ->
                ContributionWeek(
                    week = week,
                    enablePlaceholder = false
                )
                if (index != calendar.weeks.size - 1) {
                    Spacer(modifier = Modifier.size(size = 2.dp))
                }
            }
        }
    }
}

@Composable
private fun ContributionWeek(
    week: User.Week,
    enablePlaceholder: Boolean
) {
    Column {
        week.contributionDays.forEachIndexed { index, day ->
            ContributionDay(
                day = day,
                enablePlaceholder = enablePlaceholder
            )
            if (index != week.contributionDays.size - 1) {
                Spacer(modifier = Modifier.size(size = 2.dp))
            }
        }
    }
}

@Composable
private fun ContributionDay(
    day: User.ContributionDay,
    enablePlaceholder: Boolean
) {
    Box(
        modifier = Modifier
            .size(size = 12.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .then(
                other = if (enablePlaceholder) {
                    Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                } else {
                    Modifier.background(color = Color(day.color.toColorInt()))
                }
            )
    )
}

@Preview(
    name = "ContributionWeekPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun ContributionWeekPreview(
    @PreviewParameter(
        provider = ContributionCalendarProvider::class,
        limit = 1
    )
    calendar: User.ContributionCalendar
) {
    ContributionCalendar(
        calendar = calendar,
        enablePlaceholder = true
    )
}