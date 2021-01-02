package io.github.tonnyl.moka.ui.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Position
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.proto.Settings
import io.github.tonnyl.moka.serializers.store.SettingSerializer
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.widget.PreferenceCategoryText
import io.github.tonnyl.moka.widget.PreferenceDivider
import io.github.tonnyl.moka.widget.TopAppBarElevation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val SettingScreenTestTag = "SettingScreenTestTag"
const val ChooseThemeTestTag = "ChooseThemeTestTag"
const val EnableNotificationsTestTag = "EnableNotificationsTestTag"
const val SyncIntervalTestTag = "SyncIntervalTestTag"

data class InitialParams(
    val theme: Settings.Theme,
    val enableNotifications: Boolean,
    val syncInterval: Settings.NotificationSyncInterval,
    val dnd: Boolean,
    val autoSave: Boolean,
    val doNotKeepSearchHistory: Boolean,
    val keepData: Settings.KeepData
)

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

@Composable
fun SettingScreen() {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val mokaApp = AmbientContext.current.applicationContext as MokaApp
    val settings by mokaApp.settingsDataStore
        .data
        .collectAsState(initial = SettingSerializer.defaultValue)

    MokaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    title = {
                        Text(text = stringResource(R.string.navigation_menu_settings))
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { /*findNavController().navigateUp()*/ },
                            content = { Icon(imageVector = vectorResource(R.drawable.ic_arrow_back_24)) }
                        )
                    },
                    elevation = TopAppBarElevation(lifted = scrollState.value != .0f)
                )
            }
        ) {
            SettingScreenContent(
                scrollState = scrollState,
                initialParams = InitialParams(
                    theme = settings.theme,
                    enableNotifications = settings.enableNotifications,
                    syncInterval = settings.notificationSyncInterval,
                    dnd = settings.dnd,
                    autoSave = settings.autoSave,
                    doNotKeepSearchHistory = settings.doNotKeepSearchHistory,
                    keepData = settings.keepData
                ),
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
        }
    }

}

@Composable
fun SettingScreenContent(
    scrollState: ScrollState,
    initialParams: InitialParams,
    onSettingItemClick: OnSettingItemClick
) {
    var themeExpanded by remember { mutableStateOf(false) }
    var keepDataExpanded by remember { mutableStateOf(false) }
    var syncIntervalExpanded by remember { mutableStateOf(false) }

    var themeValue by remember { mutableStateOf(initialParams.theme) }
    var enableNotificationsValue by remember { mutableStateOf(initialParams.enableNotifications) }
    var syncIntervalValue by remember { mutableStateOf(initialParams.syncInterval) }
    var dndValue by remember { mutableStateOf(initialParams.dnd) }
    var autoSaveValue by remember { mutableStateOf(initialParams.autoSave) }
    var doNotKeepSearchHistoryValue by remember { mutableStateOf(initialParams.doNotKeepSearchHistory) }
    var keepDataValue by remember { mutableStateOf(initialParams.keepData) }

    ScrollableColumn(
        scrollState = scrollState,
        modifier = Modifier.testTag(SettingScreenTestTag)
    ) {
        PreferenceCategoryText(text = stringResource(R.string.settings_theme_category))

        DropdownMenu(
            toggle = {
                // fix the position
                Box(Modifier)
            },
            expanded = themeExpanded,
            onDismissRequest = {
                themeExpanded = false
            },
            dropdownOffset = Position(
                x = dimensionResource(R.dimen.fragment_content_padding),
                y = 0.dp
            )
        ) {
            Settings.Theme.values().filter {
                it != Settings.Theme.UNRECOGNIZED
            }.map { themeOption ->
                DropdownMenuItem(
                    onClick = {
                        themeValue = themeOption
                        onSettingItemClick.onThemeClick.invoke(themeOption)
                        themeExpanded = false
                    },
                    modifier = Modifier.background(
                        color = if (themeOption == themeValue) {
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
        ListItem(
            modifier = Modifier.testTag(ChooseThemeTestTag)
                .clickable {
                    themeExpanded = true
                },
            secondaryText = {
                Text(text = getThemeValuesText(themeValue))
            }
        ) {
            Text(text = stringResource(R.string.settings_theme_title))
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.settings_notifications_category))

        ListItem(
            modifier = Modifier.testTag(EnableNotificationsTestTag)
                .clickable {
                    enableNotificationsValue = !enableNotificationsValue
                    onSettingItemClick.onEnableNotificationClick.invoke(enableNotificationsValue)
                },
            trailing = {
                Switch(
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                    checked = enableNotificationsValue,
                    onCheckedChange = {
                        enableNotificationsValue = !enableNotificationsValue
                        onSettingItemClick.onEnableNotificationClick.invoke(enableNotificationsValue)
                    }
                )
            }
        ) {
            Text(text = stringResource(R.string.settings_enable_notifications_title))
        }

        DropdownMenu(
            toggle = {
                // fix the position
                Box(Modifier)
            },
            expanded = syncIntervalExpanded,
            onDismissRequest = {
                syncIntervalExpanded = false
            },
            dropdownOffset = Position(
                x = dimensionResource(R.dimen.fragment_content_padding),
                y = 0.dp
            )
        ) {
            Settings.NotificationSyncInterval.values()
                .filter {
                    it != Settings.NotificationSyncInterval.UNRECOGNIZED
                }
                .map { intervalOption ->
                    DropdownMenuItem(
                        onClick = {
                            syncIntervalValue = intervalOption
                            onSettingItemClick.onSyncIntervalClick.invoke(intervalOption)
                            syncIntervalExpanded = false
                        },
                        modifier = Modifier.background(
                            color = if (intervalOption == syncIntervalValue) {
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

        if (enableNotificationsValue) {
            ListItem(
                modifier = Modifier.testTag(SyncIntervalTestTag)
                    .clickable {
                        syncIntervalExpanded = true
                    },
                secondaryText = {
                    Text(
                        text = getNotificationSyncIntervalsText(syncIntervalValue)
                    )
                }
            ) {
                Text(text = stringResource(R.string.settings_sync_interval_title))
            }

            ListItem(
                modifier = Modifier.clickable {
                    dndValue = !dndValue
                    onSettingItemClick.onDndClick.invoke(dndValue)
                },
                secondaryText = {
                    Text(text = stringResource(R.string.settings_do_not_disturb_summary))
                },
                trailing = {
                    Switch(
                        colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                        checked = dndValue,
                        onCheckedChange = {
                            dndValue = !dndValue
                            onSettingItemClick.onDndClick.invoke(dndValue)
                        }
                    )
                }
            ) {
                Text(text = stringResource(R.string.settings_do_not_disturb_title))
            }
            ListItem(
                secondaryText = {
                    Text(text = stringResource(R.string.settings_notifications_summary))
                },
                icon = {
                    Icon(
                        imageVector = vectorResource(R.drawable.ic_info_24),
                        tint = AmbientContentColor.current.copy(alpha = ContentAlpha.medium),
                        modifier = Modifier.preferredSize(40.dp)
                            .padding(6.dp)
                    )
                }
            ) {

            }
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.settings_drafts_category))

        ListItem(
            modifier = Modifier.clickable {
                autoSaveValue = !autoSaveValue
                onSettingItemClick.onAutoSaveClick.invoke(autoSaveValue)
            },
            trailing = {
                Switch(
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                    checked = autoSaveValue,
                    onCheckedChange = {
                        autoSaveValue = !autoSaveValue
                        onSettingItemClick.onAutoSaveClick.invoke(autoSaveValue)
                    }
                )
            }
        ) {
            Text(text = stringResource(R.string.settings_auto_save_title))
        }

        ListItem(
            modifier = Modifier.clickable {

            }
        ) {
            Text(text = stringResource(R.string.settings_clear_drafts_title))
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.settings_search_category))

        ListItem(
            modifier = Modifier.clickable {
                doNotKeepSearchHistoryValue = !doNotKeepSearchHistoryValue
                onSettingItemClick.onDoNotKeepSearchHistoryClick.invoke(doNotKeepSearchHistoryValue)
            },
            trailing = {
                Switch(
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.secondary),
                    checked = doNotKeepSearchHistoryValue,
                    onCheckedChange = {
                        doNotKeepSearchHistoryValue = !doNotKeepSearchHistoryValue
                        onSettingItemClick.onDoNotKeepSearchHistoryClick.invoke(
                            doNotKeepSearchHistoryValue
                        )
                    }
                )
            }
        ) {
            Text(text = stringResource(R.string.settings_do_not_keep_search_history_title))
        }

        ListItem(
            modifier = Modifier.clickable {

            }
        ) {
            Text(text = stringResource(R.string.settings_clear_search_history_title))
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.settings_cache_category))

        ListItem(
            modifier = Modifier.clickable {
                keepDataExpanded = true
            },
            secondaryText = {
                Text(text = getKeepDataTimesText(keepDataValue))
            }
        ) {
            Text(text = stringResource(R.string.settings_keep_data_title))
        }
        DropdownMenu(
            toggle = {
                // fix the position
                Box(Modifier)
            },
            expanded = keepDataExpanded,
            onDismissRequest = {
                keepDataExpanded = false
            },
            dropdownOffset = Position(
                x = dimensionResource(R.dimen.fragment_content_padding),
                y = 0.dp
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
                            keepDataValue = keepDataOption
                            onSettingItemClick.onKeepDataClick.invoke(keepDataOption)
                        },
                        modifier = Modifier.background(
                            color = if (keepDataOption == keepDataValue) {
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

        ListItem(
            modifier = Modifier.clickable {

            }
        ) {
            Text(text = stringResource(R.string.settings_clear_local_data_title))
        }

        ListItem(
            modifier = Modifier.clickable {

            }
        ) {
            Text(text = stringResource(R.string.settings_clear_image_cache_title))
        }

        Spacer(modifier = Modifier.preferredHeight(dimensionResource(R.dimen.fragment_content_padding_half)))

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

@Preview(
    name = "SettingScreenContent",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4
)
@Composable
private fun SettingScreenContentPreview() {
    SettingScreenContent(
        scrollState = rememberScrollState(),
        initialParams = InitialParams(
            theme = Settings.Theme.AUTO,
            enableNotifications = true,
            syncInterval = Settings.NotificationSyncInterval.ONE_QUARTER,
            dnd = true,
            autoSave = true,
            doNotKeepSearchHistory = false,
            keepData = Settings.KeepData.FOREVER
        ),
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