package io.github.tonnyl.moka.ui.account.manage

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.AvatarImage
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.tonnyl.moka.common.AccountInstance
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
fun ManageAccountsScreen() {
    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val scaffoldState = rememberScaffoldState()

        val mainViewModel = LocalMainViewModel.current

        val context = LocalContext.current

        val accounts by mainViewModel.getApplication<MokaApp>()
            .accountInstancesLiveData
            .observeAsState(initial = emptyList())

        Scaffold(scaffoldState = scaffoldState) {
            ManageAccountsScreenContent(
                contentPadding = contentPaddings,
                accounts = accounts
            )
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.manage_accounts))
            },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            actions = {
                if (accounts.size < 3) {
                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, AuthActivity::class.java))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(id = R.string.accounts_add_another_account)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )

    }
}

@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
private fun ManageAccountsScreenContent(
    contentPadding: PaddingValues,
    accounts: List<AccountInstance>
) {
    val navController = LocalNavController.current

    LazyColumn(
        contentPadding = contentPadding,
        modifier = Modifier.fillMaxSize()
    ) {
        items(count = accounts.size) {
            ListItem(
                icon = {
                    AvatarImage(
                        url = accounts[it].signedInAccount.account.avatarUrl,
                        modifier = Modifier
                            .size(size = IconSize)
                            .clip(shape = CircleShape)
                    )
                },
                secondaryText = {
                    Text(
                        text = accounts[it].signedInAccount.account.id.toString(),
                        style = MaterialTheme.typography.body2,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailing = {
                    IconButton(
                        onClick = {
                            navController.navigate(
                                route = Screen.LogOutConfirmDialog.route
                                    .replace(
                                        "{${Screen.ARG_ACCOUNT_ID}}",
                                        accounts[it].signedInAccount.account.id.toString()
                                    )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(id = R.string.manage_accounts_logout)
                        )
                    }
                }
            ) {
                Text(
                    text = accounts[it].signedInAccount.account.login,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}