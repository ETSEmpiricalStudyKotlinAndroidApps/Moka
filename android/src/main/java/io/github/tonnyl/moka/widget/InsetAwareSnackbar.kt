package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun InsetAwareSnackbar(data: SnackbarData) {
    Box(modifier = Modifier.navigationBarsPadding()) {
        Snackbar(snackbarData = data)
    }
}