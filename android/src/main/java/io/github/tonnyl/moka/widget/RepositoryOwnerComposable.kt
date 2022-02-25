package io.github.tonnyl.moka.widget

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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IssueTimelineEventAuthorAvatarSize
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.tonnyl.moka.common.data.ProfileType

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
        AvatarImage(
            url = avatarUrl,
            modifier = Modifier
                .size(size = IssueTimelineEventAuthorAvatarSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    Screen.Profile.navigate(
                        navController = navController,
                        login = login,
                        type = ProfileType.NOT_SPECIFIED
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
                    Screen.Repository.navigate(
                        navController = navController,
                        login = login,
                        repoName = repoName
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