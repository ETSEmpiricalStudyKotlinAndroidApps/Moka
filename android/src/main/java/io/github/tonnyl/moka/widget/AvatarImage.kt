package io.github.tonnyl.moka.widget

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.compose.rememberImagePainter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.tonnyl.moka.common.extensions.orGhostAvatarUrl

@Composable
fun AvatarImage(
    url: String?,
    modifier: Modifier = Modifier
) {
    Image(
        painter = rememberImagePainter(
            data = url.orGhostAvatarUrl,
            builder = {
                createAvatarLoadRequest()
            }
        ),
        contentDescription = stringResource(id = R.string.users_avatar_content_description),
        modifier = modifier
    )
}