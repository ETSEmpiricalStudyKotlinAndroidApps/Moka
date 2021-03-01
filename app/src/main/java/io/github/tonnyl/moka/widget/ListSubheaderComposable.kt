package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.fillMaxWidth
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
fun ListSubheader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = ContentPaddingLargeSize)
    )
}

@Preview(showBackground = true, name = "ListSubheaderPreview")
@Composable
private fun ListSubheaderPreview() {
    ListSubheader(text = stringResource(id = R.string.navigation_menu_timeline))
}