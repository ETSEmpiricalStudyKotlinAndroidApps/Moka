package io.github.tonnyl.moka.ui.repositories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

@BindingAdapter("repositoryLanguageDrawableColor")
fun AppCompatTextView.repositoryLanguageDrawableColor(
    color: String?
) {
    val colorString = if (color == null) {
        null
    } else if (color.length == 4 && color[0] == '#') {
        "#${color[1]}${color[1]}${color[2]}${color[2]}${color[3]}${color[3]}"
    } else {
        color
    }
    (compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
        color?.let {
            Color.parseColor(colorString)
        } ?: run {
            Color.BLACK
        }
    )

}