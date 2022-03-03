package io.github.tonnyl.moka.ui.feedback

import android.os.Build
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun FeedbackConfirmDialog() {
    LocalAccountInstance.current ?: return

    val navController = LocalNavController.current

    AlertDialog(
        onDismissRequest = {
            navController.navigateUp()
        },
        text = {
            Text(text = stringResource(id = R.string.feedback_confirm_message))
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    Screen.CreateIssue.navigate(
                        navController = navController,
                        repoId = FEEDBACK_REPO_ID,
                        defaultComment = BASIC_FEEDBACK_INFO
                    ) {
                        popUpTo(
                            route = navController.currentBackStackEntry?.destination?.route
                                ?: return@navigate
                        ) {
                            inclusive = true
                        }
                    }
                }
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    )
}

private const val FEEDBACK_REPO_ID = "R_kgDOG8G2Pg"

private val BASIC_FEEDBACK_INFO by lazy {
    """
    Manufacturer: ${Build.BRAND}
    Brand: ${Build.BRAND}
    Model: ${Build.MODEL}
    SDK: ${Build.VERSION.RELEASE}
    Version: ${BuildConfig.VERSION_NAME}
    """.trimIndent()
}