package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize

@Composable
fun CategoryText(@StringRes textRes: Int) {
    Text(
        modifier = Modifier.padding(all = ContentPaddingLargeSize),
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.subtitle1
    )
}

@Preview(showBackground = true, name = "CategoryTextPreview")
@Composable
private fun CategoryTextPreview() {
    CategoryText(textRes = R.string.profile_info)
}