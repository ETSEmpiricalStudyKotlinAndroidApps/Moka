package io.github.tonnyl.moka.ui.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.CoilImage
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.data.TrendingDeveloperRepository
import io.github.tonnyl.moka.network.createAvatarLoadRequest
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.widget.Pager
import io.github.tonnyl.moka.widget.PagerState

@Composable
fun TrendingDevelopersScreen(
    pagerState: PagerState = remember { PagerState() },
    developers: List<TrendingDeveloper>
) {
    Pager(
        state = pagerState
    ) {
        LazyColumn {
            items(count = developers.size) { index ->
                TrendingDeveloperItem(
                    index = index,
                    developer = developers[index]
                )
            }
        }
    }
}

@Composable
private fun TrendingDeveloperItem(
    index: Int,
    developer: TrendingDeveloper
) {
    Row(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable {

            }
            .padding(all = ContentPaddingLargeSize)
    ) {
        CoilImage(
            contentDescription = stringResource(id = R.string.users_avatar_content_description),
            request = createAvatarLoadRequest(url = developer.avatar),
            modifier = Modifier
                .size(size = IconSize)
                .clip(shape = CircleShape)
                .clickable(onClick = {})
        )
        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
        Column {
            Row {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                    Text(
                        text = developer.name?.let {
                            stringResource(
                                R.string.explore_trending_developer_name,
                                developer.username,
                                it
                            )
                        } ?: developer.username,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.weight(weight = 1f)
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Box(
                        modifier = Modifier
                            .size(size = 20.dp)
                            .clip(shape = CircleShape)
                            .background(color = MaterialTheme.colors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = MaterialTheme.colors.background
                        )
                    }
                }
            }
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = trendingDeveloperDescContent(
                        developer.repository.name,
                        developer.repository.description
                    )
                )
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
private fun TrendingDeveloperItemPreview() {
    TrendingDeveloperItem(
        index = 0,
        developer = TrendingDeveloper(
            username = "TonnyL",
            name = "Li Zhao Tai Lang",
            type = "user",
            url = "https://github.com/TonnyL",
            avatar = "https://avatars0.githubusercontent.com/u/13329148?s=460&u=5f2267ec07a7e93d6281173e865faeb2363ff658&v=4",
            repository = TrendingDeveloperRepository(
                name = "Moka",
                description = "An Android app for github.com",
                url = "https://github.com/TonnyL/Moka"
            )
        )
    )
}