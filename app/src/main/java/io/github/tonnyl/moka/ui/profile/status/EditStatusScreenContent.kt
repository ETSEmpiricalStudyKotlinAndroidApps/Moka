package io.github.tonnyl.moka.ui.profile.status

import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawOpacity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.viewinterop.viewModel
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.SearchableEmoji
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.widget.EmojiComponent
import io.github.tonnyl.moka.widget.LottieLoadingComponent

@ExperimentalMaterialApi
@Composable
fun EditStatusScreen(
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,
    mainViewModel: MainViewModel
) {
    val viewModel = viewModel<EditStatusViewModel>()
    val clearStatus by viewModel.clearStatusState.observeAsState(null)
    val setStatus by viewModel.updateStatusState.observeAsState(null)

    val expireAt by viewModel.expiresAt.observeAsState()
    val emoji by viewModel.emojiName.observeAsState()
    val message by viewModel.message.observeAsState()
    val dnd by viewModel.limitedAvailability.observeAsState()

    EditStatusScreenContent(
        scaffoldState = scaffoldState,
        scrollState = scrollState,
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
        initialArgs = viewModel.args,
        showEmojis = { viewModel.showEmojis() },
        updateStatus = { viewModel.updateStatus() },
        clearStatus = { viewModel.clearStatus() }
    )
}

@ExperimentalMaterialApi
@Composable
private fun EditStatusScreenContent(
    scaffoldState: ScaffoldState,
    scrollState: ScrollState,

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
    initialArgs: EditStatusFragmentArgs,
    showEmojis: () -> Unit,
    updateStatus: () -> Unit,
    clearStatus: () -> Unit
) {
    val roundedCorner = RoundedCornerShape(dimensionResource(id = R.dimen.regular_radius))
    val slowToResponse = stringResource(id = R.string.edit_status_busy_message)

    var couldDisplayErrorMessage by remember { mutableStateOf(false) }

    Column {
        ScrollableColumn(
            scrollState = scrollState,
            modifier = Modifier.weight(1f)
                .padding(vertical = dimensionResource(id = R.dimen.fragment_content_padding))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))
            ) {
                EmojiComponent(
                    emoji = emojiState,
                    getEmojiByName = getEmojiByName,
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .clickable(onClick = showEmojis)
                )
                Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
                // todo Make text field single-lined.
                OutlinedTextField(
                    value = messageState ?: "",
                    onValueChange = {
                        updateMessage.invoke(it.trim())
                    },
                    label = {
                        Text(text = stringResource(id = R.string.edit_status_hint_whats_happening))
                    },
                    modifier = Modifier.weight(1f)
                        .align(Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.preferredHeight(dimensionResource(id = R.dimen.fragment_content_padding)))
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Text(
                    text = stringResource(id = R.string.edit_status_suggestions),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))
                )
            }
            Spacer(modifier = Modifier.preferredHeight(dimensionResource(id = R.dimen.fragment_content_padding)))
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
                Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
                Checkbox(
                    checked = dndState ?: false,
                    onCheckedChange = {
                        updateDnd.invoke(it)
                        updateUI.invoke()
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = stringResource(id = R.string.edit_status_do_not_disturb),
                    modifier = Modifier.align(Alignment.CenterVertically)
                        .clip(roundedCorner)
                        .clickable(onClick = {
                            updateDnd.invoke(!(dndState ?: false))
                            updateUI.invoke()
                        })
                        .padding(dimensionResource(id = R.dimen.fragment_content_padding))
                )
            }
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Text(
                    text = stringResource(id = R.string.edit_status_busy_info),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))
                )
            }
            Spacer(modifier = Modifier.preferredHeight(dimensionResource(id = R.dimen.fragment_content_padding)))
            ProvideEmphasis(emphasis = AmbientEmphasisLevels.current.medium) {
                Text(
                    text = stringResource(id = R.string.edit_status_clear_status),
                    style = MaterialTheme.typography.body2,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))
                )
            }
            Spacer(modifier = Modifier.preferredHeight(dimensionResource(id = R.dimen.fragment_content_padding)))
            val expandedState = mutableStateOf(false)
            Box(
                alignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))
                    .border(
                        shape = roundedCorner,
                        border = BorderStroke(
                            width = dimensionResource(id = R.dimen.divider_size),
                            color = MaterialTheme.colors.onBackground.copy(alpha = .12f)
                        )
                    )
                    .clickable(
                        onClick = {
                            expandedState.value = true
                        }
                    )
            ) {
                Row(modifier = Modifier.padding(vertical = dimensionResource(id = R.dimen.fragment_content_padding))) {
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
                        modifier = Modifier.weight(1f)
                            .padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))
                            .align(Alignment.CenterVertically)
                    )
                    Icon(asset = vectorResource(id = R.drawable.ic_arrow_drop_down_24))
                    Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding)))
                }
                ExpireAtDropdownMenu(
                    expandedState = expandedState,
                    updateExpireAt = updateExpireAt
                )
            }
        }
        Surface(
            elevation = dimensionResource(id = R.dimen.fragment_content_padding),
            modifier = Modifier.fillMaxWidth()
                .preferredHeight(dimensionResource(id = R.dimen.edit_status_bottom_bar_height))
        ) {
            ConstraintLayout(modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.fragment_content_padding))) {
                val (setStatusButtonRef, setStatusLoadingRef, spacerRef, clearStatusButtonRef, clearStatusLoadingRef) = createRefs()
                Button(
                    onClick = {
                        updateStatus.invoke()
                        couldDisplayErrorMessage = true
                    },
                    enabled = messageState != initialArgs.status?.message
                            || emojiState != initialArgs.status?.emoji
                            || (dndState
                        ?: false) != (initialArgs.status?.indicatesLimitedAvailability
                        ?: false)
                            || (expireAtState != null && (!messageState.isNullOrEmpty() || !emojiState.isNullOrEmpty())),
                    modifier = Modifier.constrainAs(setStatusButtonRef) {
                        end.linkTo(parent.end)
                        centerVerticallyTo(parent)
                    }.drawOpacity(
                        0f.takeIf {
                            setStatusStatus?.status == Status.LOADING
                        } ?: 1f
                    )
                ) {
                    Text(text = stringResource(id = R.string.edit_status_set_status))
                }
                if (setStatusStatus?.status == Status.LOADING) {
                    LottieLoadingComponent(
                        modifier = Modifier.constrainAs(setStatusLoadingRef) {
                            centerVerticallyTo(setStatusButtonRef)
                            centerHorizontallyTo(setStatusButtonRef)
                        }
                    )
                }
                Spacer(modifier = Modifier.preferredWidth(dimensionResource(id = R.dimen.fragment_content_padding))
                    .constrainAs(spacerRef) {
                        end.linkTo(setStatusButtonRef.start)
                        centerVerticallyTo(parent)
                    }
                )
                TextButton(
                    onClick = {
                        clearStatus.invoke()
                        couldDisplayErrorMessage = true
                    },
                    enabled = initialArgs.status != null,
                    modifier = Modifier.constrainAs(clearStatusButtonRef) {
                        end.linkTo(spacerRef.start)
                        centerVerticallyTo(parent)
                    }.drawOpacity(
                        0f.takeIf {
                            clearStatusStatus?.status == Status.LOADING
                        } ?: 1f
                    )
                ) {
                    Text(text = stringResource(id = R.string.edit_status_clear_status))
                }
                if (clearStatusStatus?.status == Status.LOADING) {
                    LottieLoadingComponent(
                        modifier = Modifier.constrainAs(clearStatusLoadingRef) {
                            centerVerticallyTo(clearStatusButtonRef)
                            centerHorizontallyTo(clearStatusButtonRef)
                        }
                    )
                }
            }
        }
    }

    if (couldDisplayErrorMessage) {
        if (clearStatusStatus?.status == Status.ERROR) {
            ErrorMessage(
                scaffoldState = scaffoldState,
                retry = clearStatus
            )
        } else if (setStatusStatus?.status == Status.ERROR) {
            ErrorMessage(
                scaffoldState = scaffoldState,
                retry = updateStatus,
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
        modifier = modifier.clip(RoundedCornerShape(dimensionResource(id = R.dimen.regular_radius)))
            .clickable(onClick = onItemClicked)
            .padding(dimensionResource(id = R.dimen.fragment_content_padding))
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
            modifier = Modifier.weight(1f)
        )
        EmojiSuggestionItem(
            emoji = emojiEnd,
            message = messageEnd,
            onItemClicked = onEndItemClicked,
            modifier = Modifier.weight(1f)
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
        toggle = {},
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

@ExperimentalMaterialApi
@Composable
private fun ErrorMessage(
    scaffoldState: ScaffoldState,
    retry: () -> Unit
) {
    val message = stringResource(id = R.string.common_error_requesting_data)
    val action = stringResource(id = R.string.common_retry)

    LaunchedTask {
        val result = scaffoldState.snackbarHostState.showSnackbar(
            message = message,
            actionLabel = action,
            duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) {
            retry.invoke()
        }
    }
}

// Preview section start

@ExperimentalMaterialApi
@Preview(showBackground = true, name = "EditScreenContentPreview")
@Composable
private fun EditScreenContentPreview() {
    EditStatusScreenContent(
        scaffoldState = rememberScaffoldState(),
        scrollState = rememberScrollState(),
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
        initialArgs = EditStatusFragmentArgs(
            EditStatusArgs(
                emoji = ":dart:",
                indicatesLimitedAvailability = false,
                message = null
            )
        ),
        showEmojis = {},
        updateStatus = {},
        clearStatus = {}
    )
}

// Preview section end