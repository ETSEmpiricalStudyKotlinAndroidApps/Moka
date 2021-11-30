package io.github.tonnyl.moka.wearos.ui.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import io.tonnyl.moka.common.R
import io.tonnyl.moka.common.data.Event

@Composable
fun TimelineScreen() {
    val scalingLazyListState = rememberScalingLazyListState()
    ScalingLazyColumn(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        state = scalingLazyListState,
        modifier = Modifier.fillMaxSize()
    ) {

    }
}

@Composable
private fun TimelineEventItem(event: Event) {
    Chip(
        onClick = {

        },
        label = {
            Text(text = event.repo?.name ?: "")
        },
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_star_border_24),
                contentDescription = null
            )
        }
    )
}

@Preview(name = "TimelineScreenPreview", showBackground = true)
@Composable
private fun TimelineScreenPreview() {
    TimelineScreen()
}