package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import io.github.tonnyl.moka.R

@ExperimentalMaterialApi
@Composable
fun SnackBarErrorMessage(
    scaffoldState: ScaffoldState,
    action: (() -> Unit)? = null,
    @StringRes
    messageId: Int = R.string.common_error_requesting_data,
    @StringRes
    actionId: Int? = R.string.common_retry,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    val message = stringResource(id = messageId)
    val actionLabel = if (actionId == null) {
        null
    } else {
        stringResource(id = actionId)
    }

    LaunchedEffect(messageId, actionId, duration, action) {
        val result = scaffoldState.snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = duration
        )
        if (result == SnackbarResult.ActionPerformed) {
            action?.invoke()
        }
    }
}