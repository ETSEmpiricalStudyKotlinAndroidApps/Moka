package io.github.tonnyl.moka.ui.account

import android.accounts.Account
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.coil.CoilImage
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.auth.AuthActivity
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.widget.OutlineChip
import kotlinx.datetime.Clock

@ExperimentalMaterialApi
@Composable
fun AccountDialogScreen(
    navController: NavController,
    showState: MutableState<Boolean>
) {
    val accounts by (LocalContext.current.applicationContext as MokaApp).loginAccounts.observeAsState()
    if (showState.value) {
        Dialog(
            onDismissRequest = {
                showState.value = false
            }
        ) {
            AccountDialogScreenContent(
                navController = navController,
                accounts = accounts ?: emptyList()
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun AccountDialogScreenContent(
    navController: NavController,
    accounts: List<Triple<Account, String, AuthenticatedUser>>
) {
    val context = LocalContext.current

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
            itemsIndexed(items = accounts) { index, triple ->
                ItemAccount(
                    navController = navController,
                    isCurrentLoginUser = index == 0,
                    account = triple.third
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
                }
        )
        ListItem(
            icon = {
                Icon(
                    contentDescription = stringResource(id = R.string.accounts_manage_your_github_account),
                    painter = painterResource(id = R.drawable.ic_person_settings),
                    modifier = Modifier
                        .size(size = IconSize)
                        .padding(all = ContentPaddingMediumSize)
                )
            },
            text = {
                Text(text = stringResource(id = R.string.accounts_manage_your_github_account))
            },
            modifier = Modifier
                .constrainAs(ref = manageAccountRef) {
                    centerHorizontallyTo(other = parent)
                    top.linkTo(anchor = addNewAccountRef.bottom)
                }
                .fillMaxWidth()
                .clickable {
                    context.safeStartActivity(Intent(Settings.ACTION_SYNC_SETTINGS))
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

                    }
                    .padding(all = ContentPaddingMediumSize)
            )
        }
    }
}

@Composable
private fun ItemAccount(
    navController: NavController,
    isCurrentLoginUser: Boolean,
    account: AuthenticatedUser
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .clickable(enabled = !isCurrentLoginUser) {

            }
            .padding(
                start = ContentPaddingLargeSize,
                end = ContentPaddingLargeSize,
                top = ContentPaddingLargeSize,
                bottom = ContentPaddingMediumSize
            )
    ) {
        CoilImage(
            contentDescription = stringResource(id = R.string.accounts_avatar_of_account),
            request = createAvatarLoadRequest(url = account.avatarUrl),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column(modifier = Modifier.weight(weight = 1f)) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                Text(
                    text = account.login,
                    style = MaterialTheme.typography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = account.id.toString(),
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
                        navController.navigate(
                            route = Screen.Profile.route
                                .replace("{${Screen.ARG_PROFILE_LOGIN}}", account.login)
                                .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.USER.name)
                        )
                    }
                )
            }
        }
    }
}


@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    name = "AccountDialogScreenContentPreview"
)
@Composable
private fun AccountDialogScreenContentPreview() {
    AccountDialogScreenContent(
        navController = rememberNavController(),
        accounts = listOf(
            Triple(
                Account("", ""),
                "",
                AuthenticatedUser(
                    login = "TonnyL",
                    id = 0L,
                    nodeId = "",
                    avatarUrl = "",
                    htmlUrl = "https://github.com/TonnyL",
                    type = "user",
                    siteAdmin = true,
                    name = "Li Zhao Tai Lang",
                    company = null,
                    blog = "https://tonnyl.io/",
                    location = "Guangzhou",
                    email = "lizhaotailang@gmail.com",
                    hireable = null,
                    bio = "Rock/Post-rock/Electronic.",
                    publicRepos = 35,
                    publicGists = 3,
                    followers = 894,
                    following = 115,
                    createdAt = Clock.System.now(),
                    updatedAt = Clock.System.now(),
                    privateGists = 2,
                    totalPrivateRepos = 2,
                    ownedPrivateRepos = 2,
                    diskUsage = 0L,
                    collaborators = 9,
                    twoFactorAuthentication = false
                )
            )
        )
    )
}