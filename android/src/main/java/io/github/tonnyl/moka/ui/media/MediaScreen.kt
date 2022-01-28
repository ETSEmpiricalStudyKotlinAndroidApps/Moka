package io.github.tonnyl.moka.ui.media

import android.app.Application
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.paging.ExperimentalPagingApi
import androidx.work.WorkInfo
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.ViewModelFactory
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.viewModel
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.max
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
fun MediaScreen(
    activity: MediaActivity,
    url: String,
    filename: String
) {
    val account = LocalAccountInstance.current ?: return
    val displayBarsState = remember { mutableStateOf(true) }

    val viewModel = viewModel<MediaViewModel>(
        factory = ViewModelFactory(),
        defaultCreationExtras = MutableCreationExtras().apply {
            this[APPLICATION_KEY] = activity.applicationContext as Application
            this[MediaViewModel.MEDIA_VIEW_MODEL_EXTRA_KEY] = MediaViewModelExtra(
                url = url,
                filename = filename,
                accountInstance = account
            )
        }
    )

    val saveWork by viewModel.saveWorkerLiveData.observeAsState()

    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            content = {
                Box {
                    ImageScreenContent(
                        url = url,
                        filename = filename,
                        displayBarsState = displayBarsState
                    )
                }
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = displayBarsState.value,
                    enter = slideInVertically(
                        initialOffsetY = { it / 2 }
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        targetOffsetY = { it / 2 }
                    ) + fadeOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.BottomCenter)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .background(color = Color.Black.copy(alpha = .1f))
                            .padding(vertical = ContentPaddingLargeSize)
                            .navigationBarsPadding()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(weight = 1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {

                                }
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.enqueueSaveWork()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_save_24),
                                    contentDescription = stringResource(id = R.string.media_save),
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.media_save),
                                style = MaterialTheme.typography.button,
                                color = Color.White
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(weight = 1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {

                                }
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.enqueueShareWork()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = stringResource(id = R.string.share),
                                    tint = Color.White
                                )
                            }
                            Text(
                                text = stringResource(id = R.string.share),
                                style = MaterialTheme.typography.button,
                                color = Color.White
                            )
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    Snackbar(snackbarData = data)
                }
            }
        )

        if (saveWork?.find { it.state == WorkInfo.State.FAILED } != null) {
            SnackBarErrorMessage(
                messageId = R.string.media_save_failed,
                scaffoldState = scaffoldState,
                action = null,
                actionId = null,
                duration = SnackbarDuration.Short
            )
        }

        AnimatedVisibility(
            visible = displayBarsState.value,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut(),
            modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.TopCenter)
        ) {
            InsetAwareTopAppBar(
                title = {
                    Text(
                        text = filename,
                        color = MaterialTheme.colors.onBackground
                    )
                },
                backgroundColor = Color.Black.copy(alpha = .2f),
                contentColor = Color.White,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            activity.finish()
                        },
                        content = {
                            Icon(
                                contentDescription = stringResource(id = R.string.navigate_up),
                                imageVector = Icons.Outlined.ArrowBack
                            )
                        }
                    )
                }
            )
        }
    }
}

@ExperimentalCoilApi
@Composable
private fun BoxScope.ImageScreenContent(
    url: String,
    filename: String,
    displayBarsState: MutableState<Boolean>,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableStateOf(MIN_SCALE) }
    val scaleAnimatedValue by animateFloatAsState(targetValue = scale, visibilityThreshold = 0.0001f)
    var originalImageHeight by remember { mutableStateOf(0f) } // px
    var originalImageWidth by remember { mutableStateOf(0f) } // px
    var screenSize by remember { mutableStateOf(IntSize.Zero) } // equals to the screen size

    var offsetX by remember { mutableStateOf(0f) }
    val offsetXAnimatedValue by animateFloatAsState(targetValue = offsetX, visibilityThreshold = 10f)
    var offsetY by remember { mutableStateOf(0f) }
    val offsetYAnimatedValue by animateFloatAsState(targetValue = offsetY, visibilityThreshold = 10f)

    var isLoading by remember { mutableStateOf(true) }

    Image(
        painter = rememberImagePainter(
            data = url,
            builder = {
                listener(
                    onSuccess = { _, _ ->
                        isLoading = false
                    },
                    onError = { _, _ ->
                        isLoading = false
                    }
                )
            },
            onExecute = { _, current ->
                originalImageHeight = current.size.height
                originalImageWidth = current.size.width

                true
            }
        ),
        contentDescription = filename,
        modifier = modifier
            .fillMaxSize()
            .offset {
                IntOffset(
                    x = offsetXAnimatedValue.roundToInt(),
                    y = offsetYAnimatedValue.roundToInt()
                )
            }
            .scale(scale = scaleAnimatedValue)
            .onSizeChanged {
                screenSize = it
            }
            .transformable(
                state = rememberTransformableState { zoomChange, panChange, _ ->
                    scale = (scale * zoomChange).scaleLimited
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (originalImageWidth == 0f
                            || originalImageHeight == 0f
                        ) {
                            return@detectTapGestures
                        }

                        offsetX = 0f
                        offsetY = 0f

                        scale = when {
                            scale != 1f -> {
                                1f
                            }
                            originalImageWidth == screenSize.width.toFloat()
                                    && originalImageHeight == screenSize.height.toFloat() -> {
                                1.2f
                            }
                            else -> {
                                max(
                                    screenSize.width / originalImageWidth,
                                    screenSize.height / originalImageHeight
                                ).scaleLimited
                            }
                        }
                    }
                ) {
                    displayBarsState.value = !displayBarsState.value
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val maxXOffset = (originalImageWidth * scale - screenSize.width) / 2
                    val minXOffset = -maxXOffset

                    val maxYOffset = (originalImageHeight * scale - screenSize.height) / 2
                    val minYOffset = -maxYOffset

                    if (scale != 1f) {
                        when {
                            offsetX + dragAmount.x < minXOffset -> {
                                offsetX = minXOffset
                            }
                            offsetX + dragAmount.x > maxXOffset -> {
                                offsetX = maxXOffset
                            }
                            else -> {
                                offsetX += dragAmount.x
                            }
                        }

                        when {
                            offsetY + dragAmount.y < minYOffset -> {
                                offsetY = minYOffset
                            }
                            offsetY + dragAmount.y > maxYOffset -> {
                                offsetY = maxYOffset
                            }
                            else -> {
                                offsetY += dragAmount.y
                            }
                        }
                    } else {
                        offsetX = 0f
                        offsetY = 0f
                    }
                }
            }
    )

    if (isLoading) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary,
            modifier = modifier.align(alignment = Alignment.Center)
        )
    }
}

@Composable
private fun VideoScreenContent() {

}

@ExperimentalMaterialApi
@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@Preview(
    name = "MediaScreenPreview",
    backgroundColor = 0x000000,
    showSystemUi = true,
    showBackground = true,
    device = Devices.PIXEL_4
)
@Composable
private fun MediaScreenPreview() {
    MediaScreen(
        url = "https://github.com/TonnyL/PaperPlane/blob/master/art/details.png?raw=true",
        filename = "details.png",
        activity = MediaActivity()
    )
}

private const val MAX_SCALE = 3f
private const val MIN_SCALE = 1f

private val Float.scaleLimited: Float
    get() = when {
        this < MIN_SCALE -> {
            MIN_SCALE
        }
        this > MAX_SCALE -> {
            MAX_SCALE
        }
        else -> {
            this
        }
    }