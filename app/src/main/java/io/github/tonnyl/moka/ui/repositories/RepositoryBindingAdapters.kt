package io.github.tonnyl.moka.ui.repositories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

@BindingAdapter("repositoryLanguageDrawableColor")
fun AppCompatTextView.repositoryLanguageDrawableColor(
        color: String?
) {
    (compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
            color?.let {
                Color.parseColor(it)
            } ?: run {
                Color.BLACK
            }
    )

}