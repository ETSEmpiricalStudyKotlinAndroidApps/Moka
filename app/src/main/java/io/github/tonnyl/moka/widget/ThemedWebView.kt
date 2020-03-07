package io.github.tonnyl.moka.widget

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import io.github.tonnyl.moka.util.isDarkModeOn

class ThemedWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : WebView(context, attrs, defStyle) {

    init {
        isScrollbarFadingEnabled = true
        settings.javaScriptEnabled = false
        settings.builtInZoomControls = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.domStorageEnabled = true
        settings.setSupportZoom(false)
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        settings.setAppCacheEnabled(false)
    }

    fun loadData(content: String) {
        val data = if (resources.isDarkModeOn) {
            content.replaceFirst("github_light.css", "github_dark.css")
        } else {
            content
        }

        loadDataWithBaseURL(
            "file:///android_asset/",
            data,
            "text/html",
            "UTF-8",
            null
        )
    }

}