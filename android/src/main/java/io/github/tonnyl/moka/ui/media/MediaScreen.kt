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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.work.WorkInfo
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ALL
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.MediaType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.max
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@ExperimentalCoilApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
fun MediaScreen(
    activity: MediaActivity,
    url: String,
    filename: String,
    mediaType: MediaType
) {
    val account = LocalAccountInstance.current ?: return
    val displayBarsState = remember { mutableStateOf(true) }

    val app = activity.applicationContext as Application
    val viewModel = viewModel(
        initializer = {
            MediaViewModel(
                extra = MediaViewModelExtra(
                    url = url,
                    filename = filename,
                    accountInstance = account,
                    mediaType = mediaType
                ),
                app = app
            )
        }
    )

    val saveWorkInfo by viewModel.saveWorkerInfo.observeAsState()
    val shareWorkInfo by viewModel.shareWorkerInfo.observeAsState()

    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxSize()
    ) {
        val scaffoldState = rememberScaffoldState()
        val totalSeconds = remember { mutableStateOf(0L) }
        val playerCallback = remember { mutableStateOf<PlayerCallback?>(null) }

        Scaffold(
            scaffoldState = scaffoldState,
            content = {
                Box {
                    when (mediaType) {
                        MediaType.Image -> {
                            ImageScreenContent(
                                url = url,
                                filename = filename,
                                displayBarsState = displayBarsState
                            )
                        }
                        MediaType.Video -> {
                            VideoScreenContent(
                                displayBarsState = displayBarsState,
                                viewModel = viewModel,
                                playerCallbackState = playerCallback,
                                duration = totalSeconds
                            )
                        }
                    }
                }
            },
            bottomBar = {
                when (mediaType) {
                    MediaType.Image -> {
                        ImageContentBottomBar(
                            displayBarsState = displayBarsState,
                            viewModel = viewModel
                        )
                    }
                    MediaType.Video -> {
                        VideoContentBottomBar(
                            displayBarsState = displayBarsState,
                            duration = totalSeconds,
                            playerCallback = playerCallback.value,
                            viewModel = viewModel
                        )
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            }
        )

        when {
            saveWorkInfo?.find { it.state == WorkInfo.State.FAILED } != null -> {
                SnackBarErrorMessage(
                    messageId = R.string.media_save_failed,
                    scaffoldState = scaffoldState,
                    action = null,
                    actionId = null,
                    duration = SnackbarDuration.Short
                )
            }
            saveWorkInfo?.find { it.state == WorkInfo.State.SUCCEEDED } != null -> {
                SnackBarErrorMessage(
                    scaffoldState = scaffoldState,
                    messageId = R.string.media_saved,
                    actionId = null
                )
            }
            mediaType == MediaType.Video
                    && (saveWorkInfo?.find { !it.state.isFinished } != null || shareWorkInfo?.find { !it.state.isFinished } != null) -> {
                SnackBarErrorMessage(
                    scaffoldState = scaffoldState,
                    messageId = R.string.media_downloading,
                    actionId = null,
                    duration = SnackbarDuration.Indefinite
                )
            }
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
                actions = {
                    if (mediaType == MediaType.Video) {
                        ShareAndOpenInBrowserMenu(
                            showMenuState = remember { mutableStateOf(false) },
                            text = "",
                            share = {
                                viewModel.enqueueShareWork()
                            },
                            openInBrowser = {
                                viewModel.enqueueSaveWork()
                            }
                        )
                    }
                },
                backgroundColor = Color.Black.copy(alpha = .2f),
                contentColor = Color.White,
                elevation = 0.dp,
                navigationIcon = {
                    AppBarNavigationIcon(onClick = { activity.finish() })
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

@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
private fun BoxScope.ImageContentBottomBar(
    displayBarsState: MutableState<Boolean>,
    viewModel: MediaViewModel
) {
    AnimatedBottomBar(displayBarsState = displayBarsState) {
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
}

@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
private fun BoxScope.VideoContentBottomBar(
    displayBarsState: MutableState<Boolean>,
    duration: MutableState<Long>,
    playerCallback: PlayerCallback?,
    viewModel: MediaViewModel
) {
    val progress by viewModel.playerProgress.observeAsState(initial = 0L)
    val isReady by viewModel.isReady.observeAsState(initial = false)

    AnimatedBottomBar(displayBarsState = displayBarsState) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(all = ContentPaddingLargeSize)
                .background(color = Color.Black.copy(alpha = .1f))
                .navigationBarsPadding()
        ) {
            Slider(
                valueRange = 0f..(duration.value.toFloat()),
                value = progress.toFloat(),
                onValueChange = {
                    if (isReady) {
                        viewModel.adjustProgress(it.toLong())
                    }
                },
                onValueChangeFinished = {
                    if (isReady) {
                        playerCallback?.seek(progress)
                    }
                },
                modifier = Modifier.clip(shape = MaterialTheme.shapes.medium)
                    .weight(weight = 1f)
            )
            Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
            val durationInMinutes = duration.value / 60
            val totalDuration = "%d:%02d".format(durationInMinutes, duration.value - durationInMinutes * 60)
            val progressInMinutes = progress / 60
            val progressText = "%d:%02d".format(progressInMinutes, progress - progressInMinutes * 60)
            Text(
                text = "$progressText / $totalDuration",
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalSerializationApi
@ExperimentalPagingApi
@Composable
private fun BoxScope.VideoScreenContent(
    displayBarsState: MutableState<Boolean>,
    duration: MutableState<Long>,
    playerCallbackState: MutableState<PlayerCallback?>,
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                repeatMode = REPEAT_MODE_ALL
                addListener(object : Player.Listener {

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)

                        viewModel.updateIsReadyState(
                            if (playbackState == Player.STATE_READY) {
                                duration.value = this@apply.duration / 1000
                                playerCallbackState.value = object : PlayerCallback {
                                    override fun seek(seekTo: Long) {
                                        this@apply.seekTo(seekTo * 1000)
                                    }
                                }

                                true
                            } else {
                                false
                            }
                        )
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)

                        viewModel.playingChanged(
                            isPlaying = isPlaying,
                            player = this@apply
                        )
                    }

                })
            }
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    var playerView by remember { mutableStateOf<StyledPlayerView?>(null) }

    DisposableEffect(key1 = playerView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    exoPlayer.setMediaSource(viewModel.mediaSource)
                    exoPlayer.prepare()
                }
                Lifecycle.Event.ON_RESUME -> {
                    exoPlayer.play()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    exoPlayer.pause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    exoPlayer.release()
                }
                else -> {

                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    AndroidView(
        factory = {
            StyledPlayerView(context).apply {
                player = exoPlayer
                useController = false
                keepScreenOn = true
                resizeMode = RESIZE_MODE_FIT
            }.also {
                playerView = it
            }
        },
        modifier = modifier.fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                displayBarsState.value = !displayBarsState.value
            }
    )

    val isPlaying by viewModel.playState.observeAsState()
    val isReady by viewModel.isReady.observeAsState()

    if (isReady == true) {
        AnimatedVisibility(
            visible = displayBarsState.value,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = modifier
                .scale(scale = 1.2f)
                .align(alignment = Alignment.Center)
        ) {
            IconButton(
                onClick = {
                    if (isPlaying == true) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.play()
                    }
                },
                modifier = Modifier.clip(shape = CircleShape)
                    .background(color = Color.Black.copy(alpha = .2f))
            ) {
                if (isPlaying == true) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause_24),
                        contentDescription = stringResource(id = R.string.media_pause_image_desc)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = stringResource(id = R.string.media_play_image_desc)
                    )
                }
            }
        }
    } else {
        CircularProgressIndicator(
            color = MaterialTheme.colors.primary,
            modifier = modifier.align(alignment = Alignment.Center)
        )
    }

}

@Composable
private fun BoxScope.AnimatedBottomBar(
    displayBarsState: MutableState<Boolean>,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = displayBarsState.value,
        enter = slideInVertically(
            initialOffsetY = { it / 2 }
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it / 2 }
        ) + fadeOut(),
        content = content,
        modifier = Modifier
            .fillMaxWidth()
            .align(alignment = Alignment.BottomCenter)
    )
}

@ExperimentalCoilApi
@ExperimentalAnimationApi
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
        activity = MediaActivity(),
        mediaType = MediaType.Image
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