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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingRepository
import io.github.tonnyl.moka.data.TrendingRepositoryBuiltBy
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.util.formatWithSuffix
import io.github.tonnyl.moka.util.toColor

@Composable
fun TrendingRepositoryItem(repository: TrendingRepository) {
    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {

            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        Image(
            painter = rememberCoilPainter(
                request = repository.avatar,
                requestBuilder = {
                    createAvatarLoadRequest()
                }
            ),
            contentDescription = stringResource(id = R.string.repository_owners_avatar_image_content_description),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable {

                }
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
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
            }
            Column {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = repository.description
                            ?: stringResource(id = R.string.no_description_provided),
                        style = MaterialTheme.typography.body2
                    )
                    Text(
                        text = stringResource(
                            R.string.explore_period_stars,
                            repository.currentPeriodStars,
                            "today"
                        ),
                        style = MaterialTheme.typography.body2
                    )
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
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Text(
                            text = repository.language
                                ?: stringResource(id = R.string.programming_language_unknown),
                            style = MaterialTheme.typography.body2
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                        Image(
                            contentDescription = stringResource(id = R.string.repository_stargazers),
                            painter = painterResource(id = R.drawable.ic_star_secondary_text_color_18)
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Text(
                            text = repository.stars.formatWithSuffix(),
                            style = MaterialTheme.typography.body2
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                        Image(
                            contentDescription = stringResource(id = R.string.repository_forks),
                            painter = painterResource(id = R.drawable.ic_code_fork_secondary_text_color_18)
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingSmallSize))
                        Text(
                            text = repository.forks.formatWithSuffix(),
                            style = MaterialTheme.typography.body2
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    name = "TrendingRepositoriesScreenPreview",
    showBackground = true
)
@Composable
private fun TrendingRepositoriesScreenPreview() {
    TrendingRepositoryItem(
        repository = TrendingRepository(
            author = "TonnyL",
            name = "PaperPlane",
            avatar = "https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4",
            url = "https://github.com/TonnyL/PaperPlane",
            description = "📚 PaperPlane - An Android reading app, including articles from Zhihu Daily, Guokr Handpick and Douban Moment. ",
            language = "Kotlin",
            languageColor = "#F18E33",
            stars = 1065,
            forks = 296,
            currentPeriodStars = 20,
            builtBy = listOf(
                TrendingRepositoryBuiltBy(
                    href = "",
                    avatar = "https://avatars1.githubusercontent.com/u/13329148?u=0a5724e7c9f7d1cc4a5486ab7ab07abb7b8d7956&v=4",
                    username = "TonnyL"
                )
            )
        )
    )
}