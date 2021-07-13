package io.github.tonnyl.moka.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.tonnyl.moka.R

@Composable
fun LottieLoadingComponent(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.loading_animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = true,
        iterations = Integer.MAX_VALUE
    )

    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "LottieLoadingComponentPreview")
@Composable
private fun LottieLoadingComponentPreview() {
    LottieLoadingComponent(modifier = Modifier)
}