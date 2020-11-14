package io.github.tonnyl.moka.ui.profile.edit

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R

@Composable
fun EditProfileScreen(scrollState: ScrollState) {
    val viewModel = viewModel<EditProfileViewModel>()
    val name by viewModel.name.observeAsState()
    val bio by viewModel.bio.observeAsState()
    val url by viewModel.url.observeAsState()
    val company by viewModel.company.observeAsState()
    val location by viewModel.location.observeAsState()
    val twitterUsername by viewModel.twitterUsername.observeAsState()

    EditProfileScreenContent(
        scrollState = scrollState,
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
}

@Composable
private fun EditProfileScreenContent(
    scrollState: ScrollState,

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
    val padding = dimensionResource(id = R.dimen.fragment_content_padding)
    ScrollableColumn(scrollState = scrollState) {
        Spacer(modifier = Modifier.preferredHeight(padding))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_person_24),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            OutlinedTextField(
                value = name ?: "",
                onValueChange = {
                    updateName.invoke(it.trim())
                },
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_name))
                },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(padding))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_info_24),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            OutlinedTextField(
                value = bio ?: "",
                onValueChange = {
                    updateBio.invoke(it.trim())
                },
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_bio))
                },
                modifier = Modifier.weight(1f)
            )
        }
        UserInputHelperText(helperResId = R.string.edit_profile_bio_caption)
        Spacer(modifier = Modifier.preferredHeight(padding))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_link_24),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            OutlinedTextField(
                value = url ?: "",
                onValueChange = {
                    updateUrl.invoke(it.trim())
                },
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_url))
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(padding))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_group_24),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            OutlinedTextField(
                value = company ?: "",
                onValueChange = {
                    updateCompany.invoke(it.trim())
                },
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_company))
                },
                modifier = Modifier.weight(1f)
            )
        }
        UserInputHelperText(helperResId = R.string.edit_profile_company_caption)
        Spacer(modifier = Modifier.preferredHeight(padding))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_location_on_24),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            OutlinedTextField(
                value = location ?: "",
                onValueChange = {
                    updateLocation.invoke(it.trim())
                },
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_location))
                },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(padding))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = padding)
        ) {
            Icon(
                asset = vectorResource(id = R.drawable.ic_twitter_24),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.preferredWidth(padding))
            OutlinedTextField(
                value = twitterUsername ?: "",
                onValueChange = {
                    updateTwitterUsername.invoke(it.trim())
                },
                label = {
                    Text(text = stringResource(id = R.string.edit_profile_twitter_username))
                },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.preferredHeight(padding))
    }
}

@Composable
private fun UserInputHelperText(@StringRes helperResId: Int) {
    Providers(AmbientContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = helperResId),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.regular_icon_size_plus_double_padding),
                top = dimensionResource(id = R.dimen.fragment_content_padding_half)
            )
        )
    }
}

@Preview(showBackground = true, name = "EditProfileScreenContent")
@Composable
private fun EditProfileScreenContentPreview() {
    EditProfileScreenContent(
        scrollState = rememberScrollState(),
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