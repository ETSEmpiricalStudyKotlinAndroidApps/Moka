package io.github.tonnyl.moka.ui.emojis.search

import android.webkit.URLUtil
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.util.SearchedEmojiItemProvider
import io.github.tonnyl.moka.widget.SearchBar

@ExperimentalComposeUiApi
@Composable
fun SearchEmojiScreen(
    navController: NavController,
    mainViewModel: MainViewModel
) {
    val emojis by mainViewModel.searchableEmojis.observeAsState()

    val textState = remember { mutableStateOf(TextFieldValue()) }

    DisposableEffect(Unit) {
        onDispose {
            mainViewModel.filterSearchable(text = null)
        }
    }

    mainViewModel.filterSearchable(text = textState.value.text)

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SearchEmojiScreenContent(
            topAppBarSize = topAppBarSize,
            emojis = emojis ?: emptyList(),
            navController = navController
        )

        SearchBar(
            hintResId = R.string.search_emoji,
            navController = navController,
            textState = textState,
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@Composable
private fun SearchEmojiScreenContent(
    topAppBarSize: Int,
    emojis: List<SearchableEmoji>,
    navController: NavController
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )
    ) {
        items(count = emojis.size) { index ->
            SearchedEmojiItem(
                emoji = emojis[index],
                navController = navController
            )
        }
    }
}

@Composable
private fun SearchedEmojiItem(
    emoji: SearchableEmoji,
    navController: NavController
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set(Screen.Emojis.RESULT_EMOJI, emoji.name)
                navController.navigateUp()
            }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size = 48.dp)
        ) {
            if (URLUtil.isValidUrl(emoji.emoji)) {
                CoilImage(
                    contentDescription = stringResource(id = R.string.emoji_status_content_description),
                    request = createAvatarLoadRequest(url = emoji.emoji),
                    modifier = Modifier.size(size = 27.dp)
                )
            } else {
                Text(
                    text = emoji.emoji,
                    fontSize = 27.sp
                )
            }
        }

        Text(
            text = emoji.name,
            style = MaterialTheme.typography.body1,
            modifier = Modifier
                .padding(horizontal = ContentPaddingMediumSize)
                .weight(weight = 1f)
        )
    }
}

@Preview(name = "SearchedEmojiItemPreview", showBackground = true)
@Composable
private fun SearchedEmojiItemPreview(
    @PreviewParameter(
        provider = SearchedEmojiItemProvider::class,
        limit = 1
    )
    emoji: SearchableEmoji
) {
    SearchedEmojiItem(
        emoji = emoji,
        navController = rememberNavController()
    )
}