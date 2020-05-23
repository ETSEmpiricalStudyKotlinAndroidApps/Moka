package io.github.tonnyl.moka.network

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.emojis.singleDrawableAsSpan

object GlideLoader {

    fun loadAvatar(url: String?, imageView: AppCompatImageView) {
        GlideApp.with(imageView.context)
            .load(url)
            .circleCrop()
            .into(imageView)
    }

    fun loadEmoji(
        url: String?,
        emojiSize: Float?,
        textView: AppCompatTextView
    ) {
        val emojiTextSize =
            emojiSize?.toInt() ?: textView.resources.getDimensionPixelSize(R.dimen.emoji_text_size)
        GlideApp.with(textView.context)
            .load(url)
            .into(object : CustomTarget<Drawable>(emojiTextSize, emojiTextSize) {

                override fun onLoadCleared(placeholder: Drawable?) {
                    textView.text = null
                }

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    textView.singleDrawableAsSpan(resource)
                }

            })
    }

}