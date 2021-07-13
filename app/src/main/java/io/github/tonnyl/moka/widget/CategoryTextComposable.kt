package io.github.tonnyl.moka.widget

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize

@Composable
fun CategoryText(
    @StringRes textRes: Int,
    enablePlaceholder: Boolean
) {
    Text(
        text = stringResource(id = textRes),
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier
            .padding(all = ContentPaddingLargeSize)
            .placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
    )
}

@Preview(showBackground = true, name = "CategoryTextPreview")
@Composable
private fun CategoryTextPreview() {
    CategoryText(
        textRes = R.string.profile_info,
        enablePlaceholder = false
    )
}