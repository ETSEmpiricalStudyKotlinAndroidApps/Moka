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
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.*
import io.github.tonnyl.moka.util.toColor
import io.github.tonnyl.moka.widget.AvatarImage
import io.tonnyl.moka.common.data.ProfileType
import io.tonnyl.moka.common.db.data.TrendingRepository
import io.tonnyl.moka.common.util.TrendingRepositoryProvider
import io.tonnyl.moka.common.util.formatWithSuffix

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
                Screen.Repository.navigate(
                    navController = navController,
                    login = repository.author,
                    repoName = repository.name
                )
            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        AvatarImage(
            url = repository.avatar,
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable(enabled = !enablePlaceholder) {
                    Screen.Profile.navigate(
                        navController = navController,
                        login = repository.author,
                        type = ProfileType.NOT_SPECIFIED
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