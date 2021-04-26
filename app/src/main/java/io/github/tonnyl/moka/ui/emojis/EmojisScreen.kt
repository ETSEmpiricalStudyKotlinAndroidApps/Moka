package io.github.tonnyl.moka.ui.emojis

import android.webkit.URLUtil
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Emoji
import io.github.tonnyl.moka.data.EmojiCategory
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.util.EmojiCategoryProvider
import io.github.tonnyl.moka.util.EmojiItemProvider
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun EmojisScreen() {
    val emojis by LocalMainViewModel.current.emojis.observeAsState(initial = emptyList())
    val lazyListState = rememberLazyListState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        EmojisScreenContent(
            topAppBarSize = topAppBarSize,
            emojis = emojis,
            lazyListState = lazyListState
        )

        val navController = LocalNavController.current

        InsetAwareTopAppBar(
            title = { },
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
                IconButton(
                    onClick = {
                        navController.navigate(
                            route = Screen.SearchEmoji.route
                        )
                    }
                ) {
                    Icon(
                        contentDescription = stringResource(id = R.string.search_emoji),
                        painter = painterResource(id = R.drawable.ic_menu_search_24)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun EmojisScreenContent(
    topAppBarSize: Int,
    emojis: List<Pair<EmojiCategory, List<Emoji>>>,
    lazyListState: LazyListState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        LazyColumn(
            state = lazyListState,
            contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
                top = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            ),
            modifier = Modifier.weight(weight = 1f)
        ) {
            emojis.forEach { pair ->
                item {
                    ItemEmojiCategory(pair.first)
                }
                for (i in pair.second.indices step 7) {
                    item {
                        ItemEmojiRow(
                            emoji0 = pair.second.getOrNull(i),
                            emoji1 = pair.second.getOrNull(i + 1),
                            emoji2 = pair.second.getOrNull(i + 2),
                            emoji3 = pair.second.getOrNull(i + 3),
                            emoji4 = pair.second.getOrNull(i + 4),
                            emoji5 = pair.second.getOrNull(i + 5),
                            emoji6 = pair.second.getOrNull(i + 6)
                        )
                    }
                }
            }
        }
        EmojiCategoryButtons(
            lazyListState = lazyListState,
            emojis = emojis
        )
    }
}

@Composable
private fun EmojiCategoryButtons(
    lazyListState: LazyListState,
    emojis: List<Pair<EmojiCategory, List<Emoji>>>,
    modifier: Modifier = Modifier
) {
    var currentSelectedCategory by remember {
        mutableStateOf(EmojiCategory.RecentlyUsed)
    }

    val categoryStartIndexMap = remember {
        mutableStateOf(mutableMapOf<Int, EmojiCategory>())
    }

    if (categoryStartIndexMap.value.isEmpty()) {
        var categoryIndex = 0
        emojis.forEach { pair ->
            categoryStartIndexMap.value[categoryIndex] = pair.first
            categoryIndex += 1
            categoryStartIndexMap.value[categoryIndex] = pair.first

            for (i in pair.second.indices step 7) {
                categoryIndex += 1
                categoryStartIndexMap.value[categoryIndex] = pair.first
            }
        }
    }

    categoryStartIndexMap.value[lazyListState.firstVisibleItemIndex]?.let {
        if (currentSelectedCategory != it) {
            currentSelectedCategory = it
        }
    }

    val scope = rememberCoroutineScope()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
            .clip(shape = MaterialTheme.shapes.medium)
    ) {
        EmojiCategory.values().forEach {
            EmojiCategoryButton(
                category = it,
                selected = currentSelectedCategory == it,
                onCategorySelected = { newSelectedCategory ->
                    for ((index, category) in categoryStartIndexMap.value) {
                        if (newSelectedCategory == category) {
                            currentSelectedCategory = newSelectedCategory
                            scope.launch {
                                lazyListState.animateScrollToItem(index = index)
                            }

                            break
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun RowScope.EmojiCategoryButton(
    category: EmojiCategory,
    selected: Boolean,
    onCategorySelected: (EmojiCategory) -> Unit
) {
    IconButton(
        onClick = {
            onCategorySelected.invoke(category)
        },
        modifier = Modifier.weight(weight = 1f)
    ) {
        val imageRes: Int
        val contentDescriptionsRes: Int
        when (category) {
            EmojiCategory.RecentlyUsed -> {
                imageRes = R.drawable.ic_emoji_recent_24
                contentDescriptionsRes = R.string.emoji_category_recent_used_content_description
            }
            EmojiCategory.SmileysAndEmotion -> {
                imageRes = R.drawable.ic_emoji_emotions_24
                contentDescriptionsRes =
                    R.string.emoji_category_smiley_and_emotion_content_description
            }
            EmojiCategory.PeopleAndBody -> {
                imageRes = R.drawable.ic_emoji_people_24
                contentDescriptionsRes =
                    R.string.emoji_category_people_and_body_content_description
            }
            EmojiCategory.AnimalsAndNature -> {
                imageRes = R.drawable.ic_emoji_nature_24
                contentDescriptionsRes =
                    R.string.emoji_category_animals_and_nature_content_description
            }
            EmojiCategory.FoodAndDrink -> {
                imageRes = R.drawable.ic_emoji_food_beverage_24
                contentDescriptionsRes =
                    R.string.emoji_category_food_and_drink_content_description
            }
            EmojiCategory.TravelAndPlaces -> {
                imageRes = R.drawable.ic_emoji_transportation_24
                contentDescriptionsRes =
                    R.string.emoji_category_travel_and_places_content_description
            }
            EmojiCategory.Activities -> {
                imageRes = R.drawable.ic_emoji_events_24
                contentDescriptionsRes = R.string.emoji_category_activities_content_description
            }
            EmojiCategory.Objects -> {
                imageRes = R.drawable.ic_emoji_objects_24
                contentDescriptionsRes = R.string.emoji_category_objects_content_description
            }
            EmojiCategory.Symbols -> {
                imageRes = R.drawable.ic_emoji_symbols_24
                contentDescriptionsRes = R.string.emoji_category_symbols_content_description
            }
            EmojiCategory.Flags -> {
                imageRes = R.drawable.ic_emoji_flags_24
                contentDescriptionsRes = R.string.emoji_category_flags_content_description
            }
            EmojiCategory.GitHubCustomEmoji -> {
                imageRes = R.drawable.ic_code_24
                contentDescriptionsRes =
                    R.string.emoji_category_github_custom_emoji_content_description
            }
        }
        Icon(
            contentDescription = stringResource(id = contentDescriptionsRes),
            painter = painterResource(
                id = imageRes
            ),
            tint = if (selected) {
                MaterialTheme.colors.primary
            } else {
                MaterialTheme.colors.onBackground
            }
        )
    }
}

@Composable
private fun ItemEmoji(
    emoji: Emoji?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size = 48.dp)
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = emoji != null) {
                navController.previousBackStackEntry?.savedStateHandle
                    ?.set(Screen.Emojis.RESULT_EMOJI, emoji?.names?.firstOrNull())
                navController.navigateUp()
            }
    ) {
        emoji?.emoji?.let { emoji ->
            if (URLUtil.isValidUrl(emoji)) {
                Image(
                    painter = rememberCoilPainter(
                        request = emoji,
                        requestBuilder = {
                            createAvatarLoadRequest()
                        }
                    ),
                    contentDescription = stringResource(id = R.string.emoji_image_content_description),
                    modifier = Modifier.size(size = 27.dp)
                )
            } else {
                Text(
                    text = emoji,
                    fontSize = 27.sp
                )
            }
        }
    }
}

/**
 * [LazyVerticalGrid] doesn't have any support for merging cells.
 */
@Composable
private fun ItemEmojiRow(
    emoji0: Emoji?,
    emoji1: Emoji?,
    emoji2: Emoji?,
    emoji3: Emoji?,
    emoji4: Emoji?,
    emoji5: Emoji?,
    emoji6: Emoji?
) {
    val navController = LocalNavController.current

    Row {
        ItemEmoji(
            emoji = emoji0,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
        ItemEmoji(
            emoji = emoji1,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
        ItemEmoji(
            emoji = emoji2,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
        ItemEmoji(
            emoji = emoji3,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
        ItemEmoji(
            emoji = emoji4,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
        ItemEmoji(
            emoji = emoji5,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
        ItemEmoji(
            emoji = emoji6,
            navController = navController,
            modifier = Modifier.weight(weight = 1f)
        )
    }
}

@Composable
private fun ItemEmojiCategory(category: EmojiCategory) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = category.categoryValue,
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = ContentPaddingMediumSize)
        )
    }
}

@Preview(name = "EmojiItemPreview", showBackground = true)
@Composable
private fun EmojiItemPreview(
    @PreviewParameter(
        provider = EmojiItemProvider::class,
        limit = 1
    )
    emoji: Emoji
) {
    ItemEmoji(
        emoji = emoji,
        navController = rememberNavController()
    )
}

@Preview(name = "EmojiCategoryItemPreview", showBackground = true)
@Composable
private fun EmojiCategoryItemPreview(
    @PreviewParameter(
        provider = EmojiCategoryProvider::class,
        limit = 1
    )
    category: EmojiCategory
) {
    ItemEmojiCategory(category)
}

@Preview(name = "EmojiCategoryButtonsPreview", showBackground = true)
@Composable
private fun EmojiCategoryButtonsPreview() {
    EmojiCategoryButtons(
        emojis = emptyList(),
        lazyListState = rememberLazyListState()
    )
}