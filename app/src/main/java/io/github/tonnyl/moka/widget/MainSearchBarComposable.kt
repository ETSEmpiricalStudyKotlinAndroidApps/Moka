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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.navigate
import com.google.accompanist.coil.rememberCoilPainter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.account.AccountDialogScreen
import io.github.tonnyl.moka.ui.theme.*
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun MainSearchBar(
    openDrawer: () -> Unit,
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

@Composable
private fun MainSearchBarContent(
    modifier: Modifier,
    avatarUrl: String?,
    onMenuClicked: () -> Unit,
    onTextClicked: () -> Unit,
    onAvatarClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .background(color = Color.Transparent)
            .height(height = 64.dp)
            .padding(all = ContentPaddingMediumSize)
    ) {
        Card(
            elevation = 3.dp,
            shape = MaterialTheme.shapes.small
        ) {
            Row {
                IconButton(onClick = onMenuClicked) {
                    Icon(
                        contentDescription = stringResource(id = R.string.navigation_header_avatar_desc),
                        painter = painterResource(id = R.drawable.ic_menu_24)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(weight = 1f)
                        .clickable(onClick = onTextClicked)
                        .padding(horizontal = ContentPaddingMediumSize)
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
                Image(
                    painter = rememberCoilPainter(
                        request = avatarUrl,
                        requestBuilder = {
                            createAvatarLoadRequest()
                        }
                    ),
                    contentDescription = stringResource(id = R.string.navigation_header_avatar_desc),
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .size(size = IconSize)
                        .clickable(onClick = onAvatarClicked)
                        .padding(all = ContentPaddingSmallSize)
                        .clip(shape = CircleShape)
                        .align(alignment = Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
            }
        }
    }
}

@Preview(showBackground = true, name = "MainSearchBarPreview")
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
