package io.github.tonnyl.moka.widget

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tonnyl.moka.R

@Composable
fun EmptyScreenContent(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes retry: Int,
    @StringRes action: Int
) {
    Column(
        modifier = Modifier.fillMaxSize()
            .padding(dimensionResource(id = R.dimen.fragment_content_padding))
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Providers(AmbientContentAlpha provides ContentAlpha.medium) {
            Image(
                imageVector = vectorResource(id = icon),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter(
                    AmbientContentColor.current.copy(alpha = ContentAlpha.medium),
                    BlendMode.SrcIn
                ),
                modifier = Modifier.preferredSize(72.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(dimensionResource(id = R.dimen.fragment_content_padding_half))
            )
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.fragment_content_padding_half))
                    .align(Alignment.CenterHorizontally)
            )
        }
        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = retry))
        }
        TextButton(
            onClick = {},
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = action))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, name = "EmptyScreenContentPreview")
@Composable
private fun EmptyScreenContentPreview() {
    EmptyScreenContent(
        icon = R.drawable.ic_menu_timeline_24,
        title = R.string.timeline_content_empty_title,
        retry = R.string.common_retry,
        action = R.string.timeline_content_empty_action
    )
}