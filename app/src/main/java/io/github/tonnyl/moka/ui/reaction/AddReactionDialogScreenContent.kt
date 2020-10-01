package io.github.tonnyl.moka.ui.reaction

import androidx.annotation.StringRes
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.ripple.RippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.type.ReactionContent

@Preview(name = "AddReactionDialog", showBackground = true)
@Composable
private fun AddReactionDialogScreenContentPreview() {
    AddReactionDialogScreenContent(
        isThumbUpChecked = true,
        isThumbDownChecked = false,
        isLaughChecked = true,
        isHoorayChecked = false,
        isConfusedChecked = false,
        isHeartChecked = true,
        isRockedChecked = false,
        isEyesChecked = true
    ) { _, _ ->

    }
}

@Composable
fun AddReactionDialogScreenContent(
    isThumbUpChecked: Boolean,
    isThumbDownChecked: Boolean,
    isLaughChecked: Boolean,
    isHoorayChecked: Boolean,
    isConfusedChecked: Boolean,
    isHeartChecked: Boolean,
    isRockedChecked: Boolean,
    isEyesChecked: Boolean,
    react: (ReactionContent, Boolean) -> Unit
) {
    var thumbUpChecked = remember { isThumbUpChecked }
    var thumbDownChecked = remember { isThumbDownChecked }
    var laughChecked = remember { isLaughChecked }
    var hoorayChecked = remember { isHoorayChecked }
    var confusedChecked = remember { isConfusedChecked }
    var heartChecked = remember { isHeartChecked }
    var rockedChecked = remember { isRockedChecked }
    var eyesChecked = remember { isEyesChecked }

    Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.fragment_content_padding))) {
        EmojiRow(
            id1 = R.string.emoji_thumbs_up,
            onClick1 = {
                thumbUpChecked = !thumbUpChecked
                react(ReactionContent.THUMBS_UP, thumbUpChecked)
            },
            checked1 = thumbUpChecked,
            id2 = R.string.emoji_thumbs_down,
            onClick2 = {
                thumbDownChecked = !thumbDownChecked
                react(ReactionContent.THUMBS_DOWN, thumbDownChecked)
            },
            checked2 = thumbDownChecked,
            id3 = R.string.emoji_laugh,
            onClick3 = {
                laughChecked = !laughChecked
                react(ReactionContent.LAUGH, laughChecked)
            },
            checked3 = laughChecked,
            id4 = R.string.emoji_hooray,
            onClick4 = {
                hoorayChecked = !hoorayChecked
                react(ReactionContent.HOORAY, hoorayChecked)
            },
            checked4 = hoorayChecked
        )
        Spacer(modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.fragment_content_padding)))
        EmojiRow(
            id1 = R.string.emoji_confused,
            onClick1 = {
                confusedChecked = !confusedChecked
                react(ReactionContent.CONFUSED, confusedChecked)
            },
            checked1 = confusedChecked,
            id2 = R.string.emoji_heart,
            onClick2 = {
                heartChecked = !heartChecked
                react(ReactionContent.HEART, heartChecked)
            },
            checked2 = heartChecked,
            id3 = R.string.emoji_rocket,
            onClick3 = {
                rockedChecked = !rockedChecked
                react(ReactionContent.ROCKET, rockedChecked)
            },
            checked3 = rockedChecked,
            id4 = R.string.emoji_eyes,
            onClick4 = {
                eyesChecked = !eyesChecked
                react(ReactionContent.EYES, eyesChecked)
            },
            checked4 = eyesChecked
        )
    }
}

@Composable
private fun EmojiRow(
    @StringRes id1: Int,
    onClick1: () -> Unit,
    checked1: Boolean,
    @StringRes id2: Int,
    onClick2: () -> Unit,
    checked2: Boolean,
    @StringRes id3: Int,
    onClick3: () -> Unit,
    checked3: Boolean,
    @StringRes id4: Int,
    onClick4: () -> Unit,
    checked4: Boolean
) {
    Row {
        EmojiButton(id = id1, onClick = onClick1, checked = checked1)
        Spacer(modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.fragment_content_padding)))
        EmojiButton(id = id2, onClick = onClick2, checked = checked2)
        Spacer(modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.fragment_content_padding)))
        EmojiButton(id = id3, onClick = onClick3, checked = checked3)
        Spacer(modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.fragment_content_padding)))
        EmojiButton(id = id4, onClick = onClick4, checked = checked4)
    }
}

@Composable
private fun EmojiButton(
    @StringRes id: Int,
    onClick: () -> Unit,
    checked: Boolean
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.preferredSize(dimensionResource(id = R.dimen.reaction_emoji_button_size))
            .toggleable(
                value = checked,
                enabled = true,
                onValueChange = { },
                indication = RippleIndication(color = MaterialTheme.colors.primary)
            ),
        backgroundColor = if (checked) {
            MaterialTheme.colors.primary.copy(alpha = .08f)
        } else {
            Color.Transparent
        }
    ) {
        Text(
            text = stringResource(id = id),
            fontSize = 24.sp,
            color = MaterialTheme.colors.onBackground
        )
    }
}