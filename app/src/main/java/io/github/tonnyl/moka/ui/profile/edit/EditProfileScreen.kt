package io.github.tonnyl.moka.ui.profile.edit

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.LottieLoadingComponent
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalMaterialApi
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
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            name = initialName,
            bio = initialBio,
            url = initialUrl,
            company = initialCompany,
            location = initialLocation,
            twitter = initialTwitter
        )
    )

    val scaffoldState = rememberScaffoldState()

    val updateState by viewModel.loadingStatus.observeAsState()

    val name by viewModel.name.observeAsState()
    val bio by viewModel.bio.observeAsState()
    val url by viewModel.url.observeAsState()
    val company by viewModel.company.observeAsState()
    val location by viewModel.location.observeAsState()
    val twitterUsername by viewModel.twitterUsername.observeAsState()

    Box {
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
                            action = viewModel::updateUserInformation
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
                    Snackbar(snackbarData = data)
                }
            },
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.edit_profile_title))
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_close_24)
                        )
                    }
                )
            },
            actions = {
                val enabled = name != initialName
                        || bio != initialBio
                        || url != initialUrl
                        || company != initialCompany
                        || location != initialLocation
                        || twitterUsername != initialTwitter
                IconButton(
                    onClick = {
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
                    if (updateState?.status == Status.LOADING) {
                        LottieLoadingComponent()
                    } else {
                        Icon(
                            contentDescription = stringResource(id = R.string.done_image_description),
                            painter = painterResource(id = R.drawable.ic_check_24)
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
                    painter = painterResource(id = R.drawable.ic_person_24),
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
                    painter = painterResource(id = R.drawable.ic_info_24),
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
                    painter = painterResource(id = R.drawable.ic_location_on_24),
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
        url = "tonnyl.io",
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