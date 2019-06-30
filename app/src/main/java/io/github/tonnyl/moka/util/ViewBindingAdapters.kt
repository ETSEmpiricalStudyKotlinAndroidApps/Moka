package io.github.tonnyl.moka.util

import android.net.Uri
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.tonnyl.moka.network.GlideLoader

@BindingAdapter("avatarUrl")
fun AppCompatImageView.avatarUrl(
    url: String?
) {
    GlideLoader.loadAvatar(url, this)
}

@BindingAdapter("avatarUrl")
fun AppCompatImageView.avatarUrl(
    url: Uri?
) {
    avatarUrl(url?.toString())
}

@BindingAdapter("imageResId")
fun AppCompatImageView.imageResId(
    @DrawableRes drawableResId: Int
) {
    setImageResource(drawableResId)
}

@BindingAdapter("backgroundResId")
fun AppCompatImageView.backgroundResId(
    @DrawableRes backgroundResId: Int
) {
    setBackgroundResource(backgroundResId)
}

@BindingAdapter("colorSchemaColors")
fun SwipeRefreshLayout.colorSchemaColors(
    colorResIds: IntArray
) {
    setColorSchemeColors(*colorResIds)
}

@BindingAdapter("textFuture")
fun AppCompatTextView.textFuture(
    text: CharSequence?
) {
    text ?: return
    setTextFuture(
        PrecomputedTextCompat.getTextFuture(
            text,
            TextViewCompat.getTextMetricsParams(this),
            null
        )
    )
}

@BindingAdapter("invisibleUnless")
fun View.invisibleUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("goneUnless")
fun View.goneUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("visibleUnless")
fun View.visibleUnless(visible: Boolean) {
    visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("intOrZero")
fun AppCompatTextView.intOrZero(
    value: Int?
) {
    text = value?.toString() ?: "0"
}