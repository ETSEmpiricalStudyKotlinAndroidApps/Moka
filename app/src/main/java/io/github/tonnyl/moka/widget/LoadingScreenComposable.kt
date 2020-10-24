package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.preferredSize(72.dp)
                .align(Alignment.Center)
                .padding(dimensionResource(id = R.dimen.fragment_content_padding_half))
        )
    }
}

@Composable
@Preview(showBackground = true, name = "LoadingScreenPreview")
private fun LoadingScreenPreview() {
    LoadingScreen()
}