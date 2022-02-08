package io.github.tonnyl.moka.widget

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.LocalNavController

@ExperimentalComposeUiApi
@Composable
fun SearchBox(
    @StringRes hintResId: Int,
    autoFocus: Boolean = true,
    textState: MutableState<TextFieldValue>,
    onFocusChanged: (Boolean) -> Unit = { },
    onImeActionPerformed: () -> Unit = { },
    onValueChange: () -> Unit = { }
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier.padding(end = ContentPaddingLargeSize)
    ) {
        BasicTextField(
            value = textState.value,
            onValueChange = { textFieldValue ->
                textState.value = textFieldValue
                onValueChange()
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions {
                focusManager.clearFocus(force = true)
                onImeActionPerformed.invoke()
            },
            cursorBrush = SolidColor(MaterialTheme.colors.secondary),
            textStyle = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onBackground),
            modifier = Modifier
                .focusable(enabled = true)
                .focusRequester(focusRequester = focusRequester)
                .onFocusChanged {
                    if (it.isFocused) {
                        keyboardController?.show()
                    } else {
                        keyboardController?.hide()
                    }

                    onFocusChanged.invoke(it.isFocused)
                }
                .fillMaxWidth()
                .horizontalScroll(
                    state = rememberScrollState(),
                    enabled = true
                )
        )
        if (textState.value.text.isEmpty()) {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(
                    text = stringResource(id = hintResId),
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun SearchBar(
    @StringRes hintResId: Int,
    modifier: Modifier = Modifier,
    autoFocus: Boolean = true,
    textState: MutableState<TextFieldValue>,
    onFocusChanged: (Boolean) -> Unit = { },
    onImeActionPerformed: () -> Unit = { },
    elevation: Dp = 4.dp
) {
    val navController = LocalNavController.current

    InsetAwareTopAppBar(
        title = {
            SearchBox(
                hintResId = hintResId,
                autoFocus = autoFocus,
                textState = textState,
                onFocusChanged = onFocusChanged,
                onImeActionPerformed = onImeActionPerformed
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                },
                content = {
                    Icon(
                        contentDescription = stringResource(id = R.string.navigate_up),
                        imageVector = Icons.Outlined.ArrowBack
                    )
                }
            )
        },
        elevation = elevation,
        modifier = modifier
    )
}

@SuppressLint("UnrememberedMutableState")
@ExperimentalComposeUiApi
@Preview(name = "SearchBarPreview", showBackground = true)
@Composable
private fun SearchBarPreview() {
    SearchBar(
        hintResId = R.string.search_emoji,
        textState = mutableStateOf(TextFieldValue())
    )
}