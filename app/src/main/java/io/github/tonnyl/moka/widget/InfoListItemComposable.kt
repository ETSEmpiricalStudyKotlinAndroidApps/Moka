package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientEmphasisLevels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideEmphasis
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
            Text(
                text = stringResource(id = leadingRes),
                style = MaterialTheme.typography.body2,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.weight(weight = 1f, fill = true))
        ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.high) {
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