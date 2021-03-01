package io.github.tonnyl.moka.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize

@Composable
fun NumberCategoryText(
    number: Int,
    category: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .padding(vertical = ContentPaddingLargeSize)
    ) {
        Text(
            text = number.toString(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(height = ContentPaddingMediumSize))
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = category,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Preview(showBackground = true, name = "NumberCategoryTextPreview")
@Composable
private fun NumberCategoryTextPreview() {
    NumberCategoryText(
        number = 11,
        category = stringResource(id = R.string.repository_projects),
        onClick = {}
    )
}