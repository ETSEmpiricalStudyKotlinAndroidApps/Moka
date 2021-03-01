package io.github.tonnyl.moka.ui.explore

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar

@Composable
@ExperimentalMaterialApi
fun ExploreFiltersScreen(
    navController: NavController,
    viewModel: MainViewModel,
    sheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    val languages by viewModel.localLanguages.observeAsState(emptyList())
    val lazyListState = rememberLazyListState()

    ModalBottomSheetLayout(
        sheetContent = {
            ExploreFiltersScreenContent(
                navController = navController,
                lazyListState = lazyListState,
                languages = languages
            )
        },
        sheetState = sheetState,
        content = content
    )
}

@ExperimentalMaterialApi
@Composable
private fun ExploreFiltersScreenContent(
    navController: NavController,
    lazyListState: LazyListState,
    languages: List<LocalLanguage>
) {
    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        LazyColumn(
            state = lazyListState,
            contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
                top = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            )
        ) {
            item {
                TimeSpanItem(initialCheckedTimeSpan = ExploreTimeSpanType.WEEKLY)
            }

            item {
                Text(
                    text = stringResource(id = R.string.explore_trending_filter_languages),
                    modifier = Modifier.padding(all = ContentPaddingLargeSize)
                )
            }

            items(count = languages.size) { index ->
                LanguageItem(language = languages[index])
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.navigation_menu_about))
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
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun LanguageItem(language: LocalLanguage) {
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
            Image(
                contentDescription = stringResource(id = R.string.explore_filter_done),
                painter = painterResource(id = R.drawable.ic_done_24)
            )
        },
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {

            }
    )
}

@Composable
private fun TimeSpanItem(initialCheckedTimeSpan: ExploreTimeSpanType) {
    var checkedTimeSpan by remember { mutableStateOf(initialCheckedTimeSpan) }
    Column {
        Text(
            text = stringResource(id = R.string.explore_trending_filter_time_span),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(all = ContentPaddingLargeSize)
        )
        TimeSpanRadioButtonItem(
            spanType = ExploreTimeSpanType.DAILY,
            checked = checkedTimeSpan == ExploreTimeSpanType.DAILY,
            onClick = {
                checkedTimeSpan = ExploreTimeSpanType.DAILY
            }
        )
        TimeSpanRadioButtonItem(
            spanType = ExploreTimeSpanType.WEEKLY,
            checked = checkedTimeSpan == ExploreTimeSpanType.WEEKLY,
            onClick = {
                checkedTimeSpan = ExploreTimeSpanType.WEEKLY
            }
        )
        TimeSpanRadioButtonItem(
            spanType = ExploreTimeSpanType.MONTHLY,
            checked = checkedTimeSpan == ExploreTimeSpanType.MONTHLY,
            onClick = {
                checkedTimeSpan = ExploreTimeSpanType.MONTHLY
            }
        )
    }
}

@Composable
private fun TimeSpanRadioButtonItem(
    spanType: ExploreTimeSpanType,
    checked: Boolean,
    onClick: (ExploreTimeSpanType) -> Unit
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
                    ExploreTimeSpanType.DAILY -> {
                        R.string.explore_trending_filter_time_span_daily
                    }
                    ExploreTimeSpanType.WEEKLY -> {
                        R.string.explore_trending_filter_time_span_weekly
                    }
                    ExploreTimeSpanType.MONTHLY -> {
                        R.string.explore_trending_filter_time_span_monthly
                    }
                }
            ),
            style = MaterialTheme.typography.body2,
            modifier = Modifier.weight(weight = 1f)
        )
    }
}

@ExperimentalMaterialApi
@Preview(name = "LanguageItemPreview", showBackground = true)
@Composable
private fun LanguageItemPreview() {
    LanguageItem(
        language = LocalLanguage(
            urlParam = null,
            name = "Kotlin",
            color = "#F18E33"
        )
    )
}

@Preview(name = "TimeSpanItemPreview", showBackground = true)
@Composable
private fun TimeSpanItemPreview() {
    TimeSpanItem(initialCheckedTimeSpan = ExploreTimeSpanType.DAILY)
}