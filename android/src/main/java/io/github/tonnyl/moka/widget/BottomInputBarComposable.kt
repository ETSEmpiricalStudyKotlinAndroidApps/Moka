package io.github.tonnyl.moka.widget

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.navigationBarsWithImePadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.tonnyl.moka.graphql.type.LockReason

@Composable
fun BottomInputBar(
    lockReason: LockReason?,
    viewerCanEdit: Boolean,
    textState: MutableState<String>,
    onSend: () -> Unit,
    isSending: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
            .background(color = Color.White)
            .padding(all = ContentPaddingLargeSize)
            .navigationBarsWithImePadding()
    ) {
        if (!viewerCanEdit) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(
                        id = R.string.issue_pr_conversation_locked,
                        stringResource(
                            id = when (lockReason) {
                                LockReason.OFF_TOPIC -> {
                                    R.string.issue_lock_reason_off_topic
                                }
                                LockReason.RESOLVED -> {
                                    R.string.issue_lock_reason_resolved
                                }
                                LockReason.SPAM -> {
                                    R.string.issue_lock_reason_spam
                                }
                                LockReason.TOO_HEATED -> {
                                    R.string.issue_lock_reason_too_heated
                                }
                                LockReason.UNKNOWN__,
                                null -> {
                                    R.string.issue_lock_reason_unknown
                                }
                            }
                        )
                    ),
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.defaultMinSize(minHeight = 40.dp)
                        .clip(shape = RoundedCornerShape(size = 20.dp))
                        .border(
                            color = MaterialTheme.colors.onBackground.copy(alpha = .12f),
                            width = 1.dp,
                            shape = RoundedCornerShape(size = 40.dp)
                        )
                        .padding(
                            horizontal = ContentPaddingLargeSize,
                            vertical = ContentPaddingMediumSize
                        )
                )
            }

            return@Row
        }

        Box(
            modifier = Modifier.weight(weight = 1f)
                .defaultMinSize(minHeight = 40.dp)
                .clip(shape = RoundedCornerShape(size = 20.dp))
                .border(
                    color = MaterialTheme.colors.onBackground.copy(alpha = .12f),
                    width = 1.dp,
                    shape = RoundedCornerShape(size = 40.dp)
                )
                .padding(
                    horizontal = ContentPaddingLargeSize,
                    vertical = ContentPaddingMediumSize
                )
                .wrapContentHeight()
        ) {
            BasicTextField(
                value = textState.value,
                onValueChange = {
                    textState.value = it
                },
                cursorBrush = SolidColor(MaterialTheme.colors.secondary),
                maxLines = 5,
                textStyle = MaterialTheme.typography.body2,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onSend.invoke()
                    }
                )
            )
            if (textState.value.isEmpty()) {
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = stringResource(id = R.string.issue_pr_comment),
                        style = MaterialTheme.typography.body2
                    )
                }
            }
        }
        AnimatedVisibility(visible = textState.value.isNotEmpty() || isSending) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(height = 40.dp)
            ) {
                Spacer(modifier = Modifier.width(width = ContentPaddingLargeSize))

                Box(modifier = Modifier.size(size = 40.dp)) {
                    if (isSending) {
                        CircularProgressIndicator(modifier = Modifier.padding(all = ContentPaddingMediumSize))
                    } else if (textState.value.isNotEmpty()) {
                        IconButton(onClick = onSend) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send_24),
                                contentDescription = stringResource(id = R.string.issue_pr_comment)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "BottomInputBarPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@SuppressLint("UnrememberedMutableState")
@Composable
private fun BottomInputBarPreview() {
    BottomInputBar(
        lockReason = null,
        viewerCanEdit = true,
        textState = mutableStateOf("abc"),
        onSend = {},
        isSending = false
    )
}

@Preview(
    name = "BottomInputBarLockedPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@SuppressLint("UnrememberedMutableState")
@Composable
private fun BottomInputBarLockedPreview() {
    BottomInputBar(
        lockReason = LockReason.OFF_TOPIC,
        viewerCanEdit = false,
        textState = mutableStateOf("abc"),
        onSend = {},
        isSending = false
    )
}