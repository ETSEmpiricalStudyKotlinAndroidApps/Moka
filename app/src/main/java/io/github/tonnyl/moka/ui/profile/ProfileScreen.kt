package io.github.tonnyl.moka.ui.profile

import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.*
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.users.UsersType
import io.github.tonnyl.moka.util.UserProvider
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val currentAccount = LocalAccountInstance.current ?: return
    val organization by viewModel.organizationProfile.observeAsState()
    val user by viewModel.userProfile.observeAsState()
    val followState by viewModel.followState.observeAsState()

    val userPlaceholder = remember {
        UserProvider().values.first()
    }

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val isLoading = (user == null && organization == null)
                || user?.status == Status.LOADING
                || organization?.status == Status.LOADING
        when {
            isLoading
                    || user?.data != null
                    || organization?.data != null -> {
                val mainViewModel = LocalMainViewModel.current
                ProfileScreenContent(
                    topAppBarSize = topAppBarSize,
                    currentLoginUser = currentAccount.signedInAccount.account.login,
                    user = userPlaceholder.takeIf { isLoading } ?: user?.data,
                    organization = organization?.data,
                    follow = followState?.data,
                    getEmojiByName = mainViewModel::getEmojiByName,
                    viewModel = viewModel,
                    enablePlaceholder = isLoading
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

        val navController = LocalNavController.current
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
                                        userValue.websiteUrl ?: ""
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

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
private fun ProfileScreenContent(
    topAppBarSize: Int,
    currentLoginUser: String,
    user: User?,
    organization: Organization?,
    follow: Boolean?,
    enablePlaceholder: Boolean,
    getEmojiByName: (String) -> SearchableEmoji?,
    viewModel: ProfileViewModel? = null
) {
    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .verticalScroll(state = rememberScrollState())
            .padding(
                paddingValues = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.systemBars,
                    applyTop = false,
                    additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
                )
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(all = ContentPaddingLargeSize)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = user?.avatarUrl ?: organization?.avatarUrl,
                    builder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                modifier = Modifier
                    .size(size = 92.dp)
                    .clip(shape = CircleShape)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
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
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                (user?.login ?: organization?.login).let {
                    if (enablePlaceholder) {
                        Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                    }
                    if (!it.isNullOrEmpty()) {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                    }
                }
            }
            if (user != null
                && user.login != currentLoginUser
            ) {
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                OutlinedButton(
                    enabled = !enablePlaceholder,
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
                        ),
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
            }
        }

        if (user != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(horizontal = ContentPaddingLargeSize)
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
                    onClick = {
                        if (enablePlaceholder) {
                            return@Card
                        }

                        if (user.isViewer) {
                            var route = Screen.EditStatus.route

                            user.status?.let { userStatus ->
                                if (!userStatus.emoji.isNullOrEmpty()) {
                                    route = route.replace(
                                        "{${Screen.ARG_EDIT_STATUS_EMOJI}}",
                                        userStatus.emoji
                                    )
                                }
                                if (!userStatus.message.isNullOrEmpty()) {
                                    route = route.replace(
                                        "{${Screen.ARG_EDIT_STATUS_MESSAGE}}",
                                        userStatus.message
                                    )
                                }
                                route = route.replace(
                                    "{${Screen.ARG_EDIT_STATUS_LIMIT_AVAILABILITY}}",
                                    user.status.indicatesLimitedAvailability.toString()
                                )
                            }
                            navController.navigate(route = route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiComponent(
                            emoji = user.status?.emoji,
                            getEmojiByName = getEmojiByName,
                            enablePlaceholder = enablePlaceholder
                        )
                        val scrollState = rememberScrollState()
                        Row(modifier = Modifier.horizontalScroll(state = scrollState)) {
                            Text(
                                text = user.status?.message.takeIf { !it.isNullOrEmpty() }
                                    ?: stringResource(id = R.string.edit_status_set_status),
                                style = MaterialTheme.typography.body1,
                                maxLines = 1,
                                modifier = Modifier.placeholder(
                                    visible = enablePlaceholder,
                                    highlight = PlaceholderHighlight.fade()
                                )
                            )
                        }
                    }
                }
            }
        }

        (user?.bio ?: organization?.description)?.let {
            if (it.isNotEmpty()) {
                Text(
                    text = it,
                    maxLines = if (enablePlaceholder) {
                        1
                    } else {
                        Int.MAX_VALUE
                    },
                    modifier = Modifier
                        .padding(all = ContentPaddingLargeSize)
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
            }
        }

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
                    enablePlaceholder = enablePlaceholder,
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
                    enablePlaceholder = enablePlaceholder,
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
                    enablePlaceholder = enablePlaceholder,
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
                    enablePlaceholder = enablePlaceholder,
                    modifier = Modifier.weight(weight = 1f)
                )
                NumberCategoryText(
                    number = user.projectsTotalCount,
                    category = stringResource(id = R.string.repository_projects),
                    onClick = { },
                    enablePlaceholder = enablePlaceholder,
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        } else if (organization != null) {
            Row(modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)) {
                NumberCategoryText(
                    number = organization.repositoriesTotalCount,
                    category = stringResource(id = R.string.profile_repositories),
                    onClick = { },
                    enablePlaceholder = enablePlaceholder,
                    modifier = Modifier.weight(weight = 1f)
                )
                NumberCategoryText(
                    number = organization.projectsTotalCount,
                    category = stringResource(id = R.string.repository_projects),
                    onClick = { },
                    enablePlaceholder = enablePlaceholder,
                    modifier = Modifier.weight(weight = 1f)
                )
            }
        }

        (user?.pinnedItems ?: organization?.pinnedItems)?.let { list ->
            if (list.isNotEmpty()) {
                Column {
                    CategoryText(
                        textRes = R.string.profile_pinned,
                        enablePlaceholder = enablePlaceholder
                    )
                    LazyRow {
                        items(count = list.size) { index ->
                            val item = list[index]
                            if (item is RepositoryItem) {
                                PinnedRepositoryCard(
                                    navController = navController,
                                    repository = item,
                                    index = index,
                                    enablePlaceholder = enablePlaceholder
                                )
                            } else if (item is Gist2) {
                                PinnedGistCard(
                                    gist = item,
                                    index = index,
                                    enablePlaceholder = enablePlaceholder
                                )
                            }
                        }
                    }
                }
            }
        }

        CategoryText(
            textRes = R.string.profile_contact,
            enablePlaceholder = enablePlaceholder
        )

        if (user != null) {
            ContactListItem(
                iconRes = R.drawable.ic_group_24,
                primaryTextRes = R.string.profile_company,
                secondaryText = user.company
                    ?: stringResource(id = R.string.no_description_provided),
                enablePlaceholder = enablePlaceholder
            )
        }

        ContactListItem(
            iconRes = R.drawable.ic_email_24,
            primaryTextRes = R.string.profile_email,
            secondaryText = user?.email
                ?: organization?.email
                ?: stringResource(id = R.string.no_description_provided),
            enablePlaceholder = enablePlaceholder
        )

        ContactListItem(
            iconRes = R.drawable.ic_location_on_24,
            primaryTextRes = R.string.profile_location,
            secondaryText = user?.location
                ?: organization?.location
                ?: stringResource(id = R.string.no_description_provided),
            enablePlaceholder = enablePlaceholder
        )
        ContactListItem(
            iconRes = R.drawable.ic_link_24,
            primaryTextRes = R.string.profile_website,
            secondaryText = user?.url ?: organization?.url
            ?: stringResource(id = R.string.no_description_provided),
            enablePlaceholder = enablePlaceholder
        )
        if (user != null) {
            CategoryText(
                textRes = R.string.profile_info,
                enablePlaceholder = enablePlaceholder
            )
            InfoListItem(
                leadingRes = R.string.profile_joined_on,
                trailing = DateUtils.getRelativeTimeSpanString(
                    user.createdAt.toEpochMilliseconds(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString(),
                enablePlaceholder = enablePlaceholder
            )
            InfoListItem(
                leadingRes = R.string.profile_updated_on,
                trailing = DateUtils.getRelativeTimeSpanString(
                    user.updatedAt.toEpochMilliseconds(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString(),
                enablePlaceholder = enablePlaceholder
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun ContactListItem(
    @DrawableRes iconRes: Int,
    @StringRes primaryTextRes: Int,
    secondaryText: String,
    enablePlaceholder: Boolean
) {
    ListItem(
        icon = {
            Icon(
                contentDescription = stringResource(id = primaryTextRes),
                painter = painterResource(id = iconRes),
                modifier = Modifier
                    .size(size = IconSize)
                    .padding(all = ContentPaddingMediumSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
        },
        secondaryText = {
            Text(
                text = secondaryText,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        },
        singleLineSecondaryText = true
    ) {
        Text(
            text = stringResource(id = primaryTextRes),
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@Composable
fun PinnedItemSmallIcon(
    @DrawableRes resId: Int,
    @StringRes contentDescriptionId: Int,
    enablePlaceholder: Boolean
) {
    Icon(
        contentDescription = stringResource(id = contentDescriptionId),
        painter = painterResource(id = resId),
        modifier = Modifier
            .size(size = RepositoryCardIconSize)
            .placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
    )
}

@Composable
private fun PinnedItemIconifiedText(
    text: String,
    enablePlaceholder: Boolean
) {
    Text(
        text = text,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.placeholder(
            visible = enablePlaceholder,
            highlight = PlaceholderHighlight.fade()
        )
    )
}

@ExperimentalMaterialApi
@Composable
private fun PinnedItemCard(
    onClick: () -> Unit,
    avatarUrl: String?,
    title: String,
    caption: String,
    index: Int,
    enablePlaceholder: Boolean,
    children: @Composable RowScope.() -> Unit
) {
    Card(
        border = BorderStroke(
            width = DividerSize,
            color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
        ),
        elevation = 0.dp,
        enabled = !enablePlaceholder,
        onClick = onClick,
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
    ) {
        Row(modifier = Modifier.padding(all = ContentPaddingLargeSize)) {
            Image(
                painter = rememberImagePainter(
                    data = avatarUrl,
                    builder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                modifier = Modifier
                    .size(size = IconSize)
                    .clip(shape = CircleShape)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.size(size = ContentPaddingLargeSize))
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = title,
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.body1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                    Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                    Text(
                        text = stringResource(
                            id = R.string.user_profile_pinned_item_caption_format,
                            caption
                        ),
                        style = MaterialTheme.typography.body2,
                        maxLines = 3,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
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

@ExperimentalMaterialApi
@Composable
private fun PinnedRepositoryCard(
    navController: NavController,
    repository: RepositoryItem,
    index: Int,
    enablePlaceholder: Boolean
) {
    PinnedItemCard(
        onClick = {
            if (enablePlaceholder) {
                return@PinnedItemCard
            }
            navController.navigate(
                route = Screen.Repository.route
                    .replace("{${Screen.ARG_PROFILE_LOGIN}}", repository.owner?.login ?: "ghost")
                    .replace("{${Screen.ARG_REPOSITORY_NAME}}", repository.name)
                    .replace(
                        "{${Screen.ARG_PROFILE_TYPE}}",
                        ProfileType.NOT_SPECIFIED.name
                    )
            )
        },
        avatarUrl = repository.owner?.avatarUrl,
        title = repository.nameWithOwner,
        caption = repository.description.takeIf {
            !it.isNullOrEmpty()
        } ?: stringResource(id = R.string.no_description_provided),
        index = index,
        enablePlaceholder = enablePlaceholder
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
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(
            text = repository.primaryLanguage?.name
                ?: stringResource(id = R.string.programming_language_unknown),
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_star_secondary_text_color_18,
            contentDescriptionId = R.string.repository_language_color_content_description,
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(
            text = repository.stargazersCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_code_fork_secondary_text_color_18,
            contentDescriptionId = R.string.repository_stargazers,
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(
            text = repository.forksCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun PinnedGistCard(
    gist: Gist2,
    index: Int,
    enablePlaceholder: Boolean
) {
    val context = LocalContext.current
    PinnedItemCard(
        onClick = {
            context.safeStartActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(gist.url)).apply {
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
        index = index,
        enablePlaceholder = enablePlaceholder
    ) {
        PinnedItemSmallIcon(
            resId = R.drawable.ic_comment_secondary_text_color_18,
            contentDescriptionId = R.string.repository_language_color_content_description,
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(
            text = gist.stargazersTotalCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_star_secondary_text_color_18,
            contentDescriptionId = R.string.repository_stargazers,
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
        PinnedItemIconifiedText(
            text = gist.forksTotalCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        PinnedItemSmallIcon(
            resId = R.drawable.ic_code_fork_secondary_text_color_18,
            contentDescriptionId = R.string.repository_forks,
            enablePlaceholder = enablePlaceholder
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
        PinnedItemIconifiedText(
            text = gist.commentsTotalCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
    }
}

// Preview section start

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Preview(showBackground = true, name = "ProfileScreen")
@Composable
private fun ProfileScreenPreview(
    @PreviewParameter(
        provider = UserProvider::class,
        limit = 1
    )
    user: User?
) {
    ProfileScreenContent(
        currentLoginUser = "",
        user = user,
        organization = null,
        follow = false,
        getEmojiByName = {
            SearchableEmoji(
                emoji = "🎯",
                name = ":dart:",
                category = "Activities"
            )
        },
        topAppBarSize = 0,
        enablePlaceholder = false
    )
}

// Preview section end