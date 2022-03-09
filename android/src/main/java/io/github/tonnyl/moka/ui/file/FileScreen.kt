package io.github.tonnyl.moka.ui.file

import android.annotation.SuppressLint
import android.app.Application
import android.webkit.WebView
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.util.displayExceptionDetails
import io.github.tonnyl.moka.util.downloadFileViaDownloadManager
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.network.Status

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
    val app = LocalContext.current.applicationContext as Application
    val viewModel = viewModel(
        initializer = {
            FileViewModel(
                app = app,
                extra = FileViewModelExtra(
                    accountInstance = currentAccount,
                    url = requestUrl,
                    filename = filename,
                    fileExtension = fileExtension
                )
            )
        },
        key = requestUrl
    )

    Box(modifier = Modifier.fillMaxSize()) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        ) {
            val contentPaddings = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            )

            val fileResource by viewModel.file.observeAsState()

            val redirectedResource by viewModel.redirectedUrl.observeAsState()

            if (redirectedResource != null) {
                if (!redirectedResource!!.data.isNullOrEmpty()) {
                    LocalContext.current.downloadFileViaDownloadManager(
                        url = redirectedResource!!.data!!,
                        accessToken = currentAccount.signedInAccount.accessToken.accessToken
                    )

                    viewModel.onRedirectedDataErrorDismissed()
                } else if (redirectedResource?.e != null) {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        dismissAction = viewModel::onRedirectedDataErrorDismissed
                    )
                }
            }

            SwipeRefresh(
                state = rememberSwipeRefreshState(
                    isRefreshing = fileResource == null
                            || fileResource?.status == Status.LOADING
                ),
                onRefresh = viewModel::geFileContent,
                indicatorPadding = contentPaddings,
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
                            contentPadding = contentPaddings,
                            fileContent = fileContent.first,
                            lang = fileContent.second
                        )
                    }
                    fileResource?.status == Status.ERROR -> {
                        if (fileResource?.e.displayExceptionDetails) {
                            EmptyScreenContent(
                                action = {
                                    viewModel.getRedirectedUrl(url = "https://github.com/$login/$repoName/blob/${filePath}/${filename}?raw=true")
                                },
                                actionId = R.string.download_file_directly,
                                titleId = R.string.download_file_directly_message,
                                throwable = fileResource?.e
                            )
                        } else {
                            EmptyScreenContent(
                                action = viewModel::geFileContent,
                                throwable = fileResource?.e
                            )
                        }
                    }
                }
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = filename)
            },
            actions = {
                ShareAndOpenInBrowserMenu(
                    showMenuState = remember { mutableStateOf(false) },
                    text = "https://github.com/$login/$repoName/blob/${filePath}/${filename}"
                )
            },
            navigationIcon = {
                AppBarNavigationIcon()
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