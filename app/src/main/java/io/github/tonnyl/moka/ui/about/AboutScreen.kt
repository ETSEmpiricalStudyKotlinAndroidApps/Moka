package io.github.tonnyl.moka.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.LocalNavController
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
    val onViewSourceCodeClick: () -> Unit,
    val onOpenSourceLicensesClick: () -> Unit,
    val onFaqClick: () -> Unit,
    val onFeedbackClick: () -> Unit
)

@ExperimentalMaterialApi
@Composable
fun AboutScreen() {
    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        AboutScreenContent(
            topAppBarSize = topAppBarSize,
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

        val navController = LocalNavController.current
        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.navigation_menu_about))
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
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
fun AboutScreenContent(
    topAppBarSize: Int,
    onItemClick: OnAboutItemClick
) {
    LazyColumn(
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            top = false,
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
            PreferenceCategoryText(text = stringResource(id = R.string.about_open_source_category))
        }
        item {
            ListItem(
                modifier = Modifier.clickable {
                    onItemClick.onViewSourceCodeClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_view_source_code_title))
            }
        }
        item {
            ListItem(
                secondaryText = {
                    Text(text = stringResource(id = R.string.about_open_source_licenses_summary))
                },
                modifier = Modifier.clickable {
                    onItemClick.onOpenSourceLicensesClick.invoke()
                }
            ) {
                Text(text = stringResource(id = R.string.about_open_source_licenses_title))
            }
            PreferenceDivider()
        }
        item {
            PreferenceCategoryText(text = stringResource(id = R.string.about_others_category))
        }
        item {
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
            onViewSourceCodeClick = {},
            onOpenSourceLicensesClick = {},
            onFaqClick = {},
            onFeedbackClick = {}
        )
    )
}