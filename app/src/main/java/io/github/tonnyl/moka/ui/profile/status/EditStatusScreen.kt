package io.github.tonnyl.moka.ui.profile.status

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.compose.navigate
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.toPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.DividerSize
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.EmojiComponent
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.LottieLoadingComponent
import io.github.tonnyl.moka.widget.SnackBarErrorMessage

@ExperimentalMaterialApi
@Composable
fun EditStatusScreen(
    initialEmoji: String?,
    initialMessage: String?,
    initialIndicatesLimitedAvailability: Boolean?,
    viewModel: EditStatusViewModel
) {
    val scaffoldState = rememberScaffoldState()

    val clearStatus by viewModel.clearStatusState.observeAsState(null)
    val setStatus by viewModel.updateStatusState.observeAsState(null)

    val expireAt by viewModel.expiresAt.observeAsState()
    val emoji by viewModel.emojiName.observeAsState()
    val message by viewModel.message.observeAsState()
    val dnd by viewModel.limitedAvailability.observeAsState()

    val navController = LocalNavController.current
    if (clearStatus?.status == Status.SUCCESS) {
        navController.previousBackStackEntry?.savedStateHandle
            ?.set(Screen.EditStatus.RESULT_UPDATE_STATUS, clearStatus?.data)
        navController.navigateUp()

        return
    }
    if (setStatus?.status == Status.SUCCESS) {
        navController.previousBackStackEntry?.savedStateHandle
            ?.set(Screen.EditStatus.RESULT_UPDATE_STATUS, setStatus?.data)
        navController.navigateUp()

        return
    }

    Box(
        modifier = Modifier
            .navigationBarsPadding()
    ) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val mainViewModel = LocalMainViewModel.current

        Scaffold(
            content = {
                EditStatusScreenContent(
                    topAppBarSize = topAppBarSize,
                    scaffoldState = scaffoldState,
                    clearStatusStatus = clearStatus,
                    setStatusStatus = setStatus,

                    emojiState = emoji,
                    updateEmoji = { viewModel.updateEmoji(it) },

                    messageState = message,
                    updateMessage = { viewModel.updateMessage(it) },

                    expireAtState = expireAt,
                    updateExpireAt = { viewModel.updateExpireAt(it) },

                    dndState = dnd,
                    updateDnd = { viewModel.updateLimitedAvailability(it) },

                    getEmojiByName = { mainViewModel.getEmojiByName(it) },
                    initialEmoji = initialEmoji,
                    initialMessage = initialMessage,
                    initialIndicatesLimitedAvailability = initialIndicatesLimitedAvailability,
                    showEmojis = { navController.navigate(route = Screen.Emojis.route) },
                    updateStatus = viewModel::updateStatus,
                    clearStatus = viewModel::clearStatus
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    Snackbar(snackbarData = data)
                }
            },
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.edit_status)) },
            navigationIcon = {
                IconButton(
                    onClick = { navController.navigateUp() },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_close_24)
                        )
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun EditStatusScreenContent(
    topAppBarSize: Int,
    scaffoldState: ScaffoldState,

    clearStatusStatus: Resource<UserStatus?>?, // 🤔
    setStatusStatus: Resource<UserStatus?>?, // 🙃

    emojiState: String?,
    updateEmoji: (String) -> Unit,

    messageState: String?,
    updateMessage: (String) -> Unit,

    expireAtState: ExpireAt?,
    updateExpireAt: (ExpireAt) -> Unit,

    dndState: Boolean?,
    updateDnd: (Boolean) -> Unit,

    getEmojiByName: (String) -> SearchableEmoji?,
    initialEmoji: String?,
    initialMessage: String?,
    initialIndicatesLimitedAvailability: Boolean?,
    showEmojis: () -> Unit,
    updateStatus: () -> Unit,
    clearStatus: () -> Unit
) {
    val slowToResponse = stringResource(id = R.string.edit_status_busy_message)

    var couldDisplayErrorMessage by remember { mutableStateOf(false) }

    Column {
        LazyColumn(
            contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
                top = false,
                additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
            ),
            modifier = Modifier
                .weight(weight = 1f)
                .padding(vertical = ContentPaddingLargeSize)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ContentPaddingLargeSize)
                ) {
                    EmojiComponent(
                        emoji = emojiState,
                        getEmojiByName = getEmojiByName,
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.medium)
                            .align(alignment = Alignment.CenterVertically)
                            .clickable(onClick = showEmojis)
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    // todo Make text field single-lined.
                    OutlinedTextField(
                        value = messageState ?: "",
                        onValueChange = {
                            updateMessage.invoke(it.trim())
                        },
                        label = {
                            Text(text = stringResource(id = R.string.edit_status_hint_whats_happening))
                        },
                        modifier = Modifier
                            .weight(weight = 1f)
                            .align(alignment = Alignment.CenterVertically)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
            }
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = R.string.edit_status_suggestions),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ContentPaddingLargeSize)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
            }
            item {
                val (onVacationEmoji, onVacationMessage) = Pair(
                    ":palm_tree:",
                    stringResource(id = R.string.edit_status_suggestion_on_vacation_message)
                )
                val (workingRemotelyEmoji, workingRemoteMessage) = Pair(
                    ":house:",
                    stringResource(id = R.string.edit_status_suggestion_working_remotely_message)
                )
                EmojiSuggestionRow(
                    emojiStart = R.string.edit_status_suggestion_on_vacation_emoji,
                    messageStart = R.string.edit_status_suggestion_on_vacation_message,
                    onStartItemClicked = {
                        updateEmoji.invoke(onVacationEmoji)
                        updateMessage.invoke(onVacationMessage)
                    },
                    emojiEnd = R.string.edit_status_suggestion_working_remotely_emoji,
                    messageEnd = R.string.edit_status_suggestion_working_remotely_message,
                    onEndItemClicked = {
                        updateEmoji.invoke(workingRemotelyEmoji)
                        updateMessage.invoke(workingRemoteMessage)
                    }
                )
            }
            item {
                val (outSickEmoji, outSickMessage) = Pair(
                    ":face_with_thermometer:",
                    stringResource(id = R.string.edit_status_suggestion_out_sick_message)
                )
                val (commutingEmoji, commutingMessage) = Pair(
                    ":bus:",
                    stringResource(id = R.string.edit_status_suggestion_commuting_message)
                )
                EmojiSuggestionRow(
                    emojiStart = R.string.edit_status_suggestion_out_sick_emoji,
                    messageStart = R.string.edit_status_suggestion_out_sick_message,
                    onStartItemClicked = {
                        updateEmoji.invoke(outSickEmoji)
                        updateMessage.invoke(outSickMessage)
                    },
                    emojiEnd = R.string.edit_status_suggestion_commuting_emoji,
                    messageEnd = R.string.edit_status_suggestion_commuting_message,
                    onEndItemClicked = {
                        updateEmoji.invoke(commutingEmoji)
                        updateMessage.invoke(commutingMessage)
                    }
                )
            }
            item {
                val (inAMeetingEmoji, inAMeetingMessage) = Pair(
                    ":date:",
                    stringResource(id = R.string.edit_status_suggestion_in_a_meeting_message)
                )
                val (focusingEmoji, focusingMessage) = Pair(
                    ":dart:",
                    stringResource(id = R.string.edit_status_suggestion_focusing_message)
                )
                EmojiSuggestionRow(
                    emojiStart = R.string.edit_status_suggestion_in_a_meeting_emoji,
                    messageStart = R.string.edit_status_suggestion_in_a_meeting_message,
                    onStartItemClicked = {
                        updateEmoji.invoke(inAMeetingEmoji)
                        updateMessage.invoke(inAMeetingMessage)
                    },
                    emojiEnd = R.string.edit_status_suggestion_focusing_emoji,
                    messageEnd = R.string.edit_status_suggestion_focusing_message,
                    onEndItemClicked = {
                        updateEmoji.invoke(focusingEmoji)
                        updateMessage.invoke(focusingMessage)
                    }
                )
            }
            item {
                Row {
                    val updateUI = {
                        if (dndState == true) {
                            if (messageState.isNullOrEmpty()) {
                                updateMessage.invoke(slowToResponse)
                            }
                        } else if (messageState == slowToResponse) {
                            updateMessage.invoke("")
                        }
                    }
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Checkbox(
                        checked = dndState ?: false,
                        onCheckedChange = {
                            updateDnd.invoke(it)
                            updateUI.invoke()
                        },
                        modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    )
                    Text(
                        text = stringResource(id = R.string.edit_status_do_not_disturb),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .clip(shape = MaterialTheme.shapes.medium)
                            .clickable {
                                updateDnd.invoke(!(dndState ?: false))
                                updateUI.invoke()
                            }
                            .padding(all = ContentPaddingLargeSize)
                    )
                }
            }
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = R.string.edit_status_busy_info),
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
            }
            item {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = R.string.edit_status_clear_status),
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = ContentPaddingLargeSize)
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(height = ContentPaddingLargeSize))
            }
            item {
                val expandedState = mutableStateOf(false)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ContentPaddingLargeSize)
                        .border(
                            shape = MaterialTheme.shapes.medium,
                            border = BorderStroke(
                                width = DividerSize,
                                color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
                            )
                        )
                        .clickable {
                            expandedState.value = true
                        }
                ) {
                    Row(modifier = Modifier.padding(vertical = ContentPaddingLargeSize)) {
                        Text(
                            text = stringResource(
                                id = when (expireAtState) {
                                    ExpireAt.Never,
                                    null -> {
                                        R.string.edit_status_clear_status_never
                                    }
                                    ExpireAt.In30Minutes -> {
                                        R.string.edit_status_clear_status_in_30_minutes
                                    }
                                    ExpireAt.In1Hour -> {
                                        R.string.edit_status_clear_status_in_1_hour
                                    }
                                    ExpireAt.Today -> {
                                        R.string.edit_status_clear_status_today
                                    }
                                }
                            ),
                            style = MaterialTheme.typography.button,
                            modifier = Modifier
                                .weight(weight = 1f)
                                .padding(horizontal = ContentPaddingLargeSize)
                                .align(alignment = Alignment.CenterVertically)
                        )
                        Icon(
                            contentDescription = stringResource(id = R.string.edit_status_clear_status_image_content_description),
                            painter = painterResource(id = R.drawable.ic_arrow_drop_down_24)
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    }
                    ExpireAtDropdownMenu(
                        expandedState = expandedState,
                        updateExpireAt = updateExpireAt
                    )
                }
            }
        }
        Surface(
            elevation = ContentPaddingLargeSize,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = 64.dp)
        ) {
            ConstraintLayout(modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)) {
                val (setStatusButtonRef, setStatusLoadingRef, spacerRef, clearStatusButtonRef, clearStatusLoadingRef) = createRefs()
                Button(
                    onClick = {
                        updateStatus.invoke()
                        couldDisplayErrorMessage = true
                    },
                    enabled = messageState != initialMessage
                            || emojiState != initialEmoji
                            || (dndState ?: false) != (initialIndicatesLimitedAvailability ?: false)
                            || (expireAtState != null && (!messageState.isNullOrEmpty() || !emojiState.isNullOrEmpty())),
                    modifier = Modifier
                        .constrainAs(ref = setStatusButtonRef) {
                            end.linkTo(anchor = parent.end)
                            centerVerticallyTo(other = parent)
                        }
                        .alpha(
                            0f.takeIf {
                                setStatusStatus?.status == Status.LOADING
                            } ?: 1f
                        )
                ) {
                    Text(text = stringResource(id = R.string.edit_status_set_status))
                }
                if (setStatusStatus?.status == Status.LOADING) {
                    LottieLoadingComponent(
                        modifier = Modifier.constrainAs(ref = setStatusLoadingRef) {
                            centerVerticallyTo(other = setStatusButtonRef)
                            centerHorizontallyTo(other = setStatusButtonRef)
                        }
                    )
                }
                Spacer(modifier = Modifier
                    .width(width = ContentPaddingLargeSize)
                    .constrainAs(ref = spacerRef) {
                        end.linkTo(anchor = setStatusButtonRef.start)
                        centerVerticallyTo(other = parent)
                    }
                )
                TextButton(
                    onClick = {
                        clearStatus.invoke()
                        couldDisplayErrorMessage = true
                    },
                    enabled = initialEmoji != null,
                    modifier = Modifier
                        .constrainAs(ref = clearStatusButtonRef) {
                            end.linkTo(anchor = spacerRef.start)
                            centerVerticallyTo(other = parent)
                        }
                        .alpha(
                            0f.takeIf {
                                clearStatusStatus?.status == Status.LOADING
                            } ?: 1f
                        )
                ) {
                    Text(text = stringResource(id = R.string.edit_status_clear_status))
                }
                if (clearStatusStatus?.status == Status.LOADING) {
                    LottieLoadingComponent(
                        modifier = Modifier.constrainAs(ref = clearStatusLoadingRef) {
                            centerVerticallyTo(other = clearStatusButtonRef)
                            centerHorizontallyTo(other = clearStatusButtonRef)
                        }
                    )
                }
            }
        }
    }

    if (couldDisplayErrorMessage) {
        if (clearStatusStatus?.status == Status.ERROR) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                action = clearStatus
            )
        } else if (setStatusStatus?.status == Status.ERROR) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                action = updateStatus
            )
        }
    }
}

@Composable
private fun EmojiSuggestionItem(
    @StringRes emoji: Int,
    @StringRes message: Int,
    modifier: Modifier = Modifier,
    onItemClicked: () -> Unit
) {
    Text(
        text = stringResource(
            id = R.string.edit_status_suggestion_format,
            stringResource(id = emoji),
            stringResource(id = message)
        ),
        style = MaterialTheme.typography.body1,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable(onClick = onItemClicked)
            .padding(all = ContentPaddingLargeSize)
    )
}

@Composable
private fun EmojiSuggestionRow(
    @StringRes emojiStart: Int,
    @StringRes messageStart: Int,
    onStartItemClicked: () -> Unit,
    @StringRes emojiEnd: Int,
    @StringRes messageEnd: Int,
    onEndItemClicked: () -> Unit
) {
    Row {
        EmojiSuggestionItem(
            emoji = emojiStart,
            message = messageStart,
            onItemClicked = onStartItemClicked,
            modifier = Modifier.weight(weight = 1f)
        )
        EmojiSuggestionItem(
            emoji = emojiEnd,
            message = messageEnd,
            onItemClicked = onEndItemClicked,
            modifier = Modifier.weight(weight = 1f)
        )
    }
}

@Composable
private fun ExpireAtDropdownMenu(
    expandedState: MutableState<Boolean>,
    updateExpireAt: (ExpireAt) -> Unit
) {
    @Composable
    fun Item(
        @StringRes text: Int,
        at: ExpireAt
    ) {
        DropdownMenuItem(
            onClick = {
                updateExpireAt.invoke(at)
                expandedState.value = false
            }
        ) {
            Text(text = stringResource(id = text))
        }
    }

    DropdownMenu(
        expanded = expandedState.value,
        onDismissRequest = {
            expandedState.value = false
        }
    ) {
        Item(R.string.edit_status_clear_status_never, ExpireAt.Never)
        Item(R.string.edit_status_clear_status_in_30_minutes, ExpireAt.In30Minutes)
        Item(R.string.edit_status_clear_status_in_1_hour, ExpireAt.In1Hour)
        Item(R.string.edit_status_clear_status_today, ExpireAt.Today)
    }
}

// Preview section start

@ExperimentalMaterialApi
@Preview(showBackground = true, name = "EditScreenContentPreview")
@Composable
private fun EditScreenContentPreview() {
    EditStatusScreenContent(
        topAppBarSize = 0,
        scaffoldState = rememberScaffoldState(),
        clearStatusStatus = null,
        setStatusStatus = null,

        emojiState = null,
        updateEmoji = {},

        messageState = null,
        updateMessage = {},

        expireAtState = null,
        updateExpireAt = {},

        dndState = null,
        updateDnd = {},

        getEmojiByName = { null },
        initialEmoji = ":dart:",
        initialMessage = null,
        initialIndicatesLimitedAvailability = false,
        showEmojis = {},
        updateStatus = {},
        clearStatus = {}
    )
}

// Preview section end