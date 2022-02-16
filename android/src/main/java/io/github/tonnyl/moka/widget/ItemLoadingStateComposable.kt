package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.IconSize

fun LazyListScope.ItemLoadingState(loadState: LoadState) {
    if (loadState == LoadState.Loading) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colors.secondary,
                    modifier = Modifier
                        .padding(all = ContentPaddingMediumSize)
                        .size(size = IconSize)
                )
            }
        }
    } else if (loadState is LoadState.Error) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(all = ContentPaddingLargeSize)
                ) {
                    Text(
                        text = stringResource(id = R.string.common_error_requesting_data),
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center
                    )
                    Spacer(
                        modifier = Modifier.height(height = ContentPaddingLargeSize)
                    )
                    Button(onClick = {}) {
                        Text(text = stringResource(id = R.string.common_retry))
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, name = "ItemLoadingStateLoadingPreview")
private fun ItemLoadingStateLoadingPreview() {
    LazyColumn {
        ItemLoadingState(loadState = LoadState.Loading)
    }
}

@Composable
@Preview(showBackground = true, name = "ItemLoadingStateErrorPreview")
private fun ItemLoadingStateErrorPreview() {
    LazyColumn {
        ItemLoadingState(loadState = LoadState.Error(IllegalStateException()))
    }
}
