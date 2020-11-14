package io.github.tonnyl.moka.ui.profile

import android.net.Uri
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRowForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.coil.CoilImage
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import kotlinx.datetime.Instant

@Composable
fun ProfileScreen(
    scrollState: ScrollState,
    currentLoginUser: String?,
    getEmojiByName: (String) -> SearchableEmoji?
) {
    val viewModel = viewModel<ProfileViewModel>()
    val organization by viewModel.organizationProfile.observeAsState()
    val user by viewModel.userProfile.observeAsState()
    val followState by viewModel.followState.observeAsState()

    when {
        (user == null && organization == null)
                || user?.status == Status.LOADING
                || organization?.status == Status.LOADING -> {
            LoadingScreen()
        }
        user?.data != null
                || organization?.data != null -> {
            ProfileScreenContent(
                scrollState = scrollState,
                currentLoginUser = currentLoginUser,
                user = user?.data,
                organization = organization?.data,
                follow = followState?.data,
                getEmojiByName = getEmojiByName,
                viewModel = viewModel
            )
        }
        else -> {
            EmptyScreenContent(
                icon = R.drawable.ic_person_outline_24,
                title = if (user?.status == Status.ERROR) {
                    R.string.user_profile_content_empty_title
                } else {
                    R.string.common_error_requesting_data
                },
                retry = R.string.common_retry,
                action = R.string.user_profile_content_empty_action
            )
        }
    }
}

@Composable
private fun ProfileScreenContent(
    scrollState: ScrollState,
    currentLoginUser: String?,
    user: User?,
    organization: Organization?,
    follow: Boolean?,
    getEmojiByName: (String) -> SearchableEmoji?,
    viewModel: ProfileViewModel? = null
) {
    ScrollableColumn(scrollState = scrollState) {
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(dimensionResource(id = R.dimen.fragment_content_padding))
        ) {
            CoilImage(
                request = createAvatarLoadRequest(
                    url = user?.avatarUrl?.toString()
                        ?: organization?.avatarUrl?.toString()
                ),
                modifier = Modifier.size(dimensionResource(id = R.dimen.profile_avatar_height))
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                (user?.name ?: organization?.name).let { name ->
                    if (!name.isNullOrEmpty()) {
                        Text(
                            text = name,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.h6
                        )
                    }
                }
                Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding_half)))
                (user?.login ?: organization?.login).let {
                    if (!it.isNullOrEmpty()) {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (user != null && user.login != currentLoginUser) {
                Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
                OutlinedButton(
                    onClick = { viewModel?.toggleFollow() },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text(
                        text = stringResource(
                            id = if (follow == true) {
                                R.string.user_profile_unfollow
                            } else {
                                R.string.user_profile_follow
                            }
                        )
                    )
                }
            }
        }
        if (user != null) {
            Box(
                modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding)),
                alignment = Alignment.Center
            ) {
                Card(
                    border = BorderStroke(
                        width = dimensionResource(id = R.dimen.divider_size),
                        color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
                    ),
                    elevation = 0.dp,
                    backgroundColor = if (user.status?.indicatesLimitedAvailability == true) {
                        colorResource(id = R.color.yellow).copy(alpha = .2f)
                    } else {
                        MaterialTheme.colors.surface
                    },
                    modifier = Modifier.fillMaxWidth()
                        .preferredHeight(dimensionResource(id = R.dimen.user_profile_status_card_height))
                        .clickable(onClick = { viewModel?.editStatus() })
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiComponent(
                            emoji = user.status?.emoji,
                            getEmojiByName = getEmojiByName,
                            modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.user_profile_status_card_height))
                        )
                        ScrollableRow {
                            Text(
                                text = user.status?.message.takeIf { !it.isNullOrEmpty() }
                                    ?: stringResource(id = R.string.edit_status_set_status),
                                style = MaterialTheme.typography.body1,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
        (user?.bio ?: organization?.description)?.let {
            Text(
                text = it,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.fragment_content_padding))
            )
        }
        if (user != null) {
            Row(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))) {
                NumberCategoryText(
                    number = user.repositoriesTotalCount,
                    category = stringResource(id = R.string.profile_repositories),
                    onClick = { viewModel?.viewRepositories() },
                    modifier = Modifier.weight(1f)
                )
                NumberCategoryText(
                    number = user.starredRepositoriesTotalCount,
                    category = stringResource(id = R.string.profile_stars),
                    onClick = { viewModel?.viewStars() },
                    modifier = Modifier.weight(1f)
                )
                NumberCategoryText(
                    number = user.followersTotalCount,
                    category = stringResource(id = R.string.profile_followers),
                    onClick = { viewModel?.viewFollowers() },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))) {
                NumberCategoryText(
                    number = user.followingTotalCount,
                    category = stringResource(id = R.string.profile_following),
                    onClick = { viewModel?.viewFollowings() },
                    modifier = Modifier.weight(1f)
                )
                NumberCategoryText(
                    number = user.projectsTotalCount,
                    category = stringResource(id = R.string.repository_projects),
                    onClick = { viewModel?.viewProjects() },
                    modifier = Modifier.weight(1f)
                )
            }
        } else if (organization != null) {
            Row(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))) {
                NumberCategoryText(
                    number = organization.repositoriesTotalCount,
                    category = stringResource(id = R.string.profile_repositories),
                    onClick = { viewModel?.viewRepositories() },
                    modifier = Modifier.weight(1f)
                )
                NumberCategoryText(
                    number = organization.projectsTotalCount,
                    category = stringResource(id = R.string.repository_projects),
                    onClick = { viewModel?.viewProjects() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        (user?.pinnedItems ?: organization?.pinnedItems)?.let { list ->
            if (list.isNotEmpty()) {
                Column {
                    CategoryText(textRes = R.string.profile_pinned)
                    LazyRowForIndexed(items = list) { index, item ->
                        if (item is RepositoryItem) {
                            PinnedRepositoryCard(
                                viewModel = viewModel,
                                repository = item,
                                index = index
                            )
                        } else if (item is Gist2) {
                            PinnedGistCard(
                                viewModel = viewModel,
                                gist = item,
                                index = index
                            )
                        }
                    }
                }
            }
        }
        CategoryText(textRes = R.string.profile_contact)
        if (user != null) {
            ContactListItem(
                iconRes = R.drawable.ic_group_24,
                primaryTextRes = R.string.profile_company,
                secondaryText = user.company
                    ?: stringResource(id = R.string.no_description_provided)
            )
        }
        ContactListItem(
            iconRes = R.drawable.ic_email_24,
            primaryTextRes = R.string.profile_email,
            secondaryText = user?.email
                ?: organization?.email
                ?: stringResource(id = R.string.no_description_provided)
        )
        ContactListItem(
            iconRes = R.drawable.ic_location_on_24,
            primaryTextRes = R.string.profile_location,
            secondaryText = user?.location
                ?: organization?.location
                ?: stringResource(id = R.string.no_description_provided)
        )
        ContactListItem(
            iconRes = R.drawable.ic_link_24,
            primaryTextRes = R.string.profile_website,
            secondaryText = user?.url?.toString()
                ?: organization?.url?.toString()
                ?: stringResource(id = R.string.no_description_provided)
        )
        if (user != null) {
            CategoryText(textRes = R.string.profile_info)
            InfoListItem(
                leadingRes = R.string.profile_joined_on,
                trailing = DateUtils.getRelativeTimeSpanString(
                    user.createdAt.toEpochMilliseconds(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString()
            )
            InfoListItem(
                leadingRes = R.string.profile_updated_on,
                trailing = DateUtils.getRelativeTimeSpanString(
                    user.updatedAt.toEpochMilliseconds(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString()
            )
        }
    }
}

@Composable
private fun ContactListItem(
    @DrawableRes iconRes: Int,
    @StringRes primaryTextRes: Int,
    secondaryText: String
) {
    ListItem(
        icon = {
            Icon(
                asset = vectorResource(id = iconRes),
                modifier = Modifier.size(dimensionResource(id = R.dimen.regular_icon_size))
            )
        },
        secondaryText = {
            Text(text = secondaryText)
        },
        singleLineSecondaryText = true
    ) {
        Text(text = stringResource(id = primaryTextRes))
    }
}

@Composable
private fun PinnedItemSmallIcon(@DrawableRes resId: Int) {
    Icon(
        asset = vectorResource(id = resId),
        modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.repository_card_icon_size))
    )
}

@Composable
private fun PinnedItemIconifiedText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption
    )
}

@Composable
private fun PinnedItemCard(
    onClick: () -> Unit,
    avatarUrl: Uri?,
    title: String,
    caption: String,
    index: Int,
    children: @Composable RowScope.() -> Unit
) {
    Card(
        border = BorderStroke(
            width = dimensionResource(id = R.dimen.divider_size),
            color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
        ),
        elevation = 0.dp,
        modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.user_profile_pinned_item_card_width))
            .padding(
                start = if (index == 0) {
                    dimensionResource(id = R.dimen.fragment_content_padding)
                } else {
                    0.dp
                },
                top = dimensionResource(id = R.dimen.fragment_content_padding),
                end = dimensionResource(id = R.dimen.fragment_content_padding),
                bottom = dimensionResource(id = R.dimen.fragment_content_padding)
            )
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(dimensionResource(id = R.dimen.fragment_content_padding))) {
            CoilImage(
                request = createAvatarLoadRequest(url = avatarUrl),
                modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.regular_icon_size))
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.fragment_content_padding)))
            Column {
                Providers(AmbientContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = title,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.preferredHeight(dimensionResource(id = R.dimen.fragment_content_padding_half)))
                    Text(
                        text = stringResource(
                            id = R.string.user_profile_pinned_item_caption_format,
                            caption
                        ),
                        style = MaterialTheme.typography.body2,
                        maxLines = 3,
                        overflow = TextOverflow.Clip
                    )
                }
                Spacer(modifier = Modifier.preferredHeight(dimensionResource(id = R.dimen.fragment_content_padding_half)))
                Providers(AmbientContentAlpha provides ContentAlpha.medium) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        children = children
                    )
                }
            }
        }
    }
}

@Composable
private fun PinnedRepositoryCard(
    viewModel: ProfileViewModel?,
    repository: RepositoryItem,
    index: Int
) {
    PinnedItemCard(
        onClick = { viewModel?.viewRepository(repository) },
        avatarUrl = repository.owner.avatarUrl,
        title = repository.nameWithOwner,
        caption = repository.description.takeIf {
            !it.isNullOrEmpty()
        } ?: stringResource(id = R.string.no_description_provided),
        index = index
    ) {
        Box(
            modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.repository_list_language_color_width))
                .background(
                    color = repository.primaryLanguage?.color?.toColor()?.let { Color(it) }
                        ?: MaterialTheme.colors.onBackground,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.list_content_padding)))
        PinnedItemIconifiedText(
            text = repository.primaryLanguage?.name
                ?: stringResource(id = R.string.programming_language_unknown)
        )
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
        PinnedItemSmallIcon(resId = R.drawable.ic_star_secondary_text_color_18)
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.list_content_padding)))
        PinnedItemIconifiedText(text = repository.stargazersCount.formatWithSuffix())
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
        PinnedItemSmallIcon(resId = R.drawable.ic_code_fork_secondary_text_color_18)
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.list_content_padding)))
        PinnedItemIconifiedText(text = repository.forksCount.formatWithSuffix())
    }
}

@Composable
private fun PinnedGistCard(
    viewModel: ProfileViewModel?,
    gist: Gist2,
    index: Int
) {
    PinnedItemCard(
        onClick = { viewModel?.viewGist(gist) },
        avatarUrl = gist.owner?.avatarUrl,
        title = gist.firstFileName ?: gist.name,
        caption = gist.firstFileText.takeIf { !it.isNullOrEmpty() }
            ?: gist.description.takeIf { !it.isNullOrEmpty() }
            ?: stringResource(id = R.string.no_description_provided),
        index = index
    ) {
        PinnedItemSmallIcon(resId = R.drawable.ic_comment_secondary_text_color_18)
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.list_content_padding)))
        PinnedItemIconifiedText(text = gist.stargazersTotalCount.formatWithSuffix())
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
        PinnedItemSmallIcon(resId = R.drawable.ic_star_secondary_text_color_18)
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.list_content_padding)))
        PinnedItemIconifiedText(text = gist.forksTotalCount.formatWithSuffix())
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
        PinnedItemSmallIcon(resId = R.drawable.ic_code_fork_secondary_text_color_18)
        Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding_half)))
        PinnedItemIconifiedText(text = gist.commentsTotalCount.formatWithSuffix())
    }
}

// Preview section start

@Preview(showBackground = true, name = "ProfileScreen")
@Composable
private fun ProfileScreenPreview() {
    ProfileScreenContent(
        scrollState = rememberScrollState(),
        currentLoginUser = "",
        user = User(
            avatarUrl = Uri.parse("https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4"),
            bio = "Rock/Post-rock/Electronic",
            bioHTML = "<div>Rock/Post-rock/Electronic</div>",
            company = null,
            companyHTML = "",
            createdAt = Instant.fromEpochMilliseconds(1436861097L),
            email = "lizhaotailang@gmail.com",
            id = "MDQ6VXNlcjEzMzI5MTQ4",
            isBountyHunter = false,
            isCampusExpert = false,
            isDeveloperProgramMember = true,
            isEmployee = false,
            isHireable = false,
            isSiteAdmin = false,
            isViewer = true,
            location = "Guangzhou",
            login = "TonnyL",
            name = "Li Zhao Tai Lang",
            resourcePath = Uri.parse("/TonnyL"),
            status = UserStatus(
                createdAt = Instant.fromEpochMilliseconds(1592643813L),
                emoji = ":dart:",
                expiresAt = null,
                id = "3209515",
                indicatesLimitedAvailability = true,
                message = "Focusing",
                updatedAt = Instant.fromEpochMilliseconds(1592643813L)
            ),
            updatedAt = Instant.fromEpochMilliseconds(1600415355L),
            url = Uri.parse("https://github.com/TonnyL"),
            viewerCanFollow = false,
            viewerIsFollowing = false,
            websiteUrl = Uri.parse("https://tonnyl.io"),
            twitterUsername = "@TonnyLZTL",
            repositoriesTotalCount = 37,
            followersTotalCount = 890,
            followingTotalCount = 111,
            starredRepositoriesTotalCount = 268,
            projectsTotalCount = 0,
            pinnedItems = mutableListOf(
                RepositoryItem(
                    "📚 PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
                    "<div>\n<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. </div>",
                    homepageUrl = null,
                    id = "MDEwOlJlcG9zaXRvcnk1NDIxMjM1NQ==",
                    isArchived = false,
                    isFork = false,
                    isLocked = false,
                    isMirror = false,
                    isPrivate = false,
                    mirrorUrl = null,
                    name = "PaperPlane",
                    nameWithOwner = "TonnyL/PaperPlane",
                    owner = RepositoryOwner(
                        avatarUrl = Uri.parse("https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4"),
                        id = "MDQ6VXNlcjEzMzI5MTQ4",
                        login = "TonnyL",
                        resourcePath = Uri.parse("/TonnyL"),
                        url = Uri.parse("https://github.com/TonnyL")
                    ),
                    parent = null,
                    primaryLanguage = Language(
                        color = "#F18E33",
                        id = "MDg6TGFuZ3VhZ2UyNzI=",
                        name = "Kotlin"
                    ),
                    shortDescriptionHTML = "<g-emoji class=\"g-emoji\" alias=\"books\" fallback-src=\"https://github.githubassets.com/images/icons/emoji/unicode/1f4da.png\">📚</g-emoji> PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
                    url = Uri.parse("https://github.com/TonnyL/PaperPlane"),
                    viewerHasStarred = false,
                    forksCount = 287,
                    stargazersCount = 1145
                ),
                Gist2(
                    createdAt = Instant.fromEpochMilliseconds(1573833346L),
                    description = "",
                    id = "MDQ6R2lzdGEzN2U5YTM3MGU0OGI5MDlhMzgzZDhlOTBiMzM5Y2Jk",
                    isFork = false,
                    isPublic = true,
                    name = "a37e9a370e48b909a383d8e90b339cbd",
                    owner = RepositoryOwner(
                        avatarUrl = Uri.parse("https://avatars3.githubusercontent.com/u/28293513?u=d7546e7c81e3ec8d39bac67dc7ac57e3fed1b244&v=4"),
                        id = "MDQ6VXNlcjI4MjkzNTEz",
                        login = "lizhaotailang",
                        resourcePath = Uri.parse("/lizhaotailang"),
                        url = Uri.parse("https://github.com/lizhaotailang")
                    ),
                    pushedAt = Instant.fromEpochMilliseconds(1573833347L),
                    resourcePath = Uri.parse("a37e9a370e48b909a383d8e90b339cbd"),
                    updatedAt = Instant.fromEpochMilliseconds(1592647150),
                    url = Uri.parse("https://gist.github.com/a37e9a370e48b909a383d8e90b339cbd"),
                    viewerHasStarred = true,
                    commentsTotalCount = 0,
                    forksTotalCount = 0,
                    stargazersTotalCount = 0,
                    firstFileName = "cryptocurrency_symbols.json",
                    firstFileText = "[\n  {\n    \"currency\": \"Bitcoin\",\n    \"abbreviation\": \"BTC\"\n  },\n  {\n    \"currency\": \"Ethereum\",\n    "
                )
            )
        ),
        organization = null,
        follow = false,
        getEmojiByName = {
            SearchableEmoji(
                emoji = "🎯",
                name = ":dart:",
                category = "Activities"
            )
        }
    )
}

// Preview section end