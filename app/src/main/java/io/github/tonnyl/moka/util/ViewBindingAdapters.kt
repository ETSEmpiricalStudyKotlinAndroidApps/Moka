package io.github.tonnyl.moka.util

import android.view.View
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.PrecomputedTextCompat
import androidx.core.widget.TextViewCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.tonnyl.moka.net.GlideLoader

object ViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("avatarUrl")
    fun avatarUrl(
            imageView: AppCompatImageView,
            url: String?
    ) {
        GlideLoader.loadAvatar(url, imageView)
    }

    @JvmStatic
    @BindingAdapter("imageResId")
    fun imageResId(
            imageView: AppCompatImageView,
            @DrawableRes drawableResId: Int
    ) {
        imageView.setImageResource(drawableResId)
    }

    @JvmStatic
    @BindingAdapter("backgroundResId")
    fun backgroundResId(
            imageView: AppCompatImageView,
            @DrawableRes backgroundResId: Int
    ) {
        imageView.setBackgroundResource(backgroundResId)
    }

    @JvmStatic
    @BindingAdapter("colorSchemaColors")
    fun colorSchemaColors(
            refreshLayout: SwipeRefreshLayout,
            colorResIds: IntArray
    ) {
        refreshLayout.setColorSchemeColors(*colorResIds)
    }

    @JvmStatic
    @BindingAdapter("textFuture")
    fun textFuture(
            textView: AppCompatTextView,
            text: CharSequence?
    ) {
        text ?: return
        textView.setTextFuture(PrecomputedTextCompat.getTextFuture(
                text,
                TextViewCompat.getTextMetricsParams(textView),
                null
        ))
    }

    @JvmStatic
    @BindingAdapter("invisibleUnless")
    fun invisibleUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("goneUnless")
    fun goneUnless(view: View, visible: Boolean) {
        view.visibility = if (visible) View.VISIBLE else View.GONE
    }

}