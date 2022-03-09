package io.github.tonnyl.moka.ui.file.download

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.util.downloadFileViaDownloadManager

/**
 * @param url Decoded url.
 */
@Composable
fun DownloadFileDialog(url: String) {
    val currentAccount = LocalAccountInstance.current ?: return
    val navController = LocalNavController.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            navController.navigateUp()
        },
        properties = DialogProperties(usePlatformDefaultWidth = true),
        confirmButton = {
            TextButton(
                onClick = {
                    context.downloadFileViaDownloadManager(
                        accessToken = currentAccount.signedInAccount.accessToken.accessToken,
                        url = url
                    )

                    navController.navigateUp()
                }
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = navController::navigateUp) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        title = {
            Text(text = stringResource(id = R.string.download_file_alert_title))
        },
        text = {
            Text(text = stringResource(id = R.string.download_file_alert_message))
        }
    )
}