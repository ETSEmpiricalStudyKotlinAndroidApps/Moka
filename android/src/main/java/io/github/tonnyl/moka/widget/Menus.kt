package io.github.tonnyl.moka.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.DropDownMenuAppBarOffset
import io.github.tonnyl.moka.util.openInBrowser
import io.github.tonnyl.moka.util.shareText

/**
 * @param text Leave it empty if [share] or [openInBrowser] is not default.
 * @param share Null stands for default action.
 * @param openInBrowser Null stands for default action.
 */
@Composable
fun ShareAndOpenInBrowserMenu(
    showMenuState: MutableState<Boolean>,
    text: String,
    share: (() -> Unit)? = null,
    openInBrowser: (() -> Unit)? = null
) {
    Box {
        IconButton(
            onClick = {
                showMenuState.value = true
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = stringResource(id = R.string.more_actions_image_content_description)
            )
        }

        val context = LocalContext.current
        DropdownMenu(
            expanded = showMenuState.value,
            onDismissRequest = {
                showMenuState.value = false
            },
            offset = DropDownMenuAppBarOffset
        ) {
            DropdownMenuItem(
                onClick = {
                    showMenuState.value = false

                    if (share == null) {
                        context.shareText(text)
                    } else {
                        share.invoke()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.share))
            }

            DropdownMenuItem(
                onClick = {
                    showMenuState.value = false

                    if (openInBrowser == null) {
                        context.openInBrowser(text)
                    } else {
                        openInBrowser.invoke()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.open_in_browser))
            }
        }
    }
}