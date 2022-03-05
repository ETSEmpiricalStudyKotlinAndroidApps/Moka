package io.github.tonnyl.moka.ui.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.util.formatDateWithDefaultLocale
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.*
import io.tonnyl.moka.common.data.GitHubStatusComponentStatus.*
import io.tonnyl.moka.common.data.GitHubStatusStatusIndicator.*
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.util.GitHubStatusProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun GitHubStatusScreen() {
    val currentAccount = LocalAccountInstance.current ?: return

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val scaffoldState = rememberScaffoldState()

        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        ) {
            val contentPaddings = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            )

            val viewModel = viewModel(
                initializer = {
                    GitHubStatusViewModel(accountInstance = currentAccount)
                }
            )

            val resource by viewModel.requestResource.observeAsState()
            val status by viewModel.gitHubStatus.observeAsState()

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = resource?.status == Status.LOADING),
                onRefresh = viewModel::refresh,
                indicatorPadding = contentPaddings,
                indicator = { state, refreshTriggerDistance ->
                    DefaultSwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = refreshTriggerDistance
                    )
                }
            ) {
                when {
                    status == null
                            && resource?.status == Status.SUCCESS -> {
                        EmptyScreenContent(
                            titleId = R.string.common_no_data_found,
                            action = viewModel::refresh
                        )
                    }
                    status == null
                            && resource?.status == Status.ERROR -> {
                        EmptyScreenContent(
                            action = viewModel::refresh,
                            throwable = resource?.e
                        )
                    }
                    else -> {
                        GitHubStatusScreenContent(
                            status = status,
                            contentPaddings = contentPaddings,
                            enablePlaceholder = status == null
                                    && resource?.status == Status.LOADING
                        )
                    }
                }
            }

            if (status != null
                && resource?.e != null
            ) {
                SnackBarErrorMessage(
                    scaffoldState = scaffoldState,
                    dismissAction = viewModel::onGitHubStatusDataErrorDismissed
                )
            }
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.github_status_title))
            },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun GitHubStatusScreenContent(
    contentPaddings: PaddingValues,
    status: GitHubStatus?,
    enablePlaceholder: Boolean
) {
    val placeholder = remember {
        GitHubStatusProvider().values.first()
    }

    LazyColumn(
        contentPadding = contentPaddings,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            GitHubStatusStatusItem(
                status = status?.status ?: placeholder.status,
                enablePlaceholder = enablePlaceholder
            )
        }

        if (enablePlaceholder) {
            items(count = 6) {
                GitHubStatusComponentItem(
                    component = placeholder.components.first(),
                    enablePlaceholder = true
                )
            }
        } else {
            items(status?.components.orEmpty().size) {
                GitHubStatusComponentItem(
                    component = status?.components.orEmpty()[it],
                    enablePlaceholder = false
                )
            }
        }

        item {
            GitHubStatusPageItem(
                page = (status ?: placeholder).page,
                enablePlaceholder = enablePlaceholder
            )
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun GitHubStatusStatusItem(
    status: GitHubStatusStatus,
    enablePlaceholder: Boolean
) {
    ListItem(
        icon = {
            status.indicator.icon().let {
                if (it is ImageVector) {
                    Icon(
                        imageVector = it,
                        contentDescription = status.description,
                        tint = Color.White,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                } else {
                    Icon(
                        painter = it as Painter,
                        contentDescription = status.description,
                        tint = Color.White,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
            }
        },
        modifier = Modifier.background(
            color = if (enablePlaceholder) {
                MaterialTheme.colors.background
            } else {
                status.indicator.color
            }
        )
    ) {
        Text(
            text = status.description,
            color = Color.White,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun GitHubStatusComponentItem(
    component: GitHubStatusComponent,
    enablePlaceholder: Boolean
) {
    ListItem(
        secondaryText = {
            Text(
                text = stringResource(
                    id = when (component.status) {
                        Operational -> {
                            R.string.github_status_operational
                        }
                        DegradedPerformance -> {
                            R.string.github_status_degraded_performance
                        }
                        PartialOutage -> {
                            R.string.github_status_partial_outage
                        }
                        MajorOutage -> {
                            R.string.github_status_major_outage
                        }
                        UnderMaintenance -> {
                            R.string.github_status_under_maintenance
                        }
                    }
                ),
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        },
        trailing = {
            component.status.icon().let {
                val modifier = Modifier
                    .size(size = IconSize)
                    .padding(all = ContentPaddingMediumSize)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                if (it is ImageVector) {
                    Icon(
                        imageVector = it,
                        contentDescription = component.description,
                        tint = component.status.color,
                        modifier = modifier
                    )
                } else {
                    Icon(
                        painter = it as Painter,
                        contentDescription = component.description,
                        tint = component.status.color,
                        modifier = modifier
                    )
                }
            }
        }
    ) {
        Text(
            text = component.name,
            modifier = Modifier.placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade()
            )
        )
    }
}

@Composable
private fun GitHubStatusPageItem(
    page: GitHubStatusPage,
    enablePlaceholder: Boolean
) {
    val formattedDateTime = remember(key1 = page.updatedAt) {
        page.updatedAt.formatDateWithDefaultLocale()
    }
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .padding(all = ContentPaddingLargeSize)
            .fillMaxWidth()
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = R.string.github_status_updated_at, formattedDateTime),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.End,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview(
    name = "GitHubStatusScreenContentPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun GitHubStatusScreenContentPreview(
    @PreviewParameter(
        provider = GitHubStatusProvider::class,
        limit = 1
    )
    status: GitHubStatus
) {
    GitHubStatusScreenContent(
        status = status,
        contentPaddings = PaddingValues(),
        enablePlaceholder = false
    )
}

private val GitHubStatusStatusIndicator.color: Color
    get() = when (this) {
        None -> {
            StatusColorGreen
        }
        Minor -> {
            StatusColorYellow
        }
        Major -> {
            StatusColorOrange
        }
        Critical -> {
            StatusColorRed
        }
        Maintenance -> {
            StatusColorBlue
        }
    }

@Composable
private fun GitHubStatusStatusIndicator.icon(): Any {
    return when (this) {
        None -> {
            Icons.Outlined.Check
        }
        Minor,
        Major -> {
            painterResource(id = R.drawable.ic_priority_high)
        }
        Critical -> {
            Icons.Outlined.Close
        }
        Maintenance -> {
            Icons.Outlined.Build
        }
    }
}

private val GitHubStatusComponentStatus.color: Color
    get() = when (this) {
        Operational -> {
            StatusColorGreen
        }
        DegradedPerformance -> {
            StatusColorYellow
        }
        PartialOutage -> {
            StatusColorOrange
        }
        MajorOutage -> {
            StatusColorRed
        }
        UnderMaintenance -> {
            StatusColorBlue
        }
    }

@Composable
private fun GitHubStatusComponentStatus.icon(): Any {
    return when (this) {
        Operational -> {
            Icons.Outlined.CheckCircle
        }
        DegradedPerformance,
        PartialOutage -> {
            painterResource(R.drawable.ic_warning_amber)
        }
        MajorOutage -> {
            painterResource(R.drawable.ic_error)
        }
        UnderMaintenance -> {
            Icons.Outlined.Build
        }
    }
}

private val StatusColorGreen = Color(0xff28a745)
private val StatusColorOrange = Color(0xffe36209)
private val StatusColorYellow = Color(0xffdbab09)
private val StatusColorRed = Color(0xffdc3545)
private val StatusColorBlue = Color(0xff0366d6)