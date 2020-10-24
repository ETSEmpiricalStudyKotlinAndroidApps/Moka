package io.github.tonnyl.moka.ui.emojis

import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.webkit.URLUtil
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import io.github.tonnyl.moka.network.ImageLoader

@BindingAdapter(
    "emoji",
    "emojiPlaceholder",
    "emojiTextSize",
    requireAll = false
)
fun AppCompatTextView.emoji(
    emoji: String?,
    emojiPlaceholder: Drawable?,
    emojiTextSize: Float?
) {
    if (emoji.isNullOrEmpty()) {
        if (emojiPlaceholder != null) {
            singleDrawableAsSpan(emojiPlaceholder)
        }
        return
    }

    if (URLUtil.isValidUrl(emoji)) {
        ImageLoader.loadEmoji(emoji, emojiTextSize, this)
    } else {
        text = emoji
    }
}

@BindingAdapter("singleDrawableAsSpan")
fun AppCompatTextView.singleDrawableAsSpan(
    singleDrawable: Drawable?
) {
    if (singleDrawable == null) {
        return
    }

    singleDrawable.setBounds(
        0,
        0,
        singleDrawable.intrinsicWidth,
        singleDrawable.intrinsicHeight
    )
    val span = SpannableString("e")
    val imageSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ImageSpan(singleDrawable, ImageSpan.ALIGN_CENTER)
    } else {
        VerticalImageSpan(singleDrawable)
    }
    span.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    text = span
}

@BindingAdapter("checkedButtonId")
fun MaterialButtonToggleGroup.checkedButtonId(@IdRes id: Int) {
    check(id)
}