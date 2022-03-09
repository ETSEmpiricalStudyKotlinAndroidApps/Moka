package io.github.tonnyl.moka.ui.account

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.about.URL_OF_PRIVACY_POLICY
import io.github.tonnyl.moka.ui.about.URL_OF_TERMS_OF_SERVICE
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.openInBrowser
import io.github.tonnyl.moka.widget.AvatarImage
import io.github.tonnyl.moka.widget.OutlineChip
import io.tonnyl.moka.common.AccountInstance
import io.tonnyl.moka.common.data.ProfileType

@Composable
fun AccountDialogScreen() {
    val accounts by LocalMainViewModel.current.getApplication<MokaApp>().accountInstancesLiveData.observeAsState(
        initial = emptyList()
    )
    if (accounts.isEmpty()) {
        return
    }

    val navController = LocalNavController.current
    Dialog(
        onDismissRequest = {
            navController.navigateUp()
        },
        properties = DialogProperties(usePlatformDefaultWidth = true)
    ) {
        AccountDialogScreenContent(accounts = accounts)
    }
}

@Composable
private fun AccountDialogScreenContent(accounts: List<AccountInstance>) {
    val context = LocalContext.current
    val currentAccount = LocalAccountInstance.current ?: return
    val navController = LocalNavController.current

    ConstraintLayout(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colors.background)
    ) {
        val (accountsRef, addNewAccountRef, manageAccountRef, policyRef, dividerRef, termsOfServiceRef, divider1, divider2) = createRefs()
        LazyColumn(
            modifier = Modifier.constrainAs(ref = accountsRef) {
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = parent.top)
            }
        ) {
            items(count = accounts.size) { index ->
                ItemAccount(
                    isCurrentLoginUser = currentAccount.signedInAccount.account.id == accounts[index].signedInAccount.account.id,
                    account = accounts[index],
                    accountIndex = index
                )
            }
        }
        Divider(
            modifier = Modifier.constrainAs(ref = divider1) {
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = accountsRef.bottom)
            }
        )
        ListItem(
            icon = {
                Icon(
                    contentDescription = stringResource(id = R.string.accounts_add_another_account),
                    painter = painterResource(id = R.drawable.ic_person_add_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .padding(all = ContentPaddingMediumSize)
                )
            },
            text = {
                Text(text = stringResource(id = R.string.accounts_add_another_account))
            },
            modifier = Modifier
                .constrainAs(ref = addNewAccountRef) {
                    centerHorizontallyTo(other = parent)
                    top.linkTo(anchor = divider1.bottom)
                }
                .fillMaxWidth()
                .clickable {
                    context.startActivity(Intent(context, AuthActivity::class.java))

                    navController.navigateUp()
                }
        )
        ListItem(
            icon = {
                Icon(
                    contentDescription = stringResource(id = R.string.accounts_manage_accounts),
                    painter = painterResource(id = R.drawable.ic_person_settings),
                    modifier = Modifier
                        .size(size = IconSize)
                        .padding(all = ContentPaddingMediumSize)
                )
            },
            text = {
                Text(text = stringResource(id = R.string.accounts_manage_accounts))
            },
            modifier = Modifier
                .constrainAs(ref = manageAccountRef) {
                    centerHorizontallyTo(other = parent)
                    top.linkTo(anchor = addNewAccountRef.bottom)
                }
                .fillMaxWidth()
                .clickable {
                    navController.navigate(route = Screen.ManageAccounts.route) {
                        popUpTo(route = navController.currentBackStackEntry?.destination?.route ?: return@navigate) {
                            inclusive = true
                        }
                    }
                }
        )
        Divider(
            modifier = Modifier.constrainAs(ref = divider2) {
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = manageAccountRef.bottom)
            }
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .constrainAs(ref = dividerRef) {
                    centerHorizontallyTo(other = parent)
                    top.linkTo(anchor = divider2.bottom)
                }
                .padding(horizontal = ContentPaddingMediumSize)
                .height(height = IconSize)
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .size(size = ContentPaddingMediumSize)
                    .background(color = MaterialTheme.colors.onBackground.copy(ContentAlpha.medium))
            )
        }

        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.about_privacy_policy),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .constrainAs(ref = policyRef) {
                        centerVerticallyTo(other = dividerRef)
                        end.linkTo(anchor = dividerRef.start)
                    }
                    .clip(shape = MaterialTheme.shapes.medium)
                    .clickable {
                        context.openInBrowser(URL_OF_PRIVACY_POLICY)
                        navController.navigateUp()
                    }
                    .padding(all = ContentPaddingMediumSize)
            )
            Text(
                text = stringResource(id = R.string.about_terms_of_service),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .constrainAs(ref = termsOfServiceRef) {
                        centerVerticallyTo(other = dividerRef)
                        start.linkTo(anchor = dividerRef.end)
                    }
                    .clip(shape = MaterialTheme.shapes.medium)
                    .clickable {
                        context.openInBrowser(URL_OF_TERMS_OF_SERVICE)
                        navController.navigateUp()
                    }
                    .padding(all = ContentPaddingMediumSize)
            )
        }
    }
}

@Composable
private fun ItemAccount(
    isCurrentLoginUser: Boolean,
    account: AccountInstance,
    accountIndex: Int
) {
    val mainViewModel = LocalMainViewModel.current
    val navController = LocalNavController.current
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .clickable(enabled = !isCurrentLoginUser) {
                mainViewModel.moveAccountToFirst(
                    account = account.signedInAccount,
                    index = accountIndex
                )

                navController.navigateUp()
            }
            .padding(
                start = ContentPaddingLargeSize,
                end = ContentPaddingLargeSize,
                top = ContentPaddingLargeSize,
                bottom = ContentPaddingMediumSize
            )
    ) {
        AvatarImage(
            url = account.signedInAccount.account.avatarUrl,
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column(modifier = Modifier.weight(weight = 1f)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = account.signedInAccount.account.login,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = account.signedInAccount.account.id.toString(),
                    style = MaterialTheme.typography.body2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isCurrentLoginUser) {
                Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                OutlineChip(
                    text = stringResource(id = R.string.accounts_view_profile),
                    onClick = {
                        Screen.Profile.navigate(
                            navController = navController,
                            login = account.signedInAccount.account.login,
                            type = ProfileType.USER
                        )
                    }
                )
            }
        }
    }
}


@SuppressLint("UnrememberedMutableState")
@Preview(
    showBackground = true,
    name = "AccountDialogScreenContentPreview"
)
@Composable
private fun AccountDialogScreenContentPreview() {
    AccountDialogScreenContent(accounts = emptyList())
}