package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize

@ExperimentalMaterialApi
@Composable
fun PreferenceCategoryText(text: String) {
    ListItem {
        Text(
            text = text,
            color = MaterialTheme.colors.secondary,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun PreferenceDivider() {
    Divider(modifier = Modifier.padding(vertical = ContentPaddingMediumSize))
}

@ExperimentalMaterialApi
@Preview(name = "PreferenceCategoryTextPreview", showBackground = true)
@Composable
private fun PreferenceCategoryTextPreview() {
    PreferenceCategoryText("Version")
}

@Preview(name = "PreferenceDividerPreview", showBackground = true)
@Composable
private fun PreferenceDividerPreview() {
    PreferenceDivider()
}