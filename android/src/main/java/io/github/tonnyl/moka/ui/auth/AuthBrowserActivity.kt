package io.github.tonnyl.moka.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalWindowInsetsController
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.WebViewComposable
import io.tonnyl.moka.common.network.KtorClient
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
class AuthBrowserActivity : ComponentActivity() {

    @OptIn(
        ExperimentalSerializationApi::class,
        ExperimentalAnimatedInsets::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val urlToLoad = intent.getStringExtra(ARG_URL)
        require(!urlToLoad.isNullOrEmpty())

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val windowInsetsControllerCompat =
                remember { WindowInsetsControllerCompat(window, window.decorView) }

            CompositionLocalProvider(LocalWindowInsetsController provides windowInsetsControllerCompat) {
                MokaTheme {
                    Surface {
                        var topAppBarSize by remember { mutableStateOf(0) }
                        var title by remember { mutableStateOf("") }

                        Scaffold {
                            WebViewComposable(
                                urlToLoad = urlToLoad,
                                shouldOverrideUrlLoading = { _, webResourceRequest ->
                                    val requestUrl = webResourceRequest?.url

                                    if (requestUrl?.toString()
                                            ?.startsWith(KtorClient.GITHUB_AUTHORIZE_CALLBACK_URI) == true
                                    ) {
                                        val code = requestUrl.getQueryParameter("code")
                                        val state = requestUrl.getQueryParameter("state")

                                        val haveResult = !code.isNullOrEmpty()
                                                && !state.isNullOrEmpty()

                                        if (haveResult) {
                                            setResult(
                                                Activity.RESULT_OK,
                                                Intent().apply {
                                                    putExtra(
                                                        RESULT_AUTH_RESULT,
                                                        AuthParameter(
                                                            code = code!!,
                                                            state = state!!
                                                        )
                                                    )
                                                }
                                            )

                                            finish()
                                        }

                                        haveResult
                                    } else {
                                        false
                                    }
                                },
                                onPageFinished = { webView, _ ->
                                    title = webView?.title.takeIf { !it.isNullOrEmpty() } ?: ""
                                },
                                modifier = Modifier.padding(
                                    paddingValues = rememberInsetsPaddingValues(
                                        insets = LocalWindowInsets.current.systemBars,
                                        applyTop = false,
                                        additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
                                    )
                                )
                            )

                            InsetAwareTopAppBar(
                                title = {
                                    Text(
                                        text = title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                navigationIcon = {
                                    IconButton(
                                        onClick = { finish() },
                                        content = {
                                            Icon(
                                                contentDescription = stringResource(id = R.string.navigate_up),
                                                imageVector = Icons.Outlined.Close
                                            )
                                        }
                                    )
                                },
                                elevation = 0.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onSizeChanged { topAppBarSize = it.height }
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {

        const val ARG_URL = "arg_url"

        const val RESULT_AUTH_RESULT = "result_auth_result"

    }

}