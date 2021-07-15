package io.github.tonnyl.moka.widget

import android.webkit.URLUtil
import androidx.compose.foundation.Image
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
import coil.compose.rememberImagePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize

@Composable
fun EmojiComponent(
    emoji: String?,
    getEmojiByName: (String) -> SearchableEmoji?,
    enablePlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val placeholder = @Composable {
            Icon(
                contentDescription = stringResource(id = R.string.emoji_image_placeholder_content_description),
                painter = painterResource(id = R.drawable.ic_emoji_emotions_24),
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .padding(all = ContentPaddingLargeSize)
                    .wrapContentHeight()
                    .placeholder(
                        visible = enablePlaceholder,
                        highlight = PlaceholderHighlight.fade()
                    )
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
                    Image(
                        painter = rememberImagePainter(data = emojiText),
                        contentDescription = stringResource(id = R.string.emoji_image_content_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = ContentPaddingLargeSize)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
                    )
                }
                else -> {
                    Text(
                        text = emojiText,
                        maxLines = 1,
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .padding(all = ContentPaddingLargeSize)
                            .align(alignment = Alignment.Center)
                            .placeholder(
                                visible = enablePlaceholder,
                                highlight = PlaceholderHighlight.fade()
                            )
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
        },
        enablePlaceholder = false
    )
}