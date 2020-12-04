package io.github.tonnyl.moka.widget

import android.webkit.URLUtil
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.coil.CoilImage
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.SearchableEmoji

@Composable
fun EmojiComponent(
    emoji: String?,
    getEmojiByName: (String) -> SearchableEmoji?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val placeholder = @Composable {
            Icon(
                imageVector = vectorResource(id = R.drawable.ic_emoji_emotions_24),
                modifier = Modifier.align(Alignment.Center)
                    .preferredSize(dimensionResource(id = R.dimen.user_profile_status_card_height))
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
                        data = emojiText,
                        modifier = Modifier.fillMaxSize()
                            .padding(dimensionResource(id = R.dimen.fragment_content_padding))
                    )
                }
                else -> {
                    Text(
                        text = emojiText,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.fragment_content_padding))
                            .align(Alignment.Center),
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