package io.github.tonnyl.moka.network

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.emojis.singleDrawableAsSpan

object ImageLoader {

    fun loadAvatar(url: String?, imageView: AppCompatImageView) {
        imageView.load(url) {
            crossfade(enable = true)
            transformations(CircleCropTransformation())
        }
    }

    fun loadEmoji(
        url: String?,
        emojiSize: Float?,
        textView: AppCompatTextView
    ) {
        val emojiTextSize =
            emojiSize?.toInt() ?: textView.resources.getDimensionPixelSize(R.dimen.emoji_text_size)
        val request = ImageRequest.Builder(textView.context)
            .data(url)
            .size(emojiTextSize)
            .target(
                onSuccess = {
                    textView.singleDrawableAsSpan(it)
                }
            )
            .build()
        textView.context.imageLoader.enqueue(request)
    }

}