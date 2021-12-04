package io.github.tonnyl.moka.widget

import android.annotation.SuppressLint
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.tonnyl.moka.R
import io.tonnyl.moka.common.data.IssuePullRequestQueryState

@Composable
fun IssuePrFiltersDropdownMenu(
    showMenu: MutableState<Boolean>,
    queryState: MutableState<IssuePullRequestQueryState>
) {
    DropdownMenu(
        expanded = showMenu.value,
        onDismissRequest = {
            showMenu.value = false
        }
    ) {
        fun choose(state: IssuePullRequestQueryState) {
            queryState.value = state
            showMenu.value = false
        }

        DropdownMenuItem(
            onClick = {
                choose(IssuePullRequestQueryState.All)
            }
        ) {
            RadioButton(
                selected = queryState.value == IssuePullRequestQueryState.All,
                onClick = {
                    choose(IssuePullRequestQueryState.All)
                }
            )
            Text(text = stringResource(id = R.string.issue_pr_filters_query_state_all))
        }
        DropdownMenuItem(
            onClick = {
                choose(IssuePullRequestQueryState.Open)
            }
        ) {
            RadioButton(
                selected = queryState.value == IssuePullRequestQueryState.Open,
                onClick = {
                    choose(IssuePullRequestQueryState.Open)
                }
            )
            Text(text = stringResource(id = R.string.issue_pr_filters_query_state_open))
        }
        DropdownMenuItem(
            onClick = {
                choose(IssuePullRequestQueryState.Closed)
            }
        ) {
            RadioButton(
                selected = queryState.value == IssuePullRequestQueryState.Closed,
                onClick = {
                    choose(IssuePullRequestQueryState.Closed)
                }
            )
            Text(text = stringResource(id = R.string.issue_pr_filters_query_state_closed))
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(
    name = "IssuePrFiltersDropdownMenuPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun IssuePrFiltersDropdownMenuPreview() {
    IssuePrFiltersDropdownMenu(
        showMenu = mutableStateOf(true),
        queryState = mutableStateOf(IssuePullRequestQueryState.All)
    )
}