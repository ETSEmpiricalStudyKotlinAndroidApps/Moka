package io.github.tonnyl.moka.ui.about

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.PreferenceCategoryText
import io.github.tonnyl.moka.widget.PreferenceDivider

const val AboutScreenTestTag = "AboutScreenTestTag"

data class OnAboutItemClick(
    val onWhatsNewClick: () -> Unit,
    val onViewInStoreClick: () -> Unit,
    val onJoinBetaClick: () -> Unit,
    val onPrivacyPolicyClick: () -> Unit,
    val onTermsOfServiceClick: () -> Unit,
    val onOpenSourceLicensesClick: () -> Unit,
    val onFaqClick: () -> Unit,
    val onFeedbackClick: () -> Unit
)

@ExperimentalMaterialApi
@Composable
fun AboutScreen() {
    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val context = LocalContext.current
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        AboutScreenContent(
            topAppBarSize = topAppBarSize,
            onItemClick = OnAboutItemClick(
                onWhatsNewClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_CHANGELOG))
                },
                onViewInStoreClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_GOOGLE_PLAY))
                },
                onJoinBetaClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_JOINING_BETA))
                },
                onPrivacyPolicyClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_PRIVACY_POLICY))
                },
                onTermsOfServiceClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_TERMS_OF_SERVICE))
                },
                onOpenSourceLicensesClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_OPEN_SOURCE_LICENSES))
                },
                onFaqClick = {
                    customTabsIntent.launchUrl(context, Uri.parse(URL_OF_FAQ))
                },
                onFeedbackClick = {}
            )
        )

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.navigation_menu_about))
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
fun AboutScreenContent(
    topAppBarSize: Int,
    onItemClick: OnAboutItemClick
) {
    LazyColumn(
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        ),
        modifier = Modifier.testTag(tag = AboutScreenTestTag)
    ) {
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.about_version_info_category))
        }
        item {
            ListItem(
                secondaryText = {
                    Text(text = BuildConfig.VERSION_NAME)
                }
            ) {
                Text(text = stringResource(id = R.string.about_build_version_title))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable(
                    onClick = {
                        onItemClick.onWhatsNewClick.invoke()
                    }
                )
            ) {
                Text(text = stringResource(id = R.string.about_whats_new_title))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onViewInStoreClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_view_in_google_play_store_title))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onJoinBetaClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_join_the_beta_program_title))
            }
            PreferenceDivider()
        }
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.about_terms_privacy_category))
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onPrivacyPolicyClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_privacy_policy))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onTermsOfServiceClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_terms_of_service))
            }
            PreferenceDivider()
        }
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.about_others_category))
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onOpenSourceLicensesClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_open_source_licenses_title))
            }
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onFaqClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.navigation_menu_faq_help))
            }
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onFeedbackClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.navigation_menu_feedback))
            }
        }
        item {
            Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview(
    name = "AboutScreenContentPreview",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4
)
private fun AboutScreenContentPreview() {
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

const val URL_OF_PRIVACY_POLICY = "https://tonnyl.github.io/android/privacy-policy.html"
const val URL_OF_TERMS_OF_SERVICE = "https://tonnyl.github.io/android/terms-conditions.html"
private const val URL_OF_OPEN_SOURCE_LICENSES =
    "https://tonnyl.github.io/android/open-source-licenses.html"
const val URL_OF_FAQ = "https://tonnyl.github.io/guide/general.html"
private const val URL_OF_CHANGELOG = "https://tonnyl.github.io/android/changelog.html"
private const val URL_OF_GOOGLE_PLAY =
    "https://play.google.com/store/apps/details?id=io.github.tonnyl.moka"

// todo replace with formal link
private const val URL_OF_JOINING_BETA =
    "https://play.google.com/apps/internaltest/4699138972138868013"