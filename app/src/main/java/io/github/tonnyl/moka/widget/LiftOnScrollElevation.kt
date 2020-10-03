package io.github.tonnyl.moka.widget

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TopAppBarElevation(lifted: Boolean): Dp {
    return (4.takeIf { lifted } ?: 0).dp
}