package io.github.tonnyl.moka.network

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import io.github.tonnyl.moka.R

@Composable
fun createAvatarLoadRequest(url: Uri?): ImageRequest {
    return ImageRequest.Builder(context = LocalContext.current)
        .placeholder(drawableResId = R.drawable.avatar_placeholder)
        .error(drawableResId = R.drawable.avatar_placeholder)
        .data(
            data = url
                ?: "https://avatars.githubusercontent.com/u/10137?s=460&u=b1951d34a583cf12ec0d3b0781ba19be97726318&v=4"
        )
        .crossfade(enable = true)
        .build()
}

@Composable
fun createAvatarLoadRequest(url: String?): ImageRequest {
    return createAvatarLoadRequest(url = url?.let { Uri.parse(it) })
}