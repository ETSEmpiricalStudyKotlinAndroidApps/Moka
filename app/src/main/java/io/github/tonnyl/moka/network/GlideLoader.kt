package io.github.tonnyl.moka.network

import android.widget.ImageView
import io.github.tonnyl.moka.GlideApp

object GlideLoader {

    fun loadAvatar(url: String?, imageView: ImageView) {
        GlideApp.with(imageView.context)
                .load(url)
                .circleCrop()
                .into(imageView)
    }

}