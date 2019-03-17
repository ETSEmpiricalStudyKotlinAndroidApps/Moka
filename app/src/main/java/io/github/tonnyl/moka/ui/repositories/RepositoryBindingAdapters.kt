package io.github.tonnyl.moka.ui.repositories

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

object RepositoryBindingAdapters {

    @JvmStatic
    @BindingAdapter("repositoryLanguageDrawableColor")
    fun repositoryLanguageDrawableColor(
            textView: AppCompatTextView,
            color: String?
    ) {
        (textView.compoundDrawablesRelative[0] as? GradientDrawable)?.setColor(
                color?.let {
                    Color.parseColor(it)
                } ?: run {
                    Color.BLACK
                }
        )

    }

}