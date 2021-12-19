package io.github.tonnyl.moka.wearos.ui.auth

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import io.github.tonnyl.moka.wearos.R

@Composable
fun AuthScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Image(
                contentDescription = stringResource(id = R.string.auth_logo_image_content_description),
                painter = painterResource(id = R.drawable.ic_app_icon_24),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colors.primary),
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(size = 80.dp)
            )
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Button(
                onClick = { },
                enabled = true
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_east_24),
                    contentDescription = stringResource(id = R.string.auth_get_started)
                )
            }
        }
    }
}

@Preview(
    name = "AuthScreenPreview",
    showBackground = true,
    uiMode = Configuration.UI_MODE_TYPE_WATCH,
    widthDp = 300,
    heightDp = 300
)
@Composable
private fun AuthScreenPreview() {
    AuthScreen()
}