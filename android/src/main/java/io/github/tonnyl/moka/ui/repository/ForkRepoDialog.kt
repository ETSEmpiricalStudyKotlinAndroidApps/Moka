package io.github.tonnyl.moka.ui.repository

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalComposeUiApi
@ExperimentalSerializationApi
@Composable
fun ForkRepoDialog(
    login: String,
    repoName: String
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            RepositoryViewModel(
                extra = RepositoryViewModelExtra(
                    accountInstance = currentAccount,
                    login = login,
                    repositoryName = repoName
                )
            )
        }
    )

    val navController = LocalNavController.current
    AlertDialog(
        onDismissRequest = {
            navController.navigateUp()
        },
        properties = DialogProperties(usePlatformDefaultWidth = true),
        title = {
            Text(text = stringResource(id = R.string.repository_fork_alert_title, repoName))
        },
        text = {
            Text(text = stringResource(id = R.string.repository_fork_alert_message))
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
                    viewModel.fork()
                    navController.navigateUp()
                }
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        }
    )
}