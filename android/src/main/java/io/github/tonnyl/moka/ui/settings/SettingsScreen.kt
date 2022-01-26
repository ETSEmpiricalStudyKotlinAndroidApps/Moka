package io.github.tonnyl.moka.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.PreferenceCategoryText
import io.github.tonnyl.moka.widget.PreferenceDivider
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import io.tonnyl.moka.common.network.Status
import io.tonnyl.moka.common.store.SettingSerializer
import io.tonnyl.moka.common.store.data.KeepData
import io.tonnyl.moka.common.store.data.NotificationSyncInterval
import io.tonnyl.moka.common.store.data.Settings
import io.tonnyl.moka.common.store.data.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.ExperimentalSerializationApi

const val SettingScreenTestTag = "SettingScreenTestTag"
const val ChooseThemeTestTag = "ChooseThemeTestTag"
const val EnableNotificationsTestTag = "EnableNotificationsTestTag"
const val SyncIntervalTestTag = "SyncIntervalTestTag"

@ExperimentalSerializationApi
data class OnSettingItemClick(
    val onThemeClick: (Theme) -> Unit,
    val onEnableNotificationClick: (Boolean) -> Unit,
    val onSyncIntervalClick: (NotificationSyncInterval) -> Unit,
    val onDndClick: (Boolean) -> Unit,
    val onAutoSaveClick: (Boolean) -> Unit,
    val onDoNotKeepSearchHistoryClick: (Boolean) -> Unit,
    val onKeepDataClick: (KeepData) -> Unit,
    val onClearDraftsClick: () -> Unit,
    val onClearSearchHistory: () -> Unit,
    val onClearLocalData: () -> Unit,
    val onClearImageCacheClick: () -> Unit
)

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun SettingScreen() {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel<SettingsViewModel>(
        factory = ViewModelFactory(
            accountInstance = currentAccount,
            context = LocalContext.current
        )
    )

    val scaffoldState = rememberScaffoldState()

    val clearHistoryState by viewModel.clearHistoryStatus.observeAsState()
    val updateSettingsState by viewModel.updateSettingsStatus.observeAsState()

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        Scaffold(
            content = {
                SettingScreenContent(
                    topAppBarSize = topAppBarSize,
                    settingsFlow = viewModel.settingsDataStore.data,
                    onSettingItemClick = OnSettingItemClick(
                        onThemeClick = { newTheme ->
                            viewModel.updateSettings(theme = newTheme)
                        },
                        onEnableNotificationClick = { newEnableNotifications ->
                            viewModel.updateSettings(enableNotifications = newEnableNotifications)
                        },
                        onSyncIntervalClick = { newNotificationSyncInterval ->
                            viewModel.updateSettings(notificationSyncInterval = newNotificationSyncInterval)
                        },
                        onDndClick = { newDnd ->
                            viewModel.updateSettings(dnd = newDnd)
                        },
                        onAutoSaveClick = { newAutoSave ->
                            viewModel.updateSettings(autoSave = newAutoSave)
                        },
                        onDoNotKeepSearchHistoryClick = { newDoNotKeepSearchHistory ->
                            viewModel.updateSettings(doNotKeepSearchHistory = newDoNotKeepSearchHistory)
                            if (newDoNotKeepSearchHistory) {
                                viewModel.clearSearchHistory(updateStatus = false)
                            }
                        },
                        onKeepDataClick = { newKeepData ->
                            viewModel.updateSettings(keepData = newKeepData)
                        },
                        onClearDraftsClick = {},
                        onClearSearchHistory = viewModel::clearSearchHistory,
                        onClearLocalData = {},
                        onClearImageCacheClick = {}
                    )
                )

                if (updateSettingsState == Status.ERROR) {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        messageId = R.string.update_settings_failed
                    )
                }

                when (clearHistoryState) {
                    Status.SUCCESS -> {
                        SnackBarErrorMessage(
                            scaffoldState = scaffoldState,
                            messageId = R.string.clear_search_history_succeeded
                        )
                    }
                    Status.ERROR -> {
                        SnackBarErrorMessage(
                            scaffoldState = scaffoldState,
                            messageId = R.string.clear_search_history_failed
                        )
                    }
                    else -> {

                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    Snackbar(snackbarData = data)
                }
            },
            scaffoldState = scaffoldState
        )

        val navController = LocalNavController.current
        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.navigation_menu_settings))
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.navigateUp()
                    },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            imageVector = Icons.Outlined.ArrowBack
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

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun SettingScreenContent(
    topAppBarSize: Int,
    settingsFlow: Flow<Settings>,
    onSettingItemClick: OnSettingItemClick
) {
    var themeExpanded by remember { mutableStateOf(false) }
    var keepDataExpanded by remember { mutableStateOf(false) }
    var syncIntervalExpanded by remember { mutableStateOf(false) }

    val settings by settingsFlow.collectAsState(initial = SettingSerializer.defaultValue)

    LazyColumn(
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        ),
        modifier = Modifier.testTag(tag = SettingScreenTestTag)
    ) {
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.settings_theme_category))
        }
        item {
            Box {
                ListItem(
                    modifier = Modifier
                        .testTag(tag = ChooseThemeTestTag)
                        .clickable {
                            themeExpanded = true
                        },
                    secondaryText = {
                        Text(text = getThemeValuesText(settings.theme))
                    }
                ) {
                    Text(text = stringResource(id = R.string.settings_theme_title))
                }
                DropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = {
                        themeExpanded = false
                    },
                    offset = DpOffset(
                        x = ContentPaddingLargeSize,
                        y = (-24).dp
                    )
                ) {
                    Theme.values().map { themeOption ->
                        DropdownMenuItem(
                            onClick = {
                                onSettingItemClick.onThemeClick.invoke(themeOption)
                                themeExpanded = false
                            },
                            modifier = Modifier.background(
                                color = if (themeOption == settings.theme) {
                                    MaterialTheme.colors.onBackground.copy(alpha = .12f)
                                } else {
                                    MaterialTheme.colors.background
                                }
                            )
                        ) {
                            Text(
                                text = getThemeValuesText(themeOption)
                            )
                        }
                    }
                }
            }
        }
        item {
            PreferenceDivider()
            PreferenceCategoryText(text = stringResource(id = R.string.settings_notifications_category))
        }
        item {
            Box {
                ListItem(
                    modifier = Modifier
                        .testTag(tag = EnableNotificationsTestTag)
                        .clickable {
                            onSettingItemClick.onEnableNotificationClick.invoke(
                                !settings.enableNotifications
                            )
                        },
                    trailing = {
                        Switch(
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                            checked = settings.enableNotifications,
                            onCheckedChange = {
                                onSettingItemClick.onEnableNotificationClick.invoke(
                                    !settings.enableNotifications
                                )
                            }
                        )
                    }
                ) {
                    Text(text = stringResource(id = R.string.settings_enable_notifications_title))
                }
                DropdownMenu(
                    expanded = syncIntervalExpanded,
                    onDismissRequest = {
                        syncIntervalExpanded = false
                    },
                    offset = DpOffset(
                        x = ContentPaddingLargeSize,
                        y = (-24).dp
                    )
                ) {
                    NotificationSyncInterval.values()
                        .map { intervalOption ->
                            DropdownMenuItem(
                                onClick = {
                                    onSettingItemClick.onSyncIntervalClick.invoke(intervalOption)
                                    syncIntervalExpanded = false
                                },
                                modifier = Modifier.background(
                                    color = if (intervalOption == settings.notificationSyncInterval) {
                                        MaterialTheme.colors.onBackground.copy(alpha = .12f)
                                    } else {
                                        MaterialTheme.colors.background
                                    }
                                )
                            ) {
                                Text(text = getNotificationSyncIntervalsText(intervalOption))
                            }
                        }
                }
            }
        }
        if (settings.enableNotifications) {
            item {
                ListItem(
                    modifier = Modifier
                        .testTag(tag = SyncIntervalTestTag)
                        .clickable {
                            syncIntervalExpanded = true
                        },
                    secondaryText = {
                        Text(
                            text = getNotificationSyncIntervalsText(settings.notificationSyncInterval)
                        )
                    }
                ) {
                    Text(text = stringResource(id = R.string.settings_sync_interval_title))
                }
            }
            item {
                ListItem(
                    modifier = Modifier.clickable {
                        onSettingItemClick.onDndClick.invoke(!settings.dnd)
                    },
                    secondaryText = {
                        Text(text = stringResource(id = R.string.settings_do_not_disturb_summary))
                    },
                    trailing = {
                        Switch(
                            colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                            checked = settings.dnd,
                            onCheckedChange = {
                                onSettingItemClick.onDndClick.invoke(!settings.dnd)
                            }
                        )
                    }
                ) {
                    Text(text = stringResource(id = R.string.settings_do_not_disturb_title))
                }
            }
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    ListItem(
                        secondaryText = {
                            Text(text = stringResource(id = R.string.settings_notifications_summary))
                        },
                        icon = {
                            Icon(
                                contentDescription = null,
                                imageVector = Icons.Outlined.Info,
                                tint = MaterialTheme.colors.onBackground.copy(LocalContentAlpha.current),
                                modifier = Modifier
                                    .size(size = 40.dp)
                                    .padding(all = 6.dp)
                            )
                        }
                    ) {

                    }
                }
            }
        }
        item {
            PreferenceDivider()
            PreferenceCategoryText(text = stringResource(id = R.string.settings_drafts_category))
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onSettingItemClick.onAutoSaveClick.invoke(!settings.autoSave)
                },
                trailing = {
                    Switch(
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                        checked = settings.autoSave,
                        onCheckedChange = {
                            onSettingItemClick.onAutoSaveClick.invoke(!settings.autoSave)
                        }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.settings_auto_save_title))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {

                }
            ) {
                Text(text = stringResource(id = R.string.settings_clear_drafts_title))
            }
            PreferenceDivider()
        }
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.settings_search_category))
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onSettingItemClick.onDoNotKeepSearchHistoryClick.invoke(
                        !settings.doNotKeepSearchHistory
                    )
                },
                trailing = {
                    Switch(
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                        checked = settings.doNotKeepSearchHistory,
                        onCheckedChange = {
                            onSettingItemClick.onDoNotKeepSearchHistoryClick.invoke(
                                !settings.doNotKeepSearchHistory
                            )
                        }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.settings_do_not_keep_search_history_title))
            }
        }
        item {
            ListItem(modifier = Modifier.clickable(onClick = onSettingItemClick.onClearSearchHistory)) {
                Text(text = stringResource(id = R.string.settings_clear_search_history_title))
            }
            PreferenceDivider()
        }
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.settings_cache_category))
        }
        item {
            Box {
                ListItem(
                    modifier = Modifier.clickable {
                        keepDataExpanded = true
                    },
                    secondaryText = {
                        Text(text = getKeepDataTimesText(settings.keepData))
                    }
                ) {
                    Text(text = stringResource(id = R.string.settings_keep_data_title))
                }
                DropdownMenu(
                    expanded = keepDataExpanded,
                    onDismissRequest = {
                        keepDataExpanded = false
                    },
                    offset = DpOffset(
                        x = ContentPaddingLargeSize,
                        y = (-48).dp
                    )
                ) {
                    KeepData.values()
                        .map { keepDataOption ->
                            DropdownMenuItem(
                                onClick = {
                                    keepDataExpanded = false
                                    onSettingItemClick.onKeepDataClick.invoke(keepDataOption)
                                },
                                modifier = Modifier.background(
                                    color = if (keepDataOption == settings.keepData) {
                                        MaterialTheme.colors.onBackground.copy(alpha = .12f)
                                    } else {
                                        MaterialTheme.colors.background
                                    }
                                )
                            ) {
                                Text(text = getKeepDataTimesText(keepDataOption))
                            }
                        }
                }
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {

                }
            ) {
                Text(text = stringResource(id = R.string.settings_clear_local_data_title))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {

                }
            ) {
                Text(text = stringResource(id = R.string.settings_clear_image_cache_title))
            }
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        }
    }
}

@ExperimentalSerializationApi
@Composable
private fun getThemeValuesText(theme: Theme): String {
    return stringResource(
        when (theme) {
            Theme.AUTO -> {
                R.string.settings_choose_theme_auto
            }
            Theme.LIGHT -> {
                R.string.settings_choose_theme_light
            }
            Theme.DARK -> {
                R.string.settings_choose_theme_dark
            }
        }
    )
}

@ExperimentalSerializationApi
@Composable
private fun getNotificationSyncIntervalsText(intervals: NotificationSyncInterval): String {
    return stringResource(
        when (intervals) {
            NotificationSyncInterval.ONE_QUARTER -> {
                R.string.sync_interval_one_quarter
            }
            NotificationSyncInterval.THIRTY_MINUTES -> {
                R.string.sync_interval_thirty_minutes
            }
            NotificationSyncInterval.ONE_HOUR -> {
                R.string.sync_interval_one_hour
            }
            NotificationSyncInterval.TWO_HOURS -> {
                R.string.sync_interval_two_hours
            }
            NotificationSyncInterval.SIX_HOURS -> {
                R.string.sync_interval_six_hours
            }
            NotificationSyncInterval.TWELVE_HOURS -> {
                R.string.sync_interval_twelve_hours
            }
            NotificationSyncInterval.TWENTY_FOUR_HOURS -> {
                R.string.sync_interval_twenty_four_hours
            }
        }
    )
}

@ExperimentalSerializationApi
@Composable
private fun getKeepDataTimesText(keepData: KeepData): String {
    return stringResource(
        when (keepData) {
            KeepData.ONE_DAY -> {
                R.string.keep_data_one_day
            }
            KeepData.THREE_DAYS -> {
                R.string.keep_data_three_days
            }
            KeepData.ONE_WEEK -> {
                R.string.keep_data_one_week
            }
            KeepData.ONE_MONTH -> {
                R.string.keep_data_one_month
            }
            KeepData.FOREVER -> {
                R.string.keep_data_forever
            }
        }
    )
}

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Preview(
    name = "SettingScreenContent",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4
)
@Composable
private fun SettingScreenContentPreview() {
    SettingScreenContent(
        topAppBarSize = 0,
        settingsFlow = flowOf(SettingSerializer.defaultValue),
        onSettingItemClick = OnSettingItemClick(
            onThemeClick = {},
            onEnableNotificationClick = {},
            onSyncIntervalClick = {},
            onDndClick = {},
            onAutoSaveClick = {},
            onDoNotKeepSearchHistoryClick = {},
            onKeepDataClick = {},
            onClearDraftsClick = {},
            onClearSearchHistory = {},
            onClearLocalData = {},
            onClearImageCacheClick = {}
        )
    )
}