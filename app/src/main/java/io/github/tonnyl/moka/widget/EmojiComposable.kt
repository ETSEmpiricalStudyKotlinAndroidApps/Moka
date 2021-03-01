package io.github.tonnyl.moka.widget

import android.webkit.URLUtil
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.coil.CoilImage
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize

@Composable
fun EmojiComponent(
    emoji: String?,
    getEmojiByName: (String) -> SearchableEmoji?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val placeholder = @Composable {
            Icon(
                contentDescription = stringResource(id = R.string.emoji_image_placeholder_content_description),
                painter = painterResource(id = R.drawable.ic_emoji_emotions_24),
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .wrapContentHeight()
                    .padding(all = ContentPaddingMediumSize)
            )
        }

        if (emoji.isNullOrEmpty()) {
            placeholder.invoke()
        } else {
            val emojiText = getEmojiByName(emoji)?.emoji
            when {
                emojiText.isNullOrEmpty() -> {
                    placeholder()
                }
                URLUtil.isValidUrl(emojiText) -> {
                    CoilImage(
                        contentDescription = stringResource(id = R.string.emoji_image_content_description),
                        data = emojiText,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = ContentPaddingLargeSize)
                    )
                }
                else -> {
                    Text(
                        text = emojiText,
                        modifier = Modifier
                            .padding(all = ContentPaddingLargeSize)
                            .align(alignment = Alignment.Center),
                        maxLines = 1,
                        style = MaterialTheme.typography.h6
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "EmojiContentPreview")
@Composable
private fun EmojiContentPreview() {
    EmojiComponent(
        emoji = null,
        getEmojiByName = {
            SearchableEmoji(
                emoji = "🎯",
                name = ":dart:",
                category = "Activities"
            )
        }
    )
}