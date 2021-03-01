package io.github.tonnyl.moka.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.proto.Settings
import io.github.tonnyl.moka.serializers.store.SettingSerializer
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.PreferenceCategoryText
import io.github.tonnyl.moka.widget.PreferenceDivider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

const val SettingScreenTestTag = "SettingScreenTestTag"
const val ChooseThemeTestTag = "ChooseThemeTestTag"
const val EnableNotificationsTestTag = "EnableNotificationsTestTag"
const val SyncIntervalTestTag = "SyncIntervalTestTag"

data class OnSettingItemClick(
    val onThemeClick: (Settings.Theme) -> Unit,
    val onEnableNotificationClick: (Boolean) -> Unit,
    val onSyncIntervalClick: (Settings.NotificationSyncInterval) -> Unit,
    val onDndClick: (Boolean) -> Unit,
    val onAutoSaveClick: (Boolean) -> Unit,
    val onDoNotKeepSearchHistoryClick: (Boolean) -> Unit,
    val onKeepDataClick: (Settings.KeepData) -> Unit,
    val onClearDraftsClick: () -> Unit,
    val onClearSearchHistory: () -> Unit,
    val onClearLocalData: () -> Unit,
    val onClearImageCacheClick: () -> Unit
)

@ExperimentalMaterialApi
@Composable
fun SettingScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val mokaApp = LocalContext.current.applicationContext as MokaApp

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        SettingScreenContent(
            topAppBarSize = topAppBarSize,
            settingsFlow = mokaApp.settingsDataStore.data,
            onSettingItemClick = OnSettingItemClick(
                onThemeClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        theme = it
                    }
                },
                onEnableNotificationClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        enableNotifications = !enableNotifications
                    }
                },
                onSyncIntervalClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        notificationSyncInterval = it
                    }
                },
                onDndClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        dnd = it
                    }
                },
                onAutoSaveClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        autoSave = it
                    }
                },
                onDoNotKeepSearchHistoryClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        doNotKeepSearchHistory = it
                    }
                },
                onKeepDataClick = {
                    updateSettingValue(coroutineScope, mokaApp.settingsDataStore) {
                        keepData = it
                    }
                },
                onClearDraftsClick = {},
                onClearSearchHistory = {},
                onClearLocalData = {},
                onClearImageCacheClick = {}
            )
        )

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
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
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
                    Settings.Theme.values().filter {
                        it != Settings.Theme.UNRECOGNIZED
                    }.map { themeOption ->
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
                    Settings.NotificationSyncInterval.values()
                        .filter {
                            it != Settings.NotificationSyncInterval.UNRECOGNIZED
                        }
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
                                painter = painterResource(id = R.drawable.ic_info_24),
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
                                settings.doNotKeepSearchHistory
                            )
                        }
                    )
                }
            ) {
                Text(text = stringResource(id = R.string.settings_do_not_keep_search_history_title))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {

                }
            ) {
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
                    Settings.KeepData.values()
                        .filter {
                            it != Settings.KeepData.UNRECOGNIZED
                        }
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

private fun updateSettingValue(
    scope: CoroutineScope,
    dataStore: DataStore<Settings>,
    block: Settings.Builder.() -> Unit
) {
    scope.launch {
        dataStore.updateData { currentSettings ->
            currentSettings.toBuilder().apply {
                block.invoke(this)
            }.build()
        }
    }
}

@Composable
private fun getThemeValuesText(theme: Settings.Theme): String {
    return stringResource(
        when (theme) { // `Else` should be never used here.
            Settings.Theme.AUTO,
            Settings.Theme.UNRECOGNIZED -> {
                R.string.settings_choose_theme_auto
            }
            Settings.Theme.LIGHT -> {
                R.string.settings_choose_theme_light
            }
            Settings.Theme.DARK -> {
                R.string.settings_choose_theme_dark
            }
        }
    )
}

@Composable
private fun getNotificationSyncIntervalsText(intervals: Settings.NotificationSyncInterval): String {
    return stringResource(
        when (intervals) { // `Else` should be never used here.
            Settings.NotificationSyncInterval.ONE_QUARTER,
            Settings.NotificationSyncInterval.UNRECOGNIZED -> {
                R.string.sync_interval_one_quarter
            }
            Settings.NotificationSyncInterval.THIRTY_MINUTES -> {
                R.string.sync_interval_thirty_minutes
            }
            Settings.NotificationSyncInterval.ONE_HOUR -> {
                R.string.sync_interval_one_hour
            }
            Settings.NotificationSyncInterval.TWO_HOURS -> {
                R.string.sync_interval_two_hours
            }
            Settings.NotificationSyncInterval.SIX_HOURS -> {
                R.string.sync_interval_six_hours
            }
            Settings.NotificationSyncInterval.TWELVE_HOURS -> {
                R.string.sync_interval_twelve_hours
            }
            Settings.NotificationSyncInterval.TWENTY_FOUR_HOURS -> {
                R.string.sync_interval_twenty_four_hours
            }
        }
    )
}

@Composable
private fun getKeepDataTimesText(keepData: Settings.KeepData): String {
    return stringResource(
        when (keepData) { // `Else` should be never used here.
            Settings.KeepData.FOREVER,
            Settings.KeepData.UNRECOGNIZED -> {
                R.string.keep_data_forever
            }
            Settings.KeepData.ONE_DAY -> {
                R.string.keep_data_one_day
            }
            Settings.KeepData.THREE_DAYS -> {
                R.string.keep_data_three_days
            }
            Settings.KeepData.ONE_WEEK -> {
                R.string.keep_data_one_week
            }
            Settings.KeepData.ONE_MONTH -> {
                R.string.keep_data_one_month
            }
        }
    )
}

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