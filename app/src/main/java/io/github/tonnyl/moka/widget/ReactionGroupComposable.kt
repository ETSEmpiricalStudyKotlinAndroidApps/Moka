package io.github.tonnyl.moka.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.fragment.ReactionGroup
import io.github.tonnyl.moka.queries.IssueQuery
import io.github.tonnyl.moka.type.ReactionContent
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingSmallSize

@Composable
fun ReactionGroupComponent(
    groups: List<ReactionGroup>,
    tailingReactButton: Boolean,
    viewerCanReact: Boolean,
    react: (ReactionContent?) -> Unit,
    enablePlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    if (groups.isNotEmpty()) {
        FlowRow(
            crossAxisAlignment = FlowCrossAxisAlignment.Center,
            crossAxisSpacing = ContentPaddingSmallSize,
            modifier = modifier
        ) {
            groups.filter { group ->
                group.reactors.totalCount != 0
            }.forEach { group ->
                if (group.reactors.totalCount != 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(percent = 50))
                            .clickable(enabled = !enablePlaceholder && viewerCanReact) {
                                react.invoke(group.content)
                            }
                            .background(
                                color = if (group.viewerHasReacted) {
                                    MaterialTheme.colors.primary
                                } else {
                                    MaterialTheme.colors.onSurface
                                }.copy(alpha = .12f)
                            )
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                            .padding(
                                horizontal = ContentPaddingMediumSize,
                                vertical = ContentPaddingSmallSize
                            )
                    ) {
                        Text(
                            text = stringResource(
                                when (group.content) {
                                    ReactionContent.CONFUSED -> {
                                        R.string.emoji_confused
                                    }
                                    ReactionContent.EYES -> {
                                        R.string.emoji_eyes
                                    }
                                    ReactionContent.HEART -> {
                                        R.string.emoji_heart
                                    }
                                    ReactionContent.HOORAY -> {
                                        R.string.emoji_hooray
                                    }
                                    ReactionContent.LAUGH -> {
                                        R.string.emoji_laugh
                                    }
                                    ReactionContent.ROCKET -> {
                                        R.string.emoji_rocket
                                    }
                                    ReactionContent.THUMBS_DOWN -> {
                                        R.string.emoji_thumbs_down
                                    }
                                    ReactionContent.THUMBS_UP -> {
                                        R.string.emoji_thumbs_up
                                    }
                                    is ReactionContent.UNKNOWN__ -> {
                                        R.string.emoji_unknown
                                    }
                                }
                            ),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier.alpha(
                                alpha = if (enablePlaceholder) {
                                    0f
                                } else {
                                    1f
                                }
                            )
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                        Text(
                            text = group.reactors.totalCount.toString(),
                            style = MaterialTheme.typography.body2
                        )
                    }
                    Spacer(modifier = Modifier.width(width = ContentPaddingMediumSize))
                }
            }
            if (tailingReactButton) {
                Icon(
                    contentDescription = stringResource(id = R.string.add_reaction_image_content_description),
                    painter = painterResource(id = R.drawable.ic_emoji_emotions_24),
                    modifier = Modifier
                        .clip(shape = CircleShape)
                        .clickable(enabled = !enablePlaceholder && viewerCanReact) {
                            react.invoke(null)
                        }
                        .placeholder(
                            visible = enablePlaceholder,
                            highlight = PlaceholderHighlight.fade()
                        )
                )
            }
        }
    }
}

@Preview(
    name = "ReactionGroupPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun ReactionGroupPreview() {
    ReactionGroupComponent(
        groups = listOf(
            IssueQuery.Data.Repository.Issue.ReactionGroup(
                __typename = "",
                content = ReactionContent.HEART,
                viewerHasReacted = true,
                reactors = IssueQuery.Data.Repository.Issue.ReactionGroup.Reactors(totalCount = 3)
            ),
            IssueQuery.Data.Repository.Issue.ReactionGroup(
                __typename = "",
                content = ReactionContent.THUMBS_UP,
                viewerHasReacted = false,
                reactors = IssueQuery.Data.Repository.Issue.ReactionGroup.Reactors(totalCount = 5)
            )
        ),
        enablePlaceholder = false,
        viewerCanReact = true,
        tailingReactButton = true,
        react = {}
    )
}