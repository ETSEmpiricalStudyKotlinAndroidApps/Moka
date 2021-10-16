package io.github.tonnyl.moka.ui.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.TrendingRepositoryProvider
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.toColor
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Composable
fun TrendingRepositoryItem(
    repository: TrendingRepository,
    timeSpanText: String,
    enablePlaceholder: Boolean
) {
    val navController = LocalNavController.current
    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(enabled = !enablePlaceholder) {
                navController.navigate(
                    route = Screen.Repository.route
                        .replace("{${Screen.ARG_PROFILE_LOGIN}}", repository.author)
                        .replace("{${Screen.ARG_REPOSITORY_NAME}}", repository.name)
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberImagePainter(
                data = repository.avatar,
                builder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.repository_owners_avatar_image_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    navController.navigate(
                        route = Screen.Profile.route
                            .replace("{${Screen.ARG_PROFILE_LOGIN}}", repository.author)
                            .replace("{${Screen.ARG_PROFILE_TYPE}}", ProfileType.NOT_SPECIFIED.name)
                    )
                }
                .placeholder(
                    visible = enablePlaceholder,
                    highlight = PlaceholderHighlight.fade()
                )
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            Row {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = stringResource(
                            R.string.repository_name_with_username,
                            repository.author,
                            repository.name
                        ),
                        style = MaterialTheme.typography.subtitle1,
                        color = MaterialTheme.colors.primary,
                        modifier = Modifier
                            .weight(weight = 1f)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    )
                }
            }
            if (enablePlaceholder) {
                Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
            }
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = repository.description
                            ?: stringResource(id = R.string.no_description_provided),
                        maxLines = 6,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                    if (enablePlaceholder) {
                        Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                    }
                    Text(
                        text = stringResource(
                            R.string.explore_period_stars,
                            repository.currentPeriodStars,
                            timeSpanText
                        ),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                    )
                    if (enablePlaceholder) {
                        Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(size = 12.dp)
                                .clip(shape = CircleShape)
                                .background(
                                    color = repository.languageColor
                                        ?.toColor()
                                        ?.let {
                                            Color(it)
                                        } ?: MaterialTheme.colors.onBackground
                                )
                                .placeholder(
                                    visible = enablePlaceholder,
                                    highlight = PlaceholderHighlight.fade()
                                )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Text(
                            text = repository.language
                                ?: stringResource(id = R.string.programming_language_unknown),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                        Image(
                            contentDescription = stringResource(id = R.string.repository_stargazers),
                            painter = painterResource(id = R.drawable.ic_star_secondary_text_color_18),
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Text(
                            text = repository.stars.formatWithSuffix(),
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                        Image(
                            contentDescription = stringResource(id = R.string.repository_forks),
                            painter = painterResource(id = R.drawable.ic_code_fork_secondary_text_color_18),
                            modifier = Modifier.placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Text(
                            text = repository.forks.formatWithSuffix(),
                            style = MaterialTheme.typography.body2,
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
}

@ExperimentalCoilApi
@ExperimentalSerializationApi
@Preview(
    name = "TrendingRepositoriesScreenPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun TrendingRepositoriesScreenPreview(
    @PreviewParameter(
        provider = TrendingRepositoryProvider::class,
        limit = 1
    )
    repository: TrendingRepository
) {
    TrendingRepositoryItem(
        timeSpanText = "daily",
        repository = repository,
        enablePlaceholder = false
    )
}