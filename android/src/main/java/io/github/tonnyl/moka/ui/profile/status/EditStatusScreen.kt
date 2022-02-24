package io.github.tonnyl.moka.ui.profile.status

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.DividerSize
import io.github.tonnyl.moka.ui.theme.LocalMainViewModel
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.data.ExpireAt
import io.tonnyl.moka.common.data.SearchableEmoji
import io.tonnyl.moka.common.network.Status
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalPagingApi
@ExperimentalSerializationApi
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

    Box(modifier = Modifier.navigationBarsPadding()) {
        var topAppBarSize by remember { mutableStateOf(0) }

        val mainViewModel = LocalMainViewModel.current

        Scaffold(
            content = {
                EditStatusScreenContent(
                    topAppBarSize = topAppBarSize,
                    scaffoldState = scaffoldState,

                    viewModel = viewModel,

                    getEmojiByName = { mainViewModel.getEmojiByName(it) },
                    initialEmoji = initialEmoji,
                    initialMessage = initialMessage,
                    initialIndicatesLimitedAvailability = initialIndicatesLimitedAvailability
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            },
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.edit_status)) },
            navigationIcon = {
                AppBarNavigationIcon(
                    contentDescription = stringResource(id = R.string.navigate_close),
                    imageVector = Icons.Outlined.Close
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalSerializationApi
@Composable
private fun EditStatusScreenContent(
    topAppBarSize: Int,
    scaffoldState: ScaffoldState,

    viewModel: EditStatusViewModel,

    getEmojiByName: (String) -> SearchableEmoji?,
    initialEmoji: String?,
    initialMessage: String?,
    initialIndicatesLimitedAvailability: Boolean?,
) {
    val slowToResponse = stringResource(id = R.string.edit_status_busy_message)

    var couldDisplayErrorMessage by remember { mutableStateOf(false) }

    val clearStatusStatus by viewModel.clearStatusState.observeAsState()
    val setStatusStatus by viewModel.updateStatusState.observeAsState()

    val emoji by viewModel.emojiName.observeAsState()
    val expireAt by viewModel.expiresAt.observeAsState()
    val message by viewModel.message.observeAsState()
    val dnd by viewModel.limitedAvailability.observeAsState()

    val navController = LocalNavController.current

    Column {
        LazyColumn(
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyTop = false,
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
                        emoji = emoji,
                        getEmojiByName = getEmojiByName,
                        enablePlaceholder = false,
                        modifier = Modifier
                            .clip(shape = MaterialTheme.shapes.medium)
                            .align(alignment = Alignment.CenterVertically)
                            .clickable(
                                onClick = {
                                    navController.navigate(route = Screen.Emojis.route)
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    // todo Make text field single-lined.
                    OutlinedTextField(
                        value = message ?: "",
                        onValueChange = {
                            viewModel.updateMessage(it.trim())
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
                        viewModel.updateEmoji(onVacationEmoji)
                        viewModel.updateMessage(onVacationMessage)
                    },
                    emojiEnd = R.string.edit_status_suggestion_working_remotely_emoji,
                    messageEnd = R.string.edit_status_suggestion_working_remotely_message,
                    onEndItemClicked = {
                        viewModel.updateEmoji(workingRemotelyEmoji)
                        viewModel.updateMessage(workingRemoteMessage)
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
                        viewModel.updateEmoji(outSickEmoji)
                        viewModel.updateMessage(outSickMessage)
                    },
                    emojiEnd = R.string.edit_status_suggestion_commuting_emoji,
                    messageEnd = R.string.edit_status_suggestion_commuting_message,
                    onEndItemClicked = {
                        viewModel.updateEmoji(commutingEmoji)
                        viewModel.updateMessage(commutingMessage)
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
                        viewModel.updateEmoji(inAMeetingEmoji)
                        viewModel.updateMessage(inAMeetingMessage)
                    },
                    emojiEnd = R.string.edit_status_suggestion_focusing_emoji,
                    messageEnd = R.string.edit_status_suggestion_focusing_message,
                    onEndItemClicked = {
                        viewModel.updateEmoji(focusingEmoji)
                        viewModel.updateMessage(focusingMessage)
                    }
                )
            }
            item {
                Row {
                    val updateUI = {
                        if (dnd == true) {
                            if (message.isNullOrEmpty()) {
                                viewModel.updateMessage(slowToResponse)
                            }
                        } else if (message == slowToResponse) {
                            viewModel.updateMessage("")
                        }
                    }
                    Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    Checkbox(
                        checked = dnd ?: false,
                        onCheckedChange = {
                            viewModel.updateLimitedAvailability(it)
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
                                viewModel.updateLimitedAvailability(!(dnd ?: false))
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
                                id = when (expireAt) {
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
                            imageVector = Icons.Outlined.ArrowDropDown
                        )
                        Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))
                    }
                    ExpireAtDropdownMenu(
                        expandedState = expandedState,
                        updateExpireAt = {
                            viewModel.updateExpireAt(it)
                        }
                    )
                }
            }
        }
        val buttonHeight = 64.dp

        Surface(
            elevation = ContentPaddingLargeSize,
            modifier = Modifier
                .fillMaxWidth()
                .height(height = buttonHeight)
        ) {
            ConstraintLayout(modifier = Modifier.padding(horizontal = ContentPaddingLargeSize)) {
                val (setStatusButtonRef, setStatusLoadingRef, spacerRef, clearStatusButtonRef, clearStatusLoadingRef) = createRefs()
                Button(
                    onClick = {
                        viewModel.updateStatus()
                        couldDisplayErrorMessage = true
                    },
                    enabled = message != initialMessage
                            || emoji != initialEmoji
                            || (dnd ?: false) != (initialIndicatesLimitedAvailability ?: false)
                            || (expireAt != null && (!message.isNullOrEmpty() || !emoji.isNullOrEmpty())),
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
                        modifier = Modifier
                            .height(height = buttonHeight)
                            .constrainAs(ref = setStatusLoadingRef) {
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
                        viewModel.clearStatus()
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
                        modifier = Modifier
                            .height(height = buttonHeight)
                            .constrainAs(ref = clearStatusLoadingRef) {
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
                action = viewModel::clearStatus,
                dismissAction = viewModel::onClearStatusErrorDismissed
            )
        } else if (setStatusStatus?.status == Status.ERROR) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                action = viewModel::updateStatus,
                dismissAction = viewModel::onUpdateStatusErrorDismissed
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