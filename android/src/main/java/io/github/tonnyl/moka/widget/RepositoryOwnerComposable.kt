package io.github.tonnyl.moka.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.tonnyl.moka.graphql.fragment.RepositoryOwner

@ExperimentalCoilApi
@Composable
fun RepositoryOwner(
    avatarUrl: String?,
    login: String,
    repoName: String,
    enablePlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberImagePainter(
                data = avatarUrl,
                builder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.repository_owners_avatar_image_content_description),
            modifier = Modifier
                .size(size = IssueTimelineEventAuthorAvatarSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    navController.navigate(
                        route = Screen.Profile.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                            .replace(
                                "{${Screen.ARG_PROFILE_TYPE}}",
                                ProfileType.NOT_SPECIFIED.name
                            )
                    )
                }
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Text(
            text = stringResource(
                id = R.string.repository_name_with_username,
                login,
                repoName
            ),
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.primary,
            modifier = Modifier
                .clickable(enabled = !enablePlaceholder) {
                    navController.navigate(
                        route = Screen.Repository.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", login)
                            .replace("{${Screen.ARG_REPOSITORY_NAME}}", repoName)
                    )
                }
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade(),
                    color = MaterialTheme.colors.primary.copy(alpha = .3f)
                )
        )
    }
}

@ExperimentalCoilApi
@Preview(
    name = "RepositoryOwnerPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun RepositoryOwnerPreview() {
    RepositoryOwner(
        avatarUrl = null,
        login = "TonnyL",
        repoName = "PaperPlane",
        enablePlaceholder = false
    )
}