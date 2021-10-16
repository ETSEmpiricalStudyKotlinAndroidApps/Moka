package io.github.tonnyl.moka.network

import coil.request.ImageRequest
import io.github.tonnyl.moka.R

fun ImageRequest.Builder.createAvatarLoadRequest(): ImageRequest.Builder {
    return apply {
        placeholder(drawableResId = R.drawable.avatar_placeholder)
        error(drawableResId = R.drawable.avatar_placeholder)
        crossfade(enable = true)
    }
}