package io.github.tonnyl.moka.ui

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Providers
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import dev.chrisbanes.accompanist.insets.AmbientWindowInsets
import dev.chrisbanes.accompanist.insets.WindowInsets
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.ui.about.AboutScreenContent
import io.github.tonnyl.moka.ui.about.AboutScreenTestTag
import io.github.tonnyl.moka.ui.about.OnAboutItemClick
import io.github.tonnyl.moka.ui.theme.MokaTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AboutScreenTest {

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
                        AboutScreenContent(
                            scrollState = rememberScrollState(),
                            onItemClick = OnAboutItemClick(
                                onWhatsNewClick = {},
                                onViewInStoreClick = {},
                                onJoinBetaClick = {},
                                onPrivacyPolicyClick = {},
                                onTermsOfServiceClick = {},
                                onViewSourceCodeClick = {},
                                onOpenSourceLicensesClick = {},
                                onFaqClick = {},
                                onFeedbackClick = {}
                            )
                        )
                    }
                }
            }
        }
    }

    @Test
    fun app_launches() {
        composeTestRule.onNodeWithTag(AboutScreenTestTag).assertIsDisplayed()

        // Check the build version is correct
        composeTestRule.onAllNodes(hasText(BuildConfig.VERSION_NAME))
    }

}