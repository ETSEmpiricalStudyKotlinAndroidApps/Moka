package io.github.tonnyl.moka.ui

import androidx.activity.ComponentActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.WindowInsets
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.ui.about.AboutScreenContent
import io.github.tonnyl.moka.ui.about.AboutScreenTestTag
import io.github.tonnyl.moka.ui.about.OnAboutItemClick
import io.github.tonnyl.moka.ui.theme.MokaTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalAnimatedInsets
class AboutScreenTest {

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
                        AboutScreenContent(
                            topAppBarSize = 0,
                            onItemClick = OnAboutItemClick(
                                onWhatsNewClick = {},
                                onViewInStoreClick = {},
                                onJoinBetaClick = {},
                                onPrivacyPolicyClick = {},
                                onTermsOfServiceClick = {},
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