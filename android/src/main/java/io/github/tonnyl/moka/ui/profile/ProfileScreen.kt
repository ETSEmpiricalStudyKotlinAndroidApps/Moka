package io.github.tonnyl.moka.ui.profile

import android.content.Intent
import android.net.Uri
import android.provider.Browser
import android.text.format.DateUtils
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.ui.users.UsersType
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.*
import io.github.tonnyl.moka.widget.contribution.ContributionCalendar
import io.tonnyl.moka.common.data.SearchableEmoji
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.util.UserProvider
import io.tonnyl.moka.common.util.formatWithSuffix
import io.tonnyl.moka.graphql.fragment.Gist
import io.tonnyl.moka.graphql.fragment.Organization
import io.tonnyl.moka.graphql.fragment.RepositoryListItemFragment
import io.tonnyl.moka.graphql.fragment.User
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalAnimationApi
@ExperimentalCoilApi
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
                    iconVector = Icons.Outlined.Person,
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
                            imageVector = Icons.Outlined.ArrowBack
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
                            imageVector = Icons.Outlined.Edit
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

@ExperimentalAnimationApi
@ExperimentalCoilApi
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
                AnimatedContent(
                    targetState = follow,
                    contentAlignment = Alignment.Center,
                    transitionSpec = {
                        fadeIn() with fadeOut()
                    }
                ) {
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
        }

        if (user != null
            && (user.status != null || user.isViewer)
        ) {
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
                    backgroundColor = if (user.status?.userStatus?.indicatesLimitedAvailability == true) {
                        userStatusDndYellow.copy(alpha = .2f)
                    } else {
                        MaterialTheme.colors.surface
                    },
                    enabled = !enablePlaceholder && user.isViewer,
                    onClick = {
                        if (enablePlaceholder) {
                            return@Card
                        }

                        if (user.isViewer) {
                            var route = Screen.EditStatus.route

                            val userStatus = user.status
                            route = route.replace(
                                "{${Screen.ARG_EDIT_STATUS_EMOJI}}",
                                userStatus?.userStatus?.emoji.toString()
                            )
                                .replace(
                                    "{${Screen.ARG_EDIT_STATUS_MESSAGE}}",
                                    userStatus?.userStatus?.message ?: ""
                                )
                                .replace(
                                    "{${Screen.ARG_EDIT_STATUS_LIMIT_AVAILABILITY}}",
                                    (user.status?.userStatus?.indicatesLimitedAvailability
                                        ?: false).toString()
                                )
                            navController.navigate(route = route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        EmojiComponent(
                            emoji = user.status?.userStatus?.emoji,
                            getEmojiByName = getEmojiByName,
                            enablePlaceholder = enablePlaceholder
                        )
                        val scrollState = rememberScrollState()
                        Row(modifier = Modifier.horizontalScroll(state = scrollState)) {
                            Text(
                                text = user.status?.userStatus?.message.takeIf { !it.isNullOrEmpty() }
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
                    number = user.repositories.totalCount,
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
                    number = user.starredRepositories.totalCount,
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
                    number = user.followers.totalCount,
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
                    number = user.following.totalCount,
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
                    number = user.projects.totalCount,
                    category = stringResource(id = R.string.repository_projects),
                    onClick = { },
                    enablePlaceholder = enablePlaceholder,
                    modifier = Modifier.weight(weight = 1f)
                )
            }
            ContributionCalendar(
                calendar = user.contributionsCollection.contributionsCollection.contributionCalendar,
                enablePlaceholder = enablePlaceholder
            )
        }

        (user?.pinnedItems?.nodes?.map { it?.pinnableItem }
            ?: organization?.pinnedItems?.nodes?.map { it?.pinnableItem })?.let { list ->
            if (list.isNotEmpty()) {
                Column {
                    CategoryText(
                        textRes = R.string.profile_pinned,
                        enablePlaceholder = enablePlaceholder
                    )
                    LazyRow {
                        items(count = list.size) { index ->
                            val item = list[index]
                            item?.repositoryListItemFragment?.let {
                                PinnedRepositoryCard(
                                    navController = navController,
                                    currentUserLogin = user?.login ?: organization?.login
                                    ?: "ghost",
                                    repository = it,
                                    index = index,
                                    enablePlaceholder = enablePlaceholder
                                )
                            }
                            item?.gist?.let {
                                PinnedGistCard(
                                    navController = navController,
                                    gist = it,
                                    currentUserLogin = user?.login ?: organization?.login
                                    ?: "ghost",
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
            iconVector = Icons.Outlined.Email,
            primaryTextRes = R.string.profile_email,
            secondaryText = user?.email
                ?: organization?.email
                ?: stringResource(id = R.string.no_description_provided),
            enablePlaceholder = enablePlaceholder
        )

        ContactListItem(
            iconVector = Icons.Outlined.LocationOn,
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
        if (user != null || organization != null) {
            CategoryText(
                textRes = R.string.profile_info,
                enablePlaceholder = enablePlaceholder
            )
            InfoListItem(
                leadingRes = R.string.profile_joined_on,
                trailing = DateUtils.getRelativeTimeSpanString(
                    (user?.createdAt ?: organization?.createdAt)!!.toEpochMilliseconds(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS
                ).toString(),
                enablePlaceholder = enablePlaceholder
            )
            InfoListItem(
                leadingRes = R.string.profile_updated_on,
                trailing = DateUtils.getRelativeTimeSpanString(
                    (user?.updatedAt ?: organization?.updatedAt)!!.toEpochMilliseconds(),
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
    @DrawableRes iconRes: Int? = null,
    iconVector: ImageVector? = null,
    @StringRes primaryTextRes: Int,
    secondaryText: String,
    enablePlaceholder: Boolean
) {
    ListItem(
        icon = {
            val modifier = Modifier
                .size(size = IconSize)
                .padding(all = ContentPaddingMediumSize)
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            if (iconRes != null) {
                Icon(
                    contentDescription = stringResource(id = primaryTextRes),
                    painter = painterResource(id = iconRes),
                    modifier = modifier
                )
            } else if (iconVector != null) {
                Icon(
                    contentDescription = stringResource(id = primaryTextRes),
                    imageVector = iconVector,
                    modifier = modifier
                )
            }
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

@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
private fun PinnedItemCard(
    navController: NavController,
    currentUserLogin: String,
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
                    .clickable(enabled = !enablePlaceholder) {
                        if (title.contains("/")) {
                            val login = title
                                .split("/")
                                .firstOrNull()
                            if (!login.isNullOrEmpty()
                                && currentUserLogin != login
                            ) {
                                navController.navigate(
                                    route = Screen.Profile.route
                                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                                        .replace(
                                            "{${Screen.ARG_PROFILE_TYPE}}",
                                            ProfileType.NOT_SPECIFIED.name
                                        )
                                )
                            }
                        }
                    }
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

@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
private fun PinnedRepositoryCard(
    navController: NavController,
    repository: RepositoryListItemFragment,
    currentUserLogin: String,
    index: Int,
    enablePlaceholder: Boolean
) {
    PinnedItemCard(
        navController = navController,
        currentUserLogin = currentUserLogin,
        onClick = {
            if (enablePlaceholder) {
                return@PinnedItemCard
            }
            navController.navigate(
                route = Screen.Repository.route
                    .replace(
                        "{${Screen.ARG_PROFILE_LOGIN}}",
                        repository.repositoryOwner.repositoryOwner.login
                    )
                    .replace("{${Screen.ARG_REPOSITORY_NAME}}", repository.name)
            )
        },
        avatarUrl = repository.repositoryOwner.repositoryOwner.avatarUrl,
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
                    color = repository.primaryLanguage?.language?.color
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
            text = repository.primaryLanguage?.language?.name
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
            text = repository.stargazers.totalCount.formatWithSuffix(),
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
            text = repository.forks.totalCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
    }
}

@ExperimentalCoilApi
@ExperimentalMaterialApi
@Composable
private fun PinnedGistCard(
    navController: NavController,
    gist: Gist,
    currentUserLogin: String,
    index: Int,
    enablePlaceholder: Boolean
) {
    val context = LocalContext.current
    PinnedItemCard(
        navController = navController,
        currentUserLogin = currentUserLogin,
        onClick = {
            context.safeStartActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse(gist.url)).apply {
                    putExtra(Browser.EXTRA_CREATE_NEW_TAB, true)
                    putExtra(Browser.EXTRA_APPLICATION_ID, context.packageName)
                }
            )
        },
        avatarUrl = gist.gistOwner?.repositoryOwner?.avatarUrl,
        title = gist.files?.firstOrNull()?.name ?: gist.name,
        caption = gist.files?.firstOrNull()?.text.takeIf { !it.isNullOrEmpty() }
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
            text = gist.stargazers.totalCount.formatWithSuffix(),
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
            text = gist.forks.totalCount.formatWithSuffix(),
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
            text = gist.comments.totalCount.formatWithSuffix(),
            enablePlaceholder = enablePlaceholder
        )
    }
}

// Preview section start

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Preview(
    showBackground = true,
    name = "ProfileScreen",
    backgroundColor = 0xFFFFFF
)
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