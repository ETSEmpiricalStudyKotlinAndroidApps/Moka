package io.github.tonnyl.moka.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.account.AccountDialogScreen
import io.github.tonnyl.moka.ui.theme.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalComposeUiApi
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun MainSearchBar(
    openDrawer: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val currentAccount = LocalAccountInstance.current ?: return
    val state = remember {
        mutableStateOf(false)
    }

    AccountDialogScreen(showState = state)

    val navController = LocalNavController.current
    MainSearchBarContent(
        modifier = modifier,
        avatarUrl = currentAccount.signedInAccount.account.avatarUrl,
        onMenuClicked = openDrawer,
        onTextClicked = {
            navController.navigate(route = Screen.Search.route)
        },
        onAvatarClicked = {
            state.value = true
        }
    )
}

@ExperimentalCoilApi
@Composable
private fun MainSearchBarContent(
    modifier: Modifier,
    avatarUrl: String?,
    onMenuClicked: (() -> Unit)?,
    onTextClicked: () -> Unit,
    onAvatarClicked: () -> Unit
) {
    val displayMenuIcon = onMenuClicked != null

    Box(
        modifier = modifier
            .background(color = Color.Transparent)
            .height(height = 64.dp)
            .fillMaxWidth()
            .padding(
                vertical = ContentPaddingMediumSize,
                horizontal = ContentPaddingLargeSize
            )
    ) {
        Card(
            elevation = 3.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = onTextClicked)
                    .padding(
                        start = DefaultIconButtonSizeModifier,
                        end = ContentPaddingSmallSize + ContentPaddingSmallSize + IconSize
                    )
            ) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = R.string.search_input_hint),
                        maxLines = 1,
                        style = MaterialTheme.typography.body1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(alignment = Alignment.CenterStart)
                    )
                }
            }
            IconButton(
                enabled = displayMenuIcon,
                onClick = {
                    onMenuClicked?.invoke()
                }
            ) {
                Icon(
                    contentDescription = stringResource(
                        id = if (displayMenuIcon) {
                            R.string.navigation_drawer_open
                        } else {
                            R.string.search_input_hint
                        }
                    ),
                    painter = painterResource(
                        id = if (displayMenuIcon) {
                            R.drawable.ic_menu_24
                        } else {
                            R.drawable.ic_menu_search_24
                        }
                    )
                )
            }
            Image(
                painter = rememberImagePainter(
                    data = avatarUrl,
                    builder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.navigation_header_avatar_desc),
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .height(height = IconSize)
                    .width(width = IconSize + ContentPaddingSmallSize)
                    .clickable(onClick = onAvatarClicked)
                    .padding(
                        start = ContentPaddingSmallSize,
                        top = ContentPaddingSmallSize,
                        end = ContentPaddingMediumSize,
                        bottom = ContentPaddingSmallSize
                    )
                    .clip(shape = CircleShape)
                    .align(alignment = Alignment.CenterEnd)
            )
        }
    }
}

@ExperimentalCoilApi
@Preview(
    showBackground = true,
    name = "MainSearchBarPreview",
    backgroundColor = 0xFFFFFF
)
@Composable
private fun MainSearchBarPreview() {
    MainSearchBarContent(
        modifier = Modifier,
        avatarUrl = null,
        onMenuClicked = {},
        onTextClicked = {},
        onAvatarClicked = {}
    )
}