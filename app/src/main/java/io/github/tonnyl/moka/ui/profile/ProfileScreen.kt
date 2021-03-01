package io.github.tonnyl.moka.ui.profile

import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.users.UsersType
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import kotlinx.datetime.Instant

@ExperimentalMaterialApi
@Composable
fun ProfileScreen(
    login: String,
    profileType: ProfileType,
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val currentLoginUser by mainViewModel.currentUser.observeAsState()
    val viewModel = viewModel<ProfileViewModel>(factory = ViewModelFactory(login, profileType))
    val organization by viewModel.organizationProfile.observeAsState()
    val user by viewModel.userProfile.observeAsState()
    val followState by viewModel.followState.observeAsState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        when {
            (user == null && organization == null)
                    || user?.status == Status.LOADING
                    || organization?.status == Status.LOADING -> {
                LoadingScreen()
            }
            user?.data != null
                    || organization?.data != null -> {
                ProfileScreenContent(
                    topAppBarSize = topAppBarSize,
                    navController = navController,
                    currentLoginUser = currentLoginUser?.login,
                    user = user?.data,
                    organization = organization?.data,
                    follow = followState?.data,
                    getEmojiByName = mainViewModel::getEmojiByName,
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

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.profile_title)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_arrow_back_24)
                        )
                    }
                )
            },
            actions = {
                val userValue = user?.data
                if (userValue?.isViewer == true) {
                    IconButton(
                        onClick = {
                            navController.navigate(
                                route = Screen.EditProfile.route
                                    .replace(
                                        "{${Screen.ARG_EDIT_PROFILE_NAME}}",
                                        userValue.name ?: ""
                                    )
                                    .replace(
                                        "{${Screen.ARG_EDIT_PROFILE_BIO}}",
                                        userValue.bio ?: ""
                                    )
                                    .replace(
                                        "{${Screen.ARG_EDIT_PROFILE_URL}}",
                                        userValue.websiteUrl?.toString() ?: ""
                                    )
                                    .replace(
                                        "{${Screen.ARG_EDIT_PROFILE_COMPANY}}",
                                        userValue.company ?: ""
                                    )
                                    .replace(
                                        "{${Screen.ARG_EDIT_PROFILE_LOCATION}}",
                                        userValue.location ?: ""
                                    )
                                    .replace(
                                        "{${Screen.ARG_EDIT_PROFILE_TWITTER}}",
                                        userValue.twitterUsername ?: ""
                                    )
                            )
                        }
                    ) {
                        Icon(
                            contentDescription = stringResource(id = R.string.edit_profile_title),
                            painter = painterResource(id = R.drawable.ic_edit_24)
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

@ExperimentalMaterialApi
@Composable
private fun ProfileScreenContent(
    topAppBarSize: Int,
    navController: NavController,
    currentLoginUser: String?,
    user: User?,
    organization: Organization?,
    follow: Boolean?,
    getEmojiByName: (String) -> SearchableEmoji?,
    viewModel: ProfileViewModel? = null
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(all = ContentPaddingLargeSize)
            ) {
                CoilImage(
                    contentDescription = stringResource(id = R.string.users_avatar_content_description),
                    request = createAvatarLoadRequest(
                        url = user?.avatarUrl?.toString()
                            ?: organization?.avatarUrl?.toString()
                    ),
                    modifier = Modifier
                        .size(size = 92.dp)
                        .clip(shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(weight = 1f)
                        .align(alignment = Alignment.CenterVertically)
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
                    Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
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
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    OutlinedButton(
                        onClick = { viewModel?.toggleFollow() },
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
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
        }
        item {
            if (user != null) {
                Box(
                    modifier = Modifier.padding(horizontal = ContentPaddingLargeSize),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        border = BorderStroke(
                            width = DividerSize,
                            color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
                        ),
                        elevation = 0.dp,
                        backgroundColor = if (user.status?.indicatesLimitedAvailability == true) {
                            userStatusDndYellow.copy(alpha = .2f)
                        } else {
                            MaterialTheme.colors.surface
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .clickable(onClick = {
                                if (user.isViewer
                                    && user.status != null
                                ) {
                                    navController.navigate(
                                        route = Screen.EditStatus.route
                                            .replace(
                                                "{${Screen.ARG_EDIT_STATUS_EMOJI}}",
                                                user.status.emoji ?: ""
                                            )
                                            .replace(
                                                "{${Screen.ARG_EDIT_STATUS_MESSAGE}}",
                                                user.status.message ?: ""
                                            )
                                            .replace(
                                                "{${Screen.ARG_EDIT_STATUS_LIMIT_AVAILABILITY}}",
                                                user.status.indicatesLimitedAvailability.toString()
                                            )
                                    )
                                }
                            })
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            EmojiComponent(
                                emoji = user.status?.emoji,
                                getEmojiByName = getEmojiByName
                            )
                            val scrollState = rememberScrollState()
                            Row(modifier = Modifier.horizontalScroll(state = scrollState)) {
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
        }
        item {
            (user?.bio ?: organization?.description)?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(all = ContentPaddingLargeSize)
                )
            }
        }
        item {
            if (user != null) {
                Row(modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)) {
                    NumberCategoryText(
                        number = user.repositoriesTotalCount,
                        category = stringResource(id = R.string.profile_repositories),
                        onClick = {
                            navController.navigate(
                                route = Screen.Repositories.route
                                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", user.login)
                                    .replace(
                                        "{${Screen.ARG_REPOSITORY_TYPE}}",
                                        RepositoryType.OWNED.name
                                    )
                                    .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.USER.name)
                            )
                        },
                        modifier = Modifier.weight(weight = 1f)
                    )
                    NumberCategoryText(
                        number = user.starredRepositoriesTotalCount,
                        category = stringResource(id = R.string.profile_stars),
                        onClick = {
                            navController.navigate(
                                route = Screen.Repositories.route
                                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", user.login)
                                    .replace(
                                        "{${Screen.ARG_REPOSITORY_TYPE}}",
                                        RepositoryType.STARRED.name
                                    )
                                    .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.USER.name)
                            )
                        },
                        modifier = Modifier.weight(weight = 1f)
                    )
                    NumberCategoryText(
                        number = user.followersTotalCount,
                        category = stringResource(id = R.string.profile_followers),
                        onClick = {
                            navController.navigate(
                                route = Screen.Users.route
                                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", user.login)
                                    .replace("{${Screen.ARG_USERS_TYPE}}", UsersType.FOLLOWER.name)
                            )
                        },
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
                Row(modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)) {
                    NumberCategoryText(
                        number = user.followingTotalCount,
                        category = stringResource(id = R.string.profile_following),
                        onClick = {
                            navController.navigate(
                                route = Screen.Users.route
                                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", user.login)
                                    .replace("{${Screen.ARG_USERS_TYPE}}", UsersType.FOLLOWING.name)
                            )
                        },
                        modifier = Modifier.weight(weight = 1f)
                    )
                    NumberCategoryText(
                        number = user.projectsTotalCount,
                        category = stringResource(id = R.string.repository_projects),
                        onClick = { },
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
            } else if (organization != null) {
                Row(modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)) {
                    NumberCategoryText(
                        number = organization.repositoriesTotalCount,
                        category = stringResource(id = R.string.profile_repositories),
                        onClick = { },
                        modifier = Modifier.weight(weight = 1f)
                    )
                    NumberCategoryText(
                        number = organization.projectsTotalCount,
                        category = stringResource(id = R.string.repository_projects),
                        onClick = { },
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
            }
        }
        item {
            (user?.pinnedItems ?: organization?.pinnedItems)?.let { list ->
                if (list.isNotEmpty()) {
                    Column {
                        CategoryText(textRes = R.string.profile_pinned)
                        LazyRow {
                            items(count = list.size) { index ->
                                val item = list[index]
                                if (item is RepositoryItem) {
                                    PinnedRepositoryCard(
                                        navController = navController,
                                        repository = item,
                                        index = index
                                    )
                                } else if (item is Gist2) {
                                    PinnedGistCard(
                                        gist = item,
                                        index = index
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            CategoryText(textRes = R.string.profile_contact)
        }
        item {
            if (user != null) {
                ContactListItem(
                    iconRes = R.drawable.ic_group_24,
                    primaryTextRes = R.string.profile_company,
                    secondaryText = user.company
                        ?: stringResource(id = R.string.no_description_provided)
                )
            }
        }
        item {
            ContactListItem(
                iconRes = R.drawable.ic_email_24,
                primaryTextRes = R.string.profile_email,
                secondaryText = user?.email
                    ?: organization?.email
                    ?: stringResource(id = R.string.no_description_provided)
            )
        }
        item {
            ContactListItem(
                iconRes = R.drawable.ic_location_on_24,
                primaryTextRes = R.string.profile_location,
                secondaryText = user?.location
                    ?: organization?.location
                    ?: stringResource(id = R.string.no_description_provided)
            )
        }
        item {
            ContactListItem(
                iconRes = R.drawable.ic_link_24,
                primaryTextRes = R.string.profile_website,
                secondaryText = user?.url?.toString()
                    ?: organization?.url?.toString()
                    ?: stringResource(id = R.string.no_description_provided)
            )
        }
        item {
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
}

@ExperimentalMaterialApi
@Composable
private fun ContactListItem(
    @DrawableRes iconRes: Int,
    @StringRes primaryTextRes: Int,
    secondaryText: String
) {
    ListItem(
        icon = {
            Icon(
                contentDescription = stringResource(id = primaryTextRes),
                painter = painterResource(id = iconRes),
                modifier = Modifier
                    .size(size = IconSize)
                    .padding(all = ContentPaddingMediumSize)
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
fun PinnedItemSmallIcon(
    @DrawableRes resId: Int,
    @StringRes contentDescriptionId: Int
) {
    Icon(
        contentDescription = stringResource(id = contentDescriptionId),
        painter = painterResource(id = resId),
        modifier = Modifier.size(size = RepositoryCardIconSize)
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
            width = DividerSize,
            color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
        ),
        elevation = 0.dp,
        modifier = Modifier
            .width(width = 320.dp)
            .padding(
                start = if (index == 0) {
                    ContentPaddingLargeSize
                } else {
                    0.dp
                },
                top = ContentPaddingLargeSize,
                end = ContentPaddingLargeSize,
                bottom = ContentPaddingLargeSize
            )
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(all = ContentPaddingLargeSize)) {
            CoilImage(
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                request = createAvatarLoadRequest(url = avatarUrl),
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
            )
            Spacer(modifier = Modifier.size(size = ContentPaddingLargeSize))
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = title,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
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
                Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        content = children
                    )
                }
            }
        }
    }
}

@Composable
private fun PinnedRepositoryCard(
    navController: NavController,
    repository: RepositoryItem,
    index: Int
) {
    PinnedItemCard(
        onClick = {
            navController.navigate(
                route = Screen.Repository.route
                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", repository.owner.login)
                    .replace("{${Screen.ARG_REPOSITORY_NAME}}", repository.name)
                    .replace(
                        "{${Screen.ARG_PROFILE_TYPE}}",
                        ProfileType.NOT_SPECIFIED.name
                    )
            )
        },
        avatarUrl = repository.owner.avatarUrl,
        title = repository.nameWithOwner,
        caption = repository.description.takeIf {
            !it.isNullOrEmpty()
        } ?: stringResource(id = R.string.no_description_provided),
        index = index
    ) {
        Box(
            modifier = Modifier
                .size(size = RepositoryCardLanguageDotSize)
                .background(
                    color = repository.primaryLanguage?.color
                        ?.toColor()
                        ?.let { Color(it) }
                        ?: MaterialTheme.colors.onBackground,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(
            text = repository.primaryLanguage?.name
                ?: stringResource(id = R.string.programming_language_unknown)
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_star_secondary_text_color_18,
            contentDescriptionId = R.string.repository_language_color_content_description
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(text = repository.stargazersCount.formatWithSuffix())
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_code_fork_secondary_text_color_18,
            contentDescriptionId = R.string.repository_stargazers
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(text = repository.forksCount.formatWithSuffix())
    }
}

@Composable
private fun PinnedGistCard(
    gist: Gist2,
    index: Int
) {
    val context = LocalContext.current
    PinnedItemCard(
        onClick = {
            context.safeStartActivity(
                Intent(Intent.ACTION_VIEW, gist.url).apply {
                    putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
                    putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
                }
            )
        },
        avatarUrl = gist.owner?.avatarUrl,
        title = gist.firstFileName ?: gist.name,
        caption = gist.firstFileText.takeIf { !it.isNullOrEmpty() }
            ?: gist.description.takeIf { !it.isNullOrEmpty() }
            ?: stringResource(id = R.string.no_description_provided),
        index = index
    ) {
        PinnedItemSmallIcon(
            resId = R.drawable.ic_comment_secondary_text_color_18,
            contentDescriptionId = R.string.repository_language_color_content_description
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(text = gist.stargazersTotalCount.formatWithSuffix())
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_star_secondary_text_color_18,
            contentDescriptionId = R.string.repository_stargazers
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(text = gist.forksTotalCount.formatWithSuffix())
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_code_fork_secondary_text_color_18,
            contentDescriptionId = R.string.repository_forks
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
        PinnedItemIconifiedText(text = gist.commentsTotalCount.formatWithSuffix())
    }
}

// Preview section start

@ExperimentalMaterialApi
@Preview(showBackground = true, name = "ProfileScreen")
@Composable
private fun ProfileScreenPreview() {
    ProfileScreenContent(
        navController = rememberNavController(),
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
        },
        topAppBarSize = 0
    )
}

// Preview section end