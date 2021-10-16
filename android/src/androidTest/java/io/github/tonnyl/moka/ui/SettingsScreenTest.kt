package io.github.tonnyl.moka.ui

import androidx.activity.ComponentActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.WindowInsets
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.serializers.store.SettingSerializer
import io.github.tonnyl.moka.ui.settings.*
import io.github.tonnyl.moka.ui.theme.MokaTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalAnimatedInsets
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var activity: ComponentActivity

    @ExperimentalMaterialApi
    @Before
    fun setUp() {
        composeTestRule.activityRule.scenario.onActivity { newActivity ->
            activity = newActivity

            val windowInsets = WindowInsets()
            composeTestRule.setContent {
                CompositionLocalProvider(LocalWindowInsets provides windowInsets) {
                    MokaTheme {
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