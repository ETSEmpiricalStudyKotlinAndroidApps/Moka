package io.github.tonnyl.moka.ui.explore.filters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.SearchBox
import io.tonnyl.moka.common.data.FiltersType
import io.tonnyl.moka.common.store.ExploreOptionsSerializer
import io.tonnyl.moka.common.store.data.ExploreLanguage
import io.tonnyl.moka.common.store.data.ExploreOptions
import io.tonnyl.moka.common.store.data.ExploreSpokenLanguage
import kotlinx.coroutines.launch

@Composable
fun ExploreFiltersScreen(filtersType: FiltersType) {
    val currentAccount = LocalAccountInstance.current ?: return

    val mainViewModel = LocalMainViewModel.current
    val languages by mainViewModel.programmingLanguages.observeAsState(initial = emptyList())
    val spokenLanguages by mainViewModel.spokenLanguages.observeAsState(initial = emptyList())

    val lazyListState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val navController = LocalNavController.current

    val viewModel = viewModel(
        key = LocalAccountInstance.current.toString(),
        initializer = {
            ExploreFiltersViewModel(
                extra = ExploreFiltersViewModelExtra(
                    accountInstance = currentAccount
                )
            )
        }
    )

    val exploreOptions by viewModel.options.observeAsState(initial = ExploreOptionsSerializer.defaultValue)
    var showSearchBox by remember { mutableStateOf(false) }
    val searchText = remember { mutableStateOf(TextFieldValue()) }

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        LazyColumn(
            state = lazyListState,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            )
        ) {
            when (filtersType) {
                FiltersType.ProgrammingLanguages -> {
                    items(count = languages.size) { index ->
                        LanguageItem(
                            language = languages[index],
                            currentOptions = exploreOptions,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
                FiltersType.SpokenLanguages -> {
                    items(count = spokenLanguages.size) { index ->
                        SpokenLanguageItem(
                            spokenLanguage = spokenLanguages[index],
                            currentOptions = exploreOptions,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }
        }

        InsetAwareTopAppBar(
            title = {
                AnimatedVisibility(
                    visible = showSearchBox,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    SearchBox(
                        hintResId = R.string.search_history_action,
                        textState = searchText,
                        onValueChange = {
                            when (filtersType) {
                                FiltersType.ProgrammingLanguages -> {
                                    mainViewModel.filterProgrammingLanguages(text = searchText.value.text)
                                }
                                FiltersType.SpokenLanguages -> {
                                    mainViewModel.filterSpokenLanguages(text = searchText.value.text)
                                }
                            }
                        }
                    )

                    DisposableEffect(Unit) {
                        onDispose {
                            when (filtersType) {
                                FiltersType.ProgrammingLanguages -> {
                                    mainViewModel.filterProgrammingLanguages(text = null)
                                }
                                FiltersType.SpokenLanguages -> {
                                    mainViewModel.filterSpokenLanguages(text = null)
                                }
                            }
                        }
                    }
                }
                AnimatedVisibility(
                    visible = !showSearchBox,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = stringResource(
                            id = when (filtersType) {
                                FiltersType.ProgrammingLanguages -> {
                                    R.string.explore_trending_filter_language
                                }
                                FiltersType.SpokenLanguages -> {
                                    R.string.explore_trending_filter_spoken_language
                                }
                            }
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.wrapContentWidth()
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        coroutineScope.launch {
                                            lazyListState.animateScrollToItem(0)
                                        }
                                    }
                                )
                            }
                    )
                }
            },
            navigationIcon = {
                AppBarNavigationIcon(
                    onClick = {
                        if (showSearchBox) {
                            showSearchBox = false
                        } else {
                            navController.navigateUp()
                        }
                    },
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(id = R.string.navigate_close)
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        showSearchBox = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = stringResource(id = R.string.search_history_action)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@Composable
private fun LanguageItem(
    navController: NavController,
    currentOptions: ExploreOptions,
    language: ExploreLanguage,
    viewModel: ExploreFiltersViewModel
) {
    ListItem(
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(size = 12.dp)
                        .clip(shape = CircleShape)
                        .background(
                            color = language.color
                                .toColor()
                                ?.let {
                                    Color(it)
                                } ?: MaterialTheme.colors.onBackground
                        )
                )
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                Text(text = language.name)
            }
        },
        trailing = {
            AnimatedCheckIcon(checked = currentOptions.exploreLanguage == language)
        },
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                viewModel.updateExploreOptions(
                    exploreLanguage = language,
                    spokenLanguage = currentOptions.exploreSpokenLanguage
                )

                navController.navigateUp()
            }
    )
}

@Composable
private fun SpokenLanguageItem(
    navController: NavController,
    currentOptions: ExploreOptions,
    spokenLanguage: ExploreSpokenLanguage,
    viewModel: ExploreFiltersViewModel
) {
    ListItem(
        trailing = {
            AnimatedCheckIcon(checked = currentOptions.exploreSpokenLanguage == spokenLanguage)
        },
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                viewModel.updateExploreOptions(
                    exploreLanguage = currentOptions.exploreLanguage,
                    spokenLanguage = spokenLanguage
                )

                navController.navigateUp()
            }
    ) {
        Text(text = spokenLanguage.name)
    }
}

@Composable
private fun AnimatedCheckIcon(checked: Boolean) {
    AnimatedVisibility(
        visible = checked,
    ) {
        Image(
            contentDescription = stringResource(id = R.string.explore_filter_done),
            imageVector = Icons.Outlined.Done
        )
    }
}