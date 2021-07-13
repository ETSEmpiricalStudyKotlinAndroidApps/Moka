package io.github.tonnyl.moka.widget

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun DefaultSwipeRefreshIndicator(
    state: SwipeRefreshState,
    refreshTriggerDistance: Dp
) {
    SwipeRefreshIndicator(
        state = state,
        refreshTriggerDistance = refreshTriggerDistance,
        scale = true,
        contentColor = MaterialTheme.colors.secondary
    )
}