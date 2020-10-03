package io.github.tonnyl.moka.network

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ContextAmbient
import coil.request.ImageRequest
import io.github.tonnyl.moka.R

@Composable
fun createAvatarLoadRequest(url: Uri?): ImageRequest {
    return ImageRequest.Builder(ContextAmbient.current)
        .placeholder(R.drawable.avatar_placeholder)
        .error(R.drawable.avatar_placeholder)
        .data(url)
        .crossfade(enable = true)
        .build()
}

@Composable
fun createAvatarLoadRequest(url: String?): ImageRequest {
    return createAvatarLoadRequest(url = url?.let { Uri.parse(it) })
}