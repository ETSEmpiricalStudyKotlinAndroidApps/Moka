package io.github.tonnyl.moka.ui.repositories.filters

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.repositories.RepositoriesQueryOption
import io.github.tonnyl.moka.ui.repositories.RepositoriesQueryOption.*
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import io.tonnyl.moka.graphql.type.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Composable
fun RepositoryFiltersSheet(
    queryOptionState: MutableState<RepositoriesQueryOption>,
    affiliationCollaboratorState: MutableState<Boolean>,
    affiliationOwnerState: MutableState<Boolean>,
    ownedOrderDirectionState: MutableState<OrderDirection?>,
    orderFieldState: MutableState<RepositoryOrderField?>,
    privacyState: MutableState<RepositoryPrivacy?>,
    starOrderDirectionState: MutableState<OrderDirection?>,
    coroutineScope: CoroutineScope,
    bottomSheetState: ModalBottomSheetState
) {
    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val scaffoldState = rememberScaffoldState()

        var affiliationCollaborator by affiliationCollaboratorState
        var affiliationOwner by affiliationOwnerState
        var repositoryOrderDirection by ownedOrderDirectionState
        var orderField by orderFieldState
        var privacy by privacyState
        var starOrderDirection by starOrderDirectionState

        var warn by remember { mutableStateOf(false) }

        val contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        fun LazyListScope.repositoryOrders() {
            item {
                OptionGroupText(textResId = R.string.repositories_order_field)
            }

            item {
                RadioButtonOption(
                    isChecked = orderField == RepositoryOrderField.PUSHED_AT,
                    onCheckedChanged = {
                        orderField = RepositoryOrderField.PUSHED_AT
                    },
                    textResId = R.string.repositories_order_field_pushed_at
                )
            }

            item {
                RadioButtonOption(
                    isChecked = orderField == RepositoryOrderField.CREATED_AT,
                    onCheckedChanged = {
                        orderField = RepositoryOrderField.CREATED_AT
                    },
                    textResId = R.string.repositories_order_field_created_at
                )
            }

            item {
                RadioButtonOption(
                    isChecked = orderField == RepositoryOrderField.UPDATED_AT,
                    onCheckedChanged = {
                        orderField = RepositoryOrderField.UPDATED_AT
                    },
                    textResId = R.string.repositories_order_field_updated_at
                )
            }

            item {
                RadioButtonOption(
                    isChecked = orderField == RepositoryOrderField.NAME,
                    onCheckedChanged = {
                        orderField = RepositoryOrderField.NAME
                    },
                    textResId = R.string.repositories_order_field_name
                )
            }

            item {
                RadioButtonOption(
                    isChecked = orderField == RepositoryOrderField.STARGAZERS,
                    onCheckedChanged = {
                        orderField = RepositoryOrderField.STARGAZERS
                    },
                    textResId = R.string.repositories_order_field_stars
                )
            }

            item {
                OptionGroupText(textResId = R.string.repositories_order)
            }

            item {
                RadioButtonOption(
                    isChecked = repositoryOrderDirection == OrderDirection.DESC,
                    onCheckedChanged = {
                        repositoryOrderDirection = OrderDirection.DESC
                    },
                    textResId = R.string.sort_order_descending
                )
            }

            item {
                RadioButtonOption(
                    isChecked = repositoryOrderDirection == OrderDirection.ASC,
                    onCheckedChanged = {
                        repositoryOrderDirection = OrderDirection.ASC
                    },
                    textResId = R.string.sort_order_ascending
                )
            }
        }

        Scaffold(
            content = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(height = contentPadding.calculateTopPadding()))
                    }

                    when (queryOptionState.value) {
                        is Forks -> {
                            repositoryOrders()
                        }
                        is Owned -> {
                            item {
                                OptionGroupText(textResId = R.string.repositories_affiliation)
                            }

                            item {
                                CheckBoxOption(
                                    isChecked = affiliationOwner,
                                    onCheckedChanged = {
                                        if (!affiliationCollaborator) {
                                            warn = true
                                        } else {
                                            affiliationOwner = !affiliationOwner
                                        }
                                    },
                                    textResId = R.string.repositories_affiliation_owner
                                )
                            }

                            item {
                                CheckBoxOption(
                                    isChecked = affiliationCollaborator,
                                    onCheckedChanged = {
                                        if (!affiliationOwner) {
                                            warn = true
                                        } else {
                                            affiliationCollaborator = !affiliationCollaborator
                                        }
                                    },
                                    textResId = R.string.repositories_affiliation_collaborator
                                )
                            }

                            item {
                                OptionGroupText(textResId = R.string.repositories_privacy)
                            }

                            item {
                                RadioButtonOption(
                                    isChecked = privacy == null,
                                    onCheckedChanged = {
                                        privacy = null
                                    },
                                    textResId = R.string.repositories_privacy_all
                                )
                            }

                            item {
                                RadioButtonOption(
                                    isChecked = privacy == RepositoryPrivacy.PUBLIC,
                                    onCheckedChanged = {
                                        privacy = RepositoryPrivacy.PUBLIC
                                    },
                                    textResId = R.string.repositories_privacy_public
                                )
                            }

                            item {
                                RadioButtonOption(
                                    isChecked = privacy == RepositoryPrivacy.PRIVATE,
                                    onCheckedChanged = {
                                        privacy = RepositoryPrivacy.PRIVATE
                                    },
                                    textResId = R.string.repositories_privacy_private
                                )
                            }

                            repositoryOrders()
                        }
                        is Starred -> {
                            item {
                                OptionGroupText(textResId = R.string.repositories_order_field)
                            }

                            item {
                                RadioButtonOption(
                                    isChecked = true,
                                    onCheckedChanged = {},
                                    textResId = R.string.repositories_order_field_starred_at
                                )
                            }

                            item {
                                OptionGroupText(textResId = R.string.repositories_order)
                            }

                            item {
                                RadioButtonOption(
                                    isChecked = starOrderDirection == OrderDirection.DESC,
                                    onCheckedChanged = {
                                        starOrderDirection = OrderDirection.DESC
                                    },
                                    textResId = R.string.sort_order_descending
                                )
                            }

                            item {
                                RadioButtonOption(
                                    isChecked = starOrderDirection == OrderDirection.ASC,
                                    onCheckedChanged = {
                                        starOrderDirection = OrderDirection.ASC
                                    },
                                    textResId = R.string.sort_order_ascending
                                )
                            }
                        }
                    }
                }
            },
            snackbarHost = {
                SnackbarHost(hostState = it) { data: SnackbarData ->
                    Snackbar(snackbarData = data)
                }
            },
            scaffoldState = scaffoldState
        )

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.notification_filters))
            },
            navigationIcon = {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    },
                    content = {
                        Icon(
                            contentDescription = stringResource(id = R.string.navigate_up),
                            painter = painterResource(id = R.drawable.ic_close_24)
                        )
                    }
                )
            },
            actions = {
                IconButton(
                    onClick = {
                        when (queryOptionState.value) {
                            is Forks -> {
                                queryOptionState.value = Forks(
                                    order = RepositoryOrder(
                                        direction = repositoryOrderDirection ?: OrderDirection.DESC,
                                        field = orderField ?: RepositoryOrderField.PUSHED_AT
                                    )
                                )
                            }
                            is Owned -> {
                                queryOptionState.value = Owned(
                                    isAffiliationCollaborator = affiliationCollaborator,
                                    isAffiliationOwner = affiliationOwner,
                                    order = RepositoryOrder(
                                        direction = repositoryOrderDirection ?: OrderDirection.DESC,
                                        field = orderField ?: RepositoryOrderField.PUSHED_AT
                                    ),
                                    privacy = privacy
                                )
                            }
                            is Starred -> {
                                queryOptionState.value = Starred(
                                    order = StarOrder(
                                        direction = starOrderDirection ?: OrderDirection.DESC,
                                        field = StarOrderField.STARRED_AT
                                    )
                                )
                            }
                        }

                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check_24),
                        contentDescription = stringResource(id = R.string.done_image_description)
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )

        if (warn) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                messageId = R.string.repositories_affiliation_warning,
                actionId = null,
                dismissAction = {
                    warn = false
                }
            )
        }
    }
}

@Composable
private fun OptionGroupText(@StringRes textResId: Int) {
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = stringResource(id = textResId),
            style = MaterialTheme.typography.body2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ContentPaddingMediumSize + 14.dp, // (IconButtonSize - RadioButtonSize) / 2 = 14.dp
                    vertical = ContentPaddingMediumSize
                )
        )
    }
}

@Composable
private fun CheckBoxOption(
    isChecked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    @StringRes textResId: Int
) {
    ItemOption(
        textResId = textResId,
        onCheckedChanged = {
            onCheckedChanged.invoke(!isChecked)
        }
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChanged
        )
    }
}

@Composable
private fun RadioButtonOption(
    isChecked: Boolean,
    onCheckedChanged: (Boolean) -> Unit,
    @StringRes textResId: Int
) {
    ItemOption(
        textResId = textResId,
        onCheckedChanged = {
            onCheckedChanged.invoke(!isChecked)
        }
    ) {
        RadioButton(
            selected = isChecked,
            onClick = {
                onCheckedChanged.invoke(!isChecked)
            }
        )
    }
}

@Composable
private fun ItemOption(
    onCheckedChanged: () -> Unit,
    @StringRes textResId: Int,
    startButton: @Composable RowScope.() -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onCheckedChanged.invoke()
            }
            .padding(horizontal = ContentPaddingMediumSize)
    ) {
        IconButton(
            onClick = {
                onCheckedChanged.invoke()
            }
        ) {
            startButton()
        }
        Text(
            text = stringResource(id = textResId),
            style = MaterialTheme.typography.body1
        )
    }
}

@Preview(
    name = "CheckBoxOptionPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun CheckBoxOptionPreview() {
    CheckBoxOption(
        isChecked = false,
        onCheckedChanged = {},
        textResId = R.string.repositories_affiliation_owner
    )
}

@Preview(
    name = "RadioButtonOptionPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun RadioButtonOptionPreview() {
    RadioButtonOption(
        isChecked = true,
        onCheckedChanged = {},
        textResId = R.string.repositories_order_field_created_at
    )
}

@Preview(
    name = "OptionGroupTextPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun OptionGroupTextPreview() {
    OptionGroupText(textResId = R.string.repositories_affiliation)
}