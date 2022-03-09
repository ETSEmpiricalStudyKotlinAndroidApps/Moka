package io.github.tonnyl.moka.ui.account.manage

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.res.stringResource
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalNavController

@Composable
fun LogoutConfirmDialog(accountId: Long) {
    val navController = LocalNavController.current

    val mainViewModel = LocalMainViewModel.current
    val accounts by mainViewModel.getApplication<MokaApp>()
        .accountInstancesLiveData
        .observeAsState(initial = emptyList())
    val account = accounts.firstOrNull { it.signedInAccount.account.id == accountId } ?: return

    AlertDialog(
        onDismissRequest = {
            navController.navigateUp()
        },
        title = {
            Text(
                text = stringResource(
                    id = R.string.manage_accounts_logout_alert_title,
                    account.signedInAccount.account.login
                )
            )
        },
        text = {
            Text(text = stringResource(id = R.string.manage_accounts_logout_alert_message))
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
                    mainViewModel.deleteAccount(account = account.signedInAccount)
                }
            ) {
                Text(text = stringResource(id = R.string.manage_accounts_logout))
            }
        }
    )
}