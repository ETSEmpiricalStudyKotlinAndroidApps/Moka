package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .size(size = 72.dp)
                .align(alignment = Alignment.Center)
                .padding(all = ContentPaddingMediumSize)
        )
    }
}

@Composable
@Preview(showBackground = true, name = "LoadingScreenPreview")
private fun LoadingScreenPreview() {
    LoadingScreen()
}