package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize

@Composable
fun InfoListItem(
    @StringRes leadingRes: Int,
    trailing: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .padding(all = ContentPaddingLargeSize)
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = leadingRes),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.body2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, name = "InfoListItemPreview")
@Composable
private fun InfoListItemPreview() {
    InfoListItem(
        leadingRes = R.string.profile_joined_on,
        trailing = "Yesterday"
    )
}