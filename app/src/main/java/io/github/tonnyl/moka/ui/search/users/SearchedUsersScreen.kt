package io.github.tonnyl.moka.ui.search.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import dev.chrisbanes.accompanist.coil.CoilImage
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.item.SearchedOrganizationItem
import io.github.tonnyl.moka.data.item.SearchedUserOrOrgItem
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.ui.users.ItemUser
import io.github.tonnyl.moka.util.SearchedOrganizationItemProvider
import io.github.tonnyl.moka.widget.*

@Composable
fun SearchedUsersScreen(
    navController: NavController,
    users: LazyPagingItems<SearchedUserOrOrgItem>,
    pagerState: PagerState = remember { PagerState() }
) {
    SwipeToRefreshLayout(
        refreshingState = users.loadState.refresh is LoadState.Loading,
        onRefresh = users::refresh,
        refreshIndicator = {
            Surface(
                elevation = 10.dp,
                shape = CircleShape
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size = 36.dp)
                        .padding(all = ContentPaddingSmallSize)
                )
            }
        }
    ) {
        Pager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                users.loadState.refresh is LoadState.NotLoading
                        && users.loadState.append is LoadState.NotLoading
                        && users.loadState.prepend is LoadState.NotLoading
                        && users.itemCount == 0 -> {

                }
                users.loadState.refresh is LoadState.NotLoading
                        && users.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_timeline_24,
                        title = R.string.timeline_content_empty_title,
                        retry = R.string.common_retry,
                        action = R.string.timeline_content_empty_action
                    )
                }
                users.loadState.refresh is LoadState.Error
                        && users.itemCount == 0 -> {
                    EmptyScreenContent(
                        icon = R.drawable.ic_menu_inbox_24,
                        title = R.string.common_error_requesting_data,
                        retry = R.string.common_retry,
                        action = R.string.notification_content_empty_action
                    )
                }
                else -> {
                    SearchedUsersScreenContent(
                        navController = navController,
                        users = users
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchedUsersScreenContent(
    navController: NavController,
    users: LazyPagingItems<SearchedUserOrOrgItem>
) {
    LazyColumn {
        item {
            ItemLoadingState(loadState = users.loadState.prepend)
        }
        itemsIndexed(lazyPagingItems = users) { _, userOrOrg ->
            if (userOrOrg?.user != null) {
                ItemUser(
                    user = userOrOrg.user,
                    navController = navController
                )
            }
            if (userOrOrg?.org != null) {
                ItemSearchedOrganization(
                    org = userOrOrg.org,
                    navController = navController
                )
            }
        }
        item {
            ItemLoadingState(loadState = users.loadState.append)
        }
    }
}

@Composable
fun ItemSearchedOrganization(
    org: SearchedOrganizationItem,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(
                    route = Screen.Profile.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", org.login)
                        .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.ORGANIZATION.name)
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        CoilImage(
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            request = createAvatarLoadRequest(url = org.avatarUrl),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            if (!org.name.isNullOrEmpty()) {
                Text(
                    text = org.name,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = org.login,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.fillMaxWidth()
                )
                (org.description ?: org.descriptionHTML)?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "ItemSearchedOrganizationPreview", showBackground = true)
@Composable
private fun ItemSearchedOrganizationPreview(
    @PreviewParameter(
        provider = SearchedOrganizationItemProvider::class,
        limit = 1
    )
    org: SearchedOrganizationItem
) {
    ItemSearchedOrganization(
        org = org,
        navController = rememberNavController()
    )
}