package io.github.tonnyl.moka.network

import android.widget.ImageView

object GlideLoader {

    fun loadAvatar(url: String?, imageView: ImageView) {
        GlideApp.with(imageView.context)
                .load(url)
                .circleCrop()
                .into(imageView)
    }

}