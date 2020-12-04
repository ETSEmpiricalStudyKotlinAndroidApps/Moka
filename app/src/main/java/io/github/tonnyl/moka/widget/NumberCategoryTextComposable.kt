package io.github.tonnyl.moka.widget

import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import io.github.tonnyl.moka.R

@Composable
fun NumberCategoryText(
    number: Int,
    category: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(dimensionResource(id = R.dimen.regular_radius)))
            .clickable(onClick = onClick)
            .padding(vertical = dimensionResource(id = R.dimen.fragment_content_padding))
    ) {
        AndroidView(
            viewBlock = { NumberCategoryTextGroup(it) },
            modifier = Modifier.fillMaxSize()
        ) {
            it.gravity = Gravity.CENTER_HORIZONTAL
            it.setNumberText(number.toString())
            it.setCategory(category)
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