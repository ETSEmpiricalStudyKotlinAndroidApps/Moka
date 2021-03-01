package io.github.tonnyl.moka.widget

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieAnimationSpec
import com.airbnb.lottie.compose.rememberLottieAnimationState
import io.github.tonnyl.moka.R

@Composable
fun LottieLoadingComponent(modifier: Modifier = Modifier) {
    val animationSpec = remember { LottieAnimationSpec.RawRes(R.raw.loading_animation) }
    val animationState = rememberLottieAnimationState(
        autoPlay = true,
        repeatCount = Integer.MAX_VALUE
    )

    LottieAnimation(
        spec = animationSpec,
        animationState = animationState,
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "LottieLoadingComponentPreview")
@Composable
private fun LottieLoadingComponentPreview() {
    LottieLoadingComponent(modifier = Modifier)
}