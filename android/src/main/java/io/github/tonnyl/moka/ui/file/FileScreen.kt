package io.github.tonnyl.moka.ui.file

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.tonnyl.moka.common.network.Status
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun FileScreen(
    login: String,
    repoName: String,
    filePath: String,
    filename: String,
    fileExtension: String?
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val requestUrl = "https://raw.githubusercontent.com/$login/$repoName/${filePath}/${filename}"
    val viewModel = viewModel<FileViewModel>(
        factory = ViewModelFactory(
            context = LocalContext.current,
            accountInstance = currentAccount,
            url = requestUrl,
            filename = filename,
            fileExtension = fileExtension
        ),
        key = requestUrl
    )

    Box(modifier = Modifier.fillMaxSize()) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val fileResource by viewModel.file.observeAsState()

        SwipeRefresh(
            state = rememberSwipeRefreshState(
                isRefreshing = fileResource == null
                        || fileResource?.status == Status.LOADING
            ),
            onRefresh = viewModel::geFileContent,
            indicatorPadding = contentPadding,
            indicator = { state, refreshTriggerDistance ->
                DefaultSwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = refreshTriggerDistance
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            val fileContent = fileResource?.data
            when {
                fileContent != null -> {
                    FileScreenContent(
                        contentPadding = contentPadding,
                        fileContent = fileContent.first,
                        lang = fileContent.second
                    )
                }
                fileResource?.status == Status.ERROR -> { // todo display more error info and help user to download the raw file.
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
            }
        }

        val navController = LocalNavController.current

        val showMenuState = remember { mutableStateOf(false) }

        InsetAwareTopAppBar(
            title = {
                Text(text = filename)
            },
            actions = {
                Box {
                    IconButton(
                        onClick = {
                            showMenuState.value = true
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_more_vert_24),
                            contentDescription = stringResource(id = R.string.notification_filters)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenuState.value,
                        onDismissRequest = {
                            showMenuState.value = false
                        },
                        offset = DpOffset(
                            x = 0.dp,
                            y = (-56).dp // @see AppBarHeight
                        )
                    ) {
                        val context = LocalContext.current
                        val fullUrl =
                            "https://github.com/$login/$repoName/blob/${filePath}/${filename}"
                        DropdownMenuItem(
                            onClick = {
                                showMenuState.value = false

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, fullUrl)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, null)
                                context.safeStartActivity(shareIntent)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.share))
                        }
                        DropdownMenuItem(
                            onClick = {
                                showMenuState.value = false

                                CustomTabsIntent.Builder()
                                    .build()
                                    .launchUrl(context, Uri.parse(fullUrl))
                            }
                        ) {
                            Text(text = stringResource(id = R.string.open_in_browser))
                        }
                    }
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_arrow_back_24)
                        )
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun FileScreenContent(
    contentPadding: PaddingValues,
    fileContent: String,
    lang: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState(), enabled = true)
            .padding(paddingValues = contentPadding)
    ) {
        var webView by remember { mutableStateOf<WebView?>(null) }

        val context = LocalContext.current
        DisposableEffect(key1 = webView) {
            val css = if (context.resources.isDarkModeOn) {
                "github-dark.min.css"
            } else {
                "github.min.css"
            }

            val codeTag = if (lang.isNullOrEmpty()) {
                "<code>"
            } else {
                "<code class=language-${lang}>"
            }

            webView?.loadDataWithBaseURL(
                "file:///android_asset/highlight/",
                """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="utf-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1">
                        <link rel="stylesheet" type="text/css" href="styles/$css">
                        <link rel="stylesheet" type="text/css" href="styles/highlightjs-line-numbers.css">
                        <script type="text/javascript" src="highlight.min.js"></script>
                        <script type="text/javascript" src="highlightjs-line-numbers.js"></script>
                        <script>hljs.highlightAll();</script>
                        <script>hljs.initLineNumbersOnLoad();</script>
                    </head>
                    <body>
                    <pre>
                    $codeTag 
                    $fileContent
                    </code>
                    </pre>
                    </body>
                    </html>
                """.trimIndent(),
                "text/html",
                "UTF-8",
                null
            )
            onDispose {
                webView?.stopLoading()
            }
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx)
            },
            modifier = Modifier.fillMaxSize()
        ) {
            webView = it

            with(it) {
                isScrollbarFadingEnabled = true
                settings.javaScriptEnabled = true
                settings.builtInZoomControls = false
                settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                settings.domStorageEnabled = true
                settings.setSupportZoom(false)
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                isVerticalScrollBarEnabled = true
                isHorizontalScrollBarEnabled = false
                settings.setAppCacheEnabled(false)
            }
        }
    }
}