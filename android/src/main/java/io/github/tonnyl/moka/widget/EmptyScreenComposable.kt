package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.tonnyl.moka.common.util.isNetworkAvailable

@Composable
fun EmptyScreenContent(
    modifier: Modifier = Modifier,
    @StringRes titleId: Int = R.string.common_error_requesting_data,
    @StringRes actionId: Int = R.string.common_retry,
    onlyPerformActionWhenNetworkConnected: Boolean = true,
    action: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(all = ContentPaddingLargeSize)
    ) {
        Column {
            Text(
                text = stringResource(id = titleId),
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .padding(vertical = ContentPaddingMediumSize)
                    .align(alignment = Alignment.CenterHorizontally)
            )
            val context = LocalContext.current
            OutlinedButton(
                onClick = {
                    if (onlyPerformActionWhenNetworkConnected) {
                        if (context.isNetworkAvailable()) {
                            action.invoke()
                        }
                    } else {
                        action.invoke()
                    }
                },
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(id = actionId))
            }
        }
    }
}

@Preview(showBackground = true, name = "EmptyScreenContentPreview")
@Composable
private fun EmptyScreenContentPreview() {
    EmptyScreenContent(
        titleId = R.string.common_error_requesting_data,
        actionId = R.string.common_retry,
        action = { }
    )
}