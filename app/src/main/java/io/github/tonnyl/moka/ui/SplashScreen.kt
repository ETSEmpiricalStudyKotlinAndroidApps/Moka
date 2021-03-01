package io.github.tonnyl.moka.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.tonnyl.moka.R

@Composable
fun SplashScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            contentDescription = stringResource(id = R.string.auth_logo_image_content_description),
            painter = painterResource(id = R.drawable.ic_code_24),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(size = 128.dp)
        )
    }
}

@Preview(name = "SplashScreenPreview", showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen()
}