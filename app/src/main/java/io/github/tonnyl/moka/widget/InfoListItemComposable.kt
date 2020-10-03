package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.EmphasisAmbient
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R

@Composable
fun InfoListItem(
    @StringRes leadingRes: Int,
    trailing: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.clip(RoundedCornerShape(dimensionResource(id = R.dimen.regular_radius)))
            .padding(dimensionResource(id = R.dimen.fragment_content_padding))
    ) {
        ProvideEmphasis(emphasis = EmphasisAmbient.current.medium) {
            Text(
                modifier = Modifier.weight(weight = 1f, fill = true),
                text = stringResource(id = leadingRes),
                style = MaterialTheme.typography.body2,
                overflow = TextOverflow.Ellipsis
            )
        }
        ProvideEmphasis(emphasis = EmphasisAmbient.current.high) {
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