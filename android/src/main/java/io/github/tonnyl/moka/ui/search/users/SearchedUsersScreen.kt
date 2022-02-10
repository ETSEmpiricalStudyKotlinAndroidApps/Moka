package io.github.tonnyl.moka.ui.search.users

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.ui.users.ItemUser
import io.github.tonnyl.moka.widget.DefaultSwipeRefreshIndicator
import io.github.tonnyl.moka.widget.EmptyScreenContent
import io.github.tonnyl.moka.widget.ItemLoadingState
import io.tonnyl.moka.common.data.SearchedUserOrOrgItem
import io.tonnyl.moka.common.ui.defaultPagingConfig
import io.tonnyl.moka.common.util.SearchedOrganizationItemProvider
import io.tonnyl.moka.common.util.UserItemProvider
import io.tonnyl.moka.graphql.fragment.OrganizationListItemFragment
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@Composable
fun SearchedUsersScreen(users: LazyPagingItems<SearchedUserOrOrgItem>) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = users.loadState.refresh is LoadState.Loading),
        onRefresh = users::refresh,
        indicator = { state, refreshTriggerDistance ->
            DefaultSwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTriggerDistance
            )
        },
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
                SearchedUsersScreenContent(users = users)
            }
        }
    }
}

@ExperimentalSerializationApi
@Composable
private fun SearchedUsersScreenContent(users: LazyPagingItems<SearchedUserOrOrgItem>) {
    val userPlaceholder = remember {
        UserItemProvider().values.last()
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            ItemLoadingState(loadState = users.loadState.prepend)
        }

        if (users.loadState.refresh is LoadState.Loading) {
            items(count = defaultPagingConfig.initialLoadSize) {
                ItemUser(user = userPlaceholder, enablePlaceholder = true)
            }
        } else {
            itemsIndexed(
                items = users,
                key = { _, item ->
                    item.user?.id ?: item.org?.id ?: item
                }
            ) { _, userOrOrg ->
                if (userOrOrg?.user != null) {
                    ItemUser(
                        user = userOrOrg.user!!,
                        enablePlaceholder = false
                    )
                }
                if (userOrOrg?.org != null) {
                    ItemSearchedOrganization(
                        org = userOrOrg.org!!,
                        enablePlaceholder = false
                    )
                }
            }
        }
        item {
            ItemLoadingState(loadState = users.loadState.append)
        }
    }
}

@Composable
fun ItemSearchedOrganization(
    org: OrganizationListItemFragment,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !enablePlaceholder) {
                navController.navigate(
                    route = Screen.Profile.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", org.login)
                        .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.ORGANIZATION.name)
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberImagePainter(
                data = org.avatarUrl,
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
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            if (!org.name.isNullOrEmpty()) {
                Text(
                    text = org.name!!,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = org.login,
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
                (org.description ?: org.descriptionHTML)?.let {
                    if (it.isNotEmpty()) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.body2,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
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
}

@Preview(
    name = "ItemSearchedOrganizationPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun ItemSearchedOrganizationPreview(
    @PreviewParameter(
        provider = SearchedOrganizationItemProvider::class,
        limit = 1
    )
    org: OrganizationListItemFragment
) {
    ItemSearchedOrganization(
        org = org,
        enablePlaceholder = false
    )
}