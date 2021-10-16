package io.github.tonnyl.moka.ui.explore

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.serializers.store.ExploreOptionsSerializer
import io.github.tonnyl.moka.serializers.store.data.ExploreLanguage
import io.github.tonnyl.moka.serializers.store.data.ExploreTimeSpan
import io.github.tonnyl.moka.serializers.store.data.displayStringResId
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalAnimationApi
@ExperimentalSerializationApi
@Composable
@ExperimentalMaterialApi
fun ExploreFiltersScreen(
    sheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    val languages by LocalMainViewModel.current.localLanguages.observeAsState(initial = emptyList())
    val lazyListState = rememberLazyListState()

    ModalBottomSheetLayout(
        sheetContent = {
            ExploreFiltersScreenContent(
                sheetState = sheetState,
                lazyListState = lazyListState,
                languages = languages
            )
        },
        sheetState = sheetState,
        content = content
    )
}

@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
private fun ExploreFiltersScreenContent(
    sheetState: ModalBottomSheetState,
    lazyListState: LazyListState,
    languages: List<ExploreLanguage>
) {
    val exploreViewModel = viewModel<ExploreViewModel>(
        key = LocalAccountInstance.current.toString(),
        factory = ViewModelFactory(accountInstance = LocalAccountInstance.current ?: return)
    )
    val exploreOptions by exploreViewModel.queryData.observeAsState(initial = ExploreOptionsSerializer.defaultValue)

    val languageState = remember { mutableStateOf(exploreOptions.exploreLanguage) }
    val timeSpanState = remember { mutableStateOf(exploreOptions.timeSpan) }

    val scope = rememberCoroutineScope()

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
            item {
                TimeSpanItem(timeSpanState = timeSpanState)
            }

            item {
                Text(
                    text = stringResource(id = R.string.explore_trending_filter_languages),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.padding(all = ContentPaddingLargeSize)
                )
            }

            items(count = languages.size) { index ->
                LanguageItem(
                    language = languages[index],
                    languageState = languageState
                )
            }
        }

        val navController = LocalNavController.current
        InsetAwareTopAppBar(
            title = {
                var languageName by remember { mutableStateOf("") }
                languageName = languageState.value.name

                var timeSpanName by remember { mutableStateOf("") }
                timeSpanName = stringResource(id = timeSpanState.value.displayStringResId)

                Row {
                    Text(
                        text = languageName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .wrapContentWidth()
                            .animateContentSize()
                    )
                    Text(
                        text = stringResource(id = R.string.explore_filter_divider),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .wrapContentWidth()
                    )
                    Text(
                        text = timeSpanName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .wrapContentWidth()
                            .animateContentSize()
                    )
                }
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_arrow_back_24)
                        )
                    }
                )
            },
            actions = {
                var visibleState by remember { mutableStateOf(false) }
                visibleState = exploreOptions.exploreLanguage != languageState.value
                        || exploreOptions.timeSpan != timeSpanState.value

                AnimatedVisibility(visible = visibleState) {
                    IconButton(
                        onClick = {
                            exploreViewModel.updateExploreOptions(
                                exploreLanguage = languageState.value,
                                timeSpan = timeSpanState.value
                            )

                            scope.launch {
                                sheetState.hide()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check_24),
                            contentDescription = stringResource(id = R.string.done_image_description)
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
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
private fun LanguageItem(
    language: ExploreLanguage,
    languageState: MutableState<ExploreLanguage>
) {
    var checked by remember { mutableStateOf(false) }
    checked = languageState.value == language

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
            AnimatedVisibility(
                visible = checked,
            ) {
                Image(
                    contentDescription = stringResource(id = R.string.explore_filter_done),
                    painter = painterResource(id = R.drawable.ic_done_24)
                )
            }
        },
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {
                languageState.value = language
            }
    )
}

@ExperimentalSerializationApi
@Composable
private fun TimeSpanItem(timeSpanState: MutableState<ExploreTimeSpan>) {
    Column {
        Text(
            text = stringResource(id = R.string.explore_trending_filter_time_span),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(all = ContentPaddingLargeSize)
        )
        TimeSpanRadioButtonItem(
            spanType = ExploreTimeSpan.DAILY,
            checked = timeSpanState.value == ExploreTimeSpan.DAILY,
            onClick = {
                timeSpanState.value = ExploreTimeSpan.DAILY
            }
        )
        TimeSpanRadioButtonItem(
            spanType = ExploreTimeSpan.WEEKLY,
            checked = timeSpanState.value == ExploreTimeSpan.WEEKLY,
            onClick = {
                timeSpanState.value = ExploreTimeSpan.WEEKLY
            }
        )
        TimeSpanRadioButtonItem(
            spanType = ExploreTimeSpan.MONTHLY,
            checked = timeSpanState.value == ExploreTimeSpan.MONTHLY,
            onClick = {
                timeSpanState.value = ExploreTimeSpan.MONTHLY
            }
        )
    }
}

@ExperimentalSerializationApi
@Composable
private fun TimeSpanRadioButtonItem(
    spanType: ExploreTimeSpan,
    checked: Boolean,
    onClick: (ExploreTimeSpan) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable {
                onClick.invoke(spanType)
            }
            .padding(
                vertical = ContentPaddingMediumSize,
                horizontal = ContentPaddingLargeSize
            )
    ) {
        RadioButton(
            selected = checked,
            onClick = {
                onClick.invoke(spanType)
            }
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Text(
            text = stringResource(
                when (spanType) {
                    ExploreTimeSpan.DAILY -> {
                        R.string.explore_trending_filter_time_span_daily
                    }
                    ExploreTimeSpan.WEEKLY -> {
                        R.string.explore_trending_filter_time_span_weekly
                    }
                    ExploreTimeSpan.MONTHLY -> {
                        R.string.explore_trending_filter_time_span_monthly
                    }
                }
            ),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(weight = 1f)
        )
    }
}

@ExperimentalAnimationApi
@SuppressLint("UnrememberedMutableState")
@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Preview(name = "LanguageItemPreview", showBackground = true)
@Composable
private fun LanguageItemPreview() {
    LanguageItem(
        language = ExploreOptionsSerializer.defaultValue.exploreLanguage,
        languageState = mutableStateOf(ExploreOptionsSerializer.defaultValue.exploreLanguage)
    )
}

@SuppressLint("UnrememberedMutableState")
@ExperimentalSerializationApi
@Preview(name = "TimeSpanItemPreview", showBackground = true)
@Composable
private fun TimeSpanItemPreview() {
    TimeSpanItem(timeSpanState = mutableStateOf(ExploreOptionsSerializer.defaultValue.timeSpan))
}