package io.github.tonnyl.moka.ui.about

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.widget.PreferenceCategoryText
import io.github.tonnyl.moka.widget.PreferenceDivider
import io.github.tonnyl.moka.widget.TopAppBarElevation

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

@Composable
fun AboutScreen() {
    val scrollState = rememberScrollState()
    MokaTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = MaterialTheme.colors.surface,
                    title = {
                        Text(text = stringResource(R.string.navigation_menu_about))
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
            AboutScreenContent(
                scrollState = scrollState,
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

@Composable
fun AboutScreenContent(
    scrollState: ScrollState,
    onItemClick: OnAboutItemClick
) {
    ScrollableColumn(
        scrollState = scrollState,
        modifier = Modifier.testTag(AboutScreenTestTag)
    ) {
        PreferenceCategoryText(text = stringResource(R.string.about_version_info_category))

        ListItem(
            secondaryText = {
                Text(text = BuildConfig.VERSION_NAME)
            }
        ) {
            Text(text = stringResource(R.string.about_build_version_title))
        }

        ListItem(
            modifier = Modifier.clickable(
                onClick = {
                    onItemClick.onWhatsNewClick.invoke()
                }
            )
        ) {
            Text(text = stringResource(R.string.about_whats_new_title))
        }

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onViewInStoreClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.about_view_in_google_play_store_title))
        }

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onJoinBetaClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.about_join_the_beta_program_title))
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.about_terms_privacy_category))

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onPrivacyPolicyClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.about_privacy_policy))
        }

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onTermsOfServiceClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.about_terms_of_service))
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.about_open_source_category))

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onViewSourceCodeClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.about_view_source_code_title))
        }

        ListItem(
            secondaryText = {
                Text(text = stringResource(R.string.about_open_source_licenses_summary))
            },
            modifier = Modifier.clickable {
                onItemClick.onOpenSourceLicensesClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.about_open_source_licenses_title))
        }

        PreferenceDivider()

        PreferenceCategoryText(text = stringResource(R.string.about_others_category))

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onFaqClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.navigation_menu_faq_help))
        }

        ListItem(
            modifier = Modifier.clickable {
                onItemClick.onFeedbackClick.invoke()
            }
        ) {
            Text(text = stringResource(R.string.navigation_menu_feedback))
        }

        Spacer(modifier = Modifier.preferredHeight(dimensionResource(R.dimen.fragment_content_padding_half)))
    }
}

@Composable
@Preview(
    name = "AboutScreenContentPreview",
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL_4
)
private fun AboutScreenContentPreview() {
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