package io.github.tonnyl.moka.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

private val ChipHeight = 32.dp
private val HorizontalPadding = 12.dp

private const val ChipBackgroundAlpha = .12f

private val ChipShape = RoundedCornerShape(percent = 50)

@Composable
fun Chip(
    text: String,
    enablePlaceholder: Boolean,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(height = ChipHeight)
            .clip(shape = ChipShape)
            .background(color = MaterialTheme.colors.primary.copy(alpha = ChipBackgroundAlpha))
            .placeholder(
                visible = enablePlaceholder,
                highlight = PlaceholderHighlight.fade(),
                color = MaterialTheme.colors.primary.copy(alpha = ChipBackgroundAlpha)
            )
            .clickable(enabled = !enablePlaceholder) {
                onClick?.invoke()
            }
            .padding(horizontal = HorizontalPadding)
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.primary
        )
    }
}

@Composable
fun OutlineChip(
    text: String,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(height = ChipHeight)
            .clip(shape = ChipShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface.copy(alpha = ChipBackgroundAlpha),
                shape = ChipShape
            )
            .clickable {
                onClick?.invoke()
            }
            .padding(horizontal = HorizontalPadding)
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface
        )
    }
}

@Preview(showBackground = true, name = "ChipPreview")
@Composable
private fun ChipPreview() {
    Chip(
        text = "Chip",
        enablePlaceholder = false
    )
}

@Preview(showBackground = true, name = "OutlineChipPreview")
@Composable
private fun OutlineChipPreview() {
    OutlineChip(text = "OutlineChip")
}