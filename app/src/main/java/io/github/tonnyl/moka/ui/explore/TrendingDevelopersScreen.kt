package io.github.tonnyl.moka.ui.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.DividerSize
import io.github.tonnyl.moka.util.TrendingDeveloperProvider

@Composable
fun TrendingDeveloperItem(
    index: Int,
    developer: TrendingDeveloper,
    enablePlaceholder: Boolean
) {
    Card(
        border = BorderStroke(
            width = DividerSize,
            color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
        ),
        elevation = 0.dp,
        modifier = Modifier
            .padding(
                start = if (index != 0) {
                    ContentPaddingLargeSize
                } else {
                    0.dp
                }
            )
            .clickable {

            }
            .width(width = 180.dp)
            .height(height = 240.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(all = ContentPaddingLargeSize)
        ) {
            Image(
                painter = rememberImagePainter(
                    data = developer.avatar,
                    builder = {
                        createAvatarLoadRequest()
                    }
                ),
                contentDescription = stringResource(id = R.string.users_avatar_content_description),
                modifier = Modifier
                    .size(size = 80.dp)
                    .clip(shape = CircleShape)
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
            )
            Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
            Text(
                text = stringResource(
                    id = R.string.user_profile_pinned_item_caption_format,
                    developer.name.takeIf { !it.isNullOrEmpty() } ?: developer.username
                ),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
            )
            if (enablePlaceholder) {
                Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(
                        id = R.string.user_profile_pinned_item_caption_format,
                        developer.username
                    ),
                    style = MaterialTheme.typography.body1,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    modifier = Modifier.placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
                )
            }
            if (developer.repository != null) {
                Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = trendingDeveloperDescContent(
                            developer.repository.name,
                            developer.repository.description
                        ),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.caption,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun trendingDeveloperDescContent(
    repositoryName: String,
    desc: String?
): AnnotatedString {
    return buildAnnotatedString {
        append(
            AnnotatedString(
                text = repositoryName,
                spanStyle = SpanStyle(
                    color = MaterialTheme.colors.onBackground
                )
            )
        )
        if (!desc.isNullOrEmpty()) {
            append(
                stringResource(
                    id = R.string.explore_trending_developer_repository_description,
                    desc
                )
            )
        }
    }
}

@Preview(
    name = "TrendingDeveloperItemPreview",
    showBackground = true
)
@Composable
private fun TrendingDeveloperItemPreview(
    @PreviewParameter(
        provider = TrendingDeveloperProvider::class,
        limit = 1
    )
    developer: TrendingDeveloper
) {
    TrendingDeveloperItem(
        index = 0,
        developer = developer,
        enablePlaceholder = false
    )
}