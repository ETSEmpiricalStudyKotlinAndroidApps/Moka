package io.github.tonnyl.moka.ui.repository

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.paging.ExperimentalPagingApi
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalComposeUiApi
@ExperimentalSerializationApi
@Composable
fun ForkRepoDialog(
    showState: MutableState<Boolean>,
    fork: () -> Unit,
    repoName: String
) {
    val accounts by LocalMainViewModel.current.getApplication<MokaApp>().accountInstancesLiveData.observeAsState(
        initial = emptyList()
    )
    if (accounts.isNullOrEmpty()) {
        return
    }

    if (showState.value) {
        AlertDialog(
            onDismissRequest = {
                showState.value = false
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
                        showState.value = false
                    }
                ) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        fork.invoke()
                        showState.value = false
                    }
                ) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        )
    }
}