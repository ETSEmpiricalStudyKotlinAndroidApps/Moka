package io.github.tonnyl.moka.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Providers
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.WindowInsets
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.proto.Settings
import io.github.tonnyl.moka.ui.settings.*
import io.github.tonnyl.moka.ui.theme.MokaTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var activity: AppCompatActivity

    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity { newActivity ->
            activity = newActivity

            val windowInsets = WindowInsets()
            composeTestRule.setContent {
                Providers(AmbientWindowInsets provides windowInsets) {
                    MokaTheme {
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
                }
            }
        }
    }

    @Test
    fun app_launches() {
        composeTestRule.onNodeWithTag(SettingScreenTestTag).assertIsDisplayed()
    }

    @Test
    fun userClick_dropdownMenuAppears() {
        composeTestRule.onNodeWithTag(ChooseThemeTestTag)
            .performClick()

        val themeAutoOption =
            composeTestRule.onNodeWithText(activity.getString(R.string.settings_choose_theme_auto))

        // Check the theme auto dropdown menu item is shown
        themeAutoOption.assertExists()

        // Choose theme auto
        themeAutoOption.performClick()

        // Check the theme auto dropdown menu is hidden
        themeAutoOption.assertDoesNotExist()
    }

    @Test
    fun userClick_dependencyChanges() {
        val notificationsItem = composeTestRule.onNodeWithTag(EnableNotificationsTestTag)

        // Make switch unchecked
        notificationsItem.performClick()

        // Check the sync interval item is hidden
        composeTestRule.onNodeWithTag(SyncIntervalTestTag)
            .assertDoesNotExist()

        // Make switch checked
        notificationsItem.performClick()

        // Check the sync interval item is hidden
        composeTestRule.onNodeWithTag(SyncIntervalTestTag)
            .assertExists()
    }

}