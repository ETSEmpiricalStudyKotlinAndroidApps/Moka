package io.github.tonnyl.moka.widget

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import io.github.tonnyl.moka.databinding.AuthWebViewBinding

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewComposable(
    urlToLoad: String,
    modifier: Modifier = Modifier,
    shouldOverrideUrlLoading: ((WebView?, WebResourceRequest?) -> Boolean)? = null,
    onPageFinished: ((WebView?, String?) -> Unit)? = null
) {
    Box(modifier = modifier) {
        var webView by remember { mutableStateOf<WebView?>(null) }

        DisposableEffect(key1 = webView) {
            webView?.loadUrl(urlToLoad)
            onDispose {
                webView?.stopLoading()
            }
        }

        var progress by remember { mutableStateOf(0f) }
        val refreshColor = MaterialTheme.colors.primary.toArgb()

        // don't use custom WebView,
        // see https://issuetracker.google.com/issues/213674066
        AndroidViewBinding(factory = AuthWebViewBinding::inflate) {
            webView = authWebView

            with(authWebView) {
                isScrollbarFadingEnabled = true
                settings.javaScriptEnabled = true
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

            authWebView.webChromeClient = object : WebChromeClient() {

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    progress = newProgress / 100f
                }

            }

            authWebView.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    onPageFinished?.invoke(webView, url)

                    refresh.isRefreshing = false
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    refresh.isRefreshing = false
                }

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {
                    return shouldOverrideUrlLoading?.invoke(view, request)
                        ?: super.shouldOverrideUrlLoading(view, request)
                }

            }

            refresh.setColorSchemeColors(refreshColor)

            refresh.setOnRefreshListener {
                webView?.reload()
            }
        }

        if (progress != 1f) {
            LinearProgressIndicator(
                progress = progress,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 2.dp)
            )
        }

        if (webView?.canGoBack() == true) {
            BackHandler {
                webView?.goBack()
            }
        }
    }
}