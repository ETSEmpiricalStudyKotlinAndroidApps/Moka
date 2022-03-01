package io.github.tonnyl.moka.ui.exception

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.AppBarHeight
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.SnackBarErrorMessage

/**
 * @param details Url decoded string.
 */
@Composable
fun ExceptionDetailsScreen(details: String) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() },
            applyBottom = false,
            additionalBottom = AppBarHeight
        )

        val scaffoldState = rememberScaffoldState()

        var back by remember { mutableStateOf(false) }
        var displaySnackBar by remember { mutableStateOf(false) }

        if (back) {
            LocalNavController.current.navigateUp()
            back = false
        }

        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    Box(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .padding(bottom = AppBarHeight)
                    ) {
                        Snackbar(snackbarData = data)
                    }
                }
            }
        ) {
            Box(contentAlignment = Alignment.BottomCenter) {
                Text(
                    text = details,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state = scrollState)
                        .padding(paddingValues = contentPaddings)
                        .padding(horizontal = ContentPaddingLargeSize)
                )

                if (displaySnackBar) {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        messageId = R.string.report_succeeded,
                        dismissAction = {
                            back = true
                        }
                    )
                }

                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.background.copy(alpha = .9f))
                        .navigationBarsPadding()
                        .height(height = AppBarHeight)
                        .padding(horizontal = ContentPaddingLargeSize)
                ) {
                    TextButton(
                        enabled = !back && !displaySnackBar,
                        onClick = {
                            FirebaseCrashlytics.getInstance()
                                .recordException(Exception(details))

                            displaySnackBar = true
                        }
                    ) {
                        Text(text = stringResource(id = R.string.report_to_moka_team))
                    }
                }
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.exception_details))
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