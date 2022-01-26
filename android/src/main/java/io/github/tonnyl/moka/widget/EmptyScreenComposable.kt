package io.github.tonnyl.moka.widget

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize

@Composable
fun EmptyScreenContent(
    @DrawableRes icon: Int? = null,
    iconVector: ImageVector? = null,
    @StringRes title: Int,
    @StringRes retry: Int,
    @StringRes action: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = ContentPaddingLargeSize)
    ) {
        Spacer(modifier = Modifier.weight(weight = 1f))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            val modifier = Modifier
                .size(size = 72.dp)
                .align(alignment = Alignment.CenterHorizontally)
                .padding(all = ContentPaddingMediumSize)
            if (icon != null) {
                Image(
                    contentDescription = stringResource(id = R.string.empty_screen_icon_content_description),
                    painter = painterResource(id = icon),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = LocalContentAlpha.current
                        )
                    ),
                    modifier = modifier
                )
            } else if (iconVector != null) {
                Image(
                    contentDescription = stringResource(id = R.string.empty_screen_icon_content_description),
                    imageVector = iconVector,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colors.onBackground.copy(
                            alpha = LocalContentAlpha.current
                        )
                    ),
                    modifier = modifier
                )
            }
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .padding(vertical = ContentPaddingMediumSize)
                    .align(alignment = Alignment.CenterHorizontally)
            )
        }
        Button(
            onClick = {},
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = retry))
        }
        TextButton(
            onClick = {},
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = action))
        }
        Spacer(modifier = Modifier.weight(weight = 1f))
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