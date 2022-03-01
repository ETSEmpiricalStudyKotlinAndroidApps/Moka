package io.github.tonnyl.moka.ui.issues.create

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.ExperimentalPagingApi
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.Screen
import io.github.tonnyl.moka.ui.theme.ContentPaddingLargeSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.ui.theme.LocalNavController
import io.github.tonnyl.moka.widget.*
import io.tonnyl.moka.common.network.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalPagingApi
@ExperimentalSerializationApi
@Composable
fun CreateIssueScreen(repoId: String) {
    val currentAccount = LocalAccountInstance.current ?: return

    val viewModel = viewModel(
        initializer = {
            CreateIssueViewModel(
                extra = CreateIssueViewModelExtra(
                    accountInstance = currentAccount,
                    repoId = repoId
                )
            )
        }
    )

    val displayDiscardAlert = remember { mutableStateOf(false) }

    val navController = LocalNavController.current

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val scaffoldState = rememberScaffoldState()

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val createIssueResource = viewModel.createIssueLiveData.observeAsState()

        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    InsetAwareSnackbar(data = data)
                }
            }
        ) {
            CreateIssueScreenContent(
                contentPadding = contentPadding,
                titleState = viewModel.titleState,
                bodyState = viewModel.bodyState,
                create = viewModel::create
            )

            when (createIssueResource.value?.status) {
                Status.ERROR -> {
                    SnackBarErrorMessage(
                        scaffoldState = scaffoldState,
                        action = viewModel::create,
                        actionId = R.string.common_retry,
                        dismissAction = viewModel::onCreateIssueErrorDismissed
                    )
                }
                Status.SUCCESS -> {
                    val issue = createIssueResource.value?.data ?: return@Scaffold

                    Screen.Issue.navigate(
                        navController = navController,
                        login = issue.repository.owner.login,
                        repoName = issue.repository.name,
                        number = issue.number
                    ) {
                        popUpTo(route = navController.currentBackStackEntry?.destination?.route ?: return@navigate) {
                            inclusive = true
                        }
                    }

                    viewModel.onCreateIssueErrorDismissed()
                }
                Status.LOADING -> {
                    LocalSoftwareKeyboardController.current?.hide()
                    LocalFocusManager.current.clearFocus(force = true)
                }
                else -> {

                }
            }
        }

        InsetAwareTopAppBar(
            title = { Text(text = stringResource(id = R.string.create_issue)) },
            navigationIcon = {
                AppBarNavigationIcon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = stringResource(id = R.string.navigate_close),
                    onClick = {
                        if (viewModel.titleState.value.isNotEmpty()
                            || viewModel.bodyState.value.isNotEmpty()
                        ) {
                            displayDiscardAlert.value = true
                        } else {
                            navController.navigateUp()
                        }
                    }
                )
            },
            actions = {
                AnimatedVisibility(
                    visible = viewModel.titleState.value.isNotEmpty(),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Box {
                        if (createIssueResource.value?.status == Status.LOADING) {
                            LottieLoadingComponent(modifier = Modifier.size(size = IconSize))
                        } else {
                            IconButton(
                                onClick = viewModel::create,
                                enabled = createIssueResource.value?.status == null
                                        || createIssueResource.value?.status == Status.ERROR
                            ) {
                                Icon(
                                    contentDescription = stringResource(id = R.string.done_image_description),
                                    imageVector = Icons.Outlined.Send
                                )
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }

    if (viewModel.titleState.value.isNotEmpty()
        || viewModel.bodyState.value.isNotEmpty()
    ) {
        BackHandler {
            displayDiscardAlert.value = true
        }
    }

    if (displayDiscardAlert.value) {
        DiscardAlert(
            navController = navController,
            displayDiscardAlertState = displayDiscardAlert
        )
    }

}

@Composable
private fun DiscardAlert(
    navController: NavController,
    displayDiscardAlertState: MutableState<Boolean>
) {
    AlertDialog(
        onDismissRequest = {
            displayDiscardAlertState.value = false
        },
        title = {
            Text(text = stringResource(id = R.string.create_issue_discard_changes_alert_title))
        },
        text = {
            Text(text = stringResource(id = R.string.create_issue_discard_changes_alert_message))
        },
        dismissButton = {
            TextButton(
                onClick = {
                    displayDiscardAlertState.value = false
                }
            ) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    displayDiscardAlertState.value = false
                    navController.navigateUp()
                }
            ) {
                Text(text = stringResource(id = R.string.create_issue_discard_changes_alert_confirm))
            }
        }
    )
}

@ExperimentalComposeUiApi
@Composable
private fun CreateIssueScreenContent(
    contentPadding: PaddingValues,
    titleState: MutableState<String>,
    bodyState: MutableState<String>,
    create: () -> Unit
) {
    val titleFocusRequester = remember { FocusRequester() }
    val bodyFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            // It is really weired the keyboard may not display properly if there is no delay.
            // Can reproduce this bug by launching search screen from app launcher shortcut.
            delay(1)
            titleFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(modifier = Modifier.padding(paddingValues = contentPadding)) {
        BasicTextField(
            value = titleState.value,
            onValueChange = {
                titleState.value = it
            },
            textStyle = MaterialTheme.typography.body1,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions {
                bodyFocusRequester.requestFocus()
            },
            cursorBrush = SolidColor(value = MaterialTheme.colors.primary),
            decorationBox = {
                Box {
                    TextFieldHint(
                        input = titleState.value,
                        hintResId = R.string.create_issue_title_hint
                    )
                    it.invoke()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = ContentPaddingLargeSize)
                .focusable(enabled = true)
                .focusRequester(focusRequester = titleFocusRequester)
                .clearFocusOnKeyboardDismiss()
        )
        BasicTextField(
            value = bodyState.value,
            onValueChange = {
                bodyState.value = it
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions {
                if (titleState.value.isNotEmpty()) {
                    create.invoke()
                }
            },
            cursorBrush = SolidColor(TextFieldDefaults.textFieldColors().cursorColor(isError = false).value),
            textStyle = MaterialTheme.typography.body1,
            decorationBox = {
                Box {
                    TextFieldHint(
                        input = bodyState.value,
                        hintResId = R.string.create_issue_body_hint
                    )
                    it.invoke()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight = 1f)
                .padding(all = ContentPaddingLargeSize)
                .focusable(enabled = true)
                .focusRequester(focusRequester = bodyFocusRequester)
        )
    }
}

@Composable
private fun TextFieldHint(
    input: String,
    @StringRes hintResId: Int
) {
    if (input.isEmpty()) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = stringResource(id = hintResId),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@ExperimentalComposeUiApi
@SuppressLint("UnrememberedMutableState")
@Preview(
    name = "CreateIssueScreenContentPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun CreateIssueScreenContentPreview() {
    CreateIssueScreenContent(
        contentPadding = PaddingValues(),
        titleState = mutableStateOf(""),
        bodyState = mutableStateOf(""),
        create = { }
    )
}