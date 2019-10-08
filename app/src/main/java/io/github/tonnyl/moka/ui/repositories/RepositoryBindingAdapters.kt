package io.github.tonnyl.moka.ui.repositories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import io.github.tonnyl.moka.util.toColor

@BindingAdapter("repositoryLanguageDrawableColor")
fun AppCompatTextView.repositoryLanguageDrawableColor(
    color: String?
) {
    (compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(color?.toColor() ?: Color.BLACK)
}