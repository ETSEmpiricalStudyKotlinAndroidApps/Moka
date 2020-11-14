package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R

@Composable
fun CategoryText(@StringRes textRes: Int) {
    Text(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.fragment_content_padding)),
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.subtitle1
    )
}

@Preview(showBackground = true, name = "CategoryTextPreview")
@Composable
private fun CategoryTextPreview() {
    CategoryText(textRes = R.string.profile_info)
}