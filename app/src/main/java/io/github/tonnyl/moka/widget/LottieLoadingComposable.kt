package io.github.tonnyl.moka.widget

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R

@Composable
fun LottieLoadingComponent(modifier: Modifier = Modifier) {
    AndroidView(
        viewBlock = { context ->
            LayoutInflater.from(context)
                .inflate(R.layout.view_lottie_loading, FrameLayout(context), false)
        },
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "LottieLoadingComponentPreview")
@Composable
private fun LottieLoadingComponentPreview() {
    LottieLoadingComponent(modifier = Modifier)
}