package io.github.tonnyl.moka.ui.profile.edit

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.network.Status
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalComposeUiApi
@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun EditProfileScreen(
    initialName: String?,
    initialBio: String?,
    initialUrl: String?,
    initialCompany: String?,
    initialLocation: String?,
    initialTwitter: String?
) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<EditProfileViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[EditProfileViewModel.EDIT_PROFILE_VIEW_MODEL_EXTRA_KEY] =
                EditProfileViewModelExtra(
                    accountInstance = currentAccount,
                    initialName = initialName,
                    initialBio = initialBio,
                    initialUrl = initialUrl,
                    initialCompany = initialCompany,
                    initialLocation = initialLocation,
                    initialTwitter = initialTwitter
                )
        }
    )

    val scaffoldState = rememberScaffoldState()

    val updateState by viewModel.loadingStatus.observeAsState()

    val name by viewModel.name.observeAsState()
    val bio by viewModel.bio.observeAsState()
    val url by viewModel.url.observeAsState()
    val company by viewModel.company.observeAsState()
    val location by viewModel.location.observeAsState()
    val twitterUsername by viewModel.twitterUsername.observeAsState()

    Box(modifier = Modifier.navigationBarsPadding()) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val navController = LocalNavController.current

        Scaffold(
            content = {
                EditProfileScreenContent(
                    topAppBarSize = topAppBarSize,
                    name = name,
                    updateName = { viewModel.updateLocal(name = it) },
                    bio = bio,
                    updateBio = { viewModel.updateLocal(bio = it) },
                    url = url,
                    updateUrl = { viewModel.updateLocal(url = url) },
                    company = company,
                    updateCompany = { viewModel.updateLocal(company = it) },
                    location = location,
                    updateLocation = { viewModel.updateLocal(location = it) },
                    twitterUsername = twitterUsername,
                    updateTwitterUsername = { viewModel.updateLocal(twitterUsername = it) }
                )

                when (updateState?.status) {
                    Status.ERROR -> {
                        SnackBarErrorMessage(
                            scaffoldState = scaffoldState,
                            action = viewModel::updateUserInformation,
                            dismissAction = viewModel::onErrorDismissed
                        )
                    }
                    Status.SUCCESS -> {
                        navController.navigateUp()
                    }
                    else -> {

                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        )

        val keyboardController = LocalSoftwareKeyboardController.current
        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.edit_profile_title))
            },
            navigationIcon = {
                AppBarNavigationIcon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(id = R.string.navigate_close)
                )
            },
            actions = {
                val enabled = name != initialName
                        || bio != initialBio
                        || url != initialUrl
                        || company != initialCompany
                        || location != initialLocation
                        || twitterUsername != initialTwitter

                if (updateState?.status == Status.LOADING) {
                    LottieLoadingComponent(modifier = Modifier.size(size = IconSize))
                } else {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            if (enabled) {
                                viewModel.updateUserInformation()
                            } else {
                                navController.navigateUp()
                            }
                        },
                        // ☹️ actions of TopAppBar have set emphasis internally...
                        // So in theory, setting enabled won't change the appearance at all.
                        enabled = enabled
                    ) {
                        Icon(
                            contentDescription = stringResource(id = R.string.done_image_description),
                            imageVector = Icons.Outlined.Check
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

@Composable
private fun EditProfileScreenContent(
    topAppBarSize: Int,

    name: String?,
    updateName: (String) -> Unit,

    bio: String?,
    updateBio: (String) -> Unit,

    url: String?,
    updateUrl: (String) -> Unit,

    company: String?,
    updateCompany: (String) -> Unit,

    location: String?,
    updateLocation: (String) -> Unit,

    twitterUsername: String?,
    updateTwitterUsername: (String) -> Unit
) {
    LazyColumn(
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.edit_profile_name),
                    imageVector = Icons.Outlined.Person,
                    modifier = Modifier
                        .size(size = IconSize)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = ContentPaddingMediumSize)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedTextField(
                    value = name ?: "",
                    onValueChange = {
                        updateName.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_name))
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.edit_profile_bio),
                    imageVector = Icons.Outlined.Info,
                    modifier = Modifier
                        .size(size = IconSize)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = ContentPaddingMediumSize)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedTextField(
                    value = bio ?: "",
                    onValueChange = {
                        updateBio.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_bio))
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
        item {
            UserInputHelperText(helperResId = R.string.edit_profile_bio_caption)
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.edit_profile_url),
                    painter = painterResource(id = R.drawable.ic_link_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = ContentPaddingMediumSize)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedTextField(
                    value = url ?: "",
                    onValueChange = {
                        updateUrl.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_url))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.edit_profile_company),
                    painter = painterResource(id = R.drawable.ic_group_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = ContentPaddingMediumSize)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedTextField(
                    value = company ?: "",
                    onValueChange = {
                        updateCompany.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_company))
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
        item {
            UserInputHelperText(helperResId = R.string.edit_profile_company_caption)
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.edit_profile_location),
                    imageVector = Icons.Outlined.LocationOn,
                    modifier = Modifier
                        .size(size = IconSize)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = ContentPaddingMediumSize)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedTextField(
                    value = location ?: "",
                    onValueChange = {
                        updateLocation.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_location))
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ContentPaddingLargeSize)
            ) {
                Icon(
                    contentDescription = stringResource(id = R.string.edit_profile_twitter_username),
                    painter = painterResource(id = R.drawable.ic_twitter_24),
                    modifier = Modifier
                        .size(size = IconSize)
                        .align(alignment = Alignment.CenterVertically)
                        .padding(all = ContentPaddingMediumSize)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedTextField(
                    value = twitterUsername ?: "",
                    onValueChange = {
                        updateTwitterUsername.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_profile_twitter_username))
                    },
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
        }
    }
}

@Composable
private fun UserInputHelperText(@StringRes helperResId: Int) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = helperResId),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(
                start = IconSize + ContentPaddingLargeSize * 2,
                top = ContentPaddingMediumSize
            )
        )
    }
}

@Preview(showBackground = true, name = "EditProfileScreenContent")
@Composable
private fun EditProfileScreenContentPreview() {
    EditProfileScreenContent(
        topAppBarSize = 0,
        name = "Li Zhao Tai Lang",
        bio = "Rock/Post-rock/Electronic",
        url = "lizhaotailang.works",
        company = "",
        location = "Guangzhou",
        twitterUsername = "@TonnyLZTL",
        updateName = {},
        updateBio = {},
        updateUrl = {},
        updateCompany = {},
        updateLocation = {},
        updateTwitterUsername = {}
    )
}