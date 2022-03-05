package io.github.tonnyl.moka.ui.status.incident

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.status.GitHubStatusViewModel
import io.github.tonnyl.moka.ui.status.color
import io.github.tonnyl.moka.ui.theme.ContentPaddingMediumSize
import io.github.tonnyl.moka.ui.theme.IconSize
import io.github.tonnyl.moka.ui.theme.LocalAccountInstance
import io.github.tonnyl.moka.util.formatDateWithDefaultLocale
import io.github.tonnyl.moka.widget.AppBarNavigationIcon
import io.github.tonnyl.moka.widget.InsetAwareTopAppBar
import io.tonnyl.moka.common.data.GitHubIncident
import io.tonnyl.moka.common.data.GitHubIncidentStatus
import io.tonnyl.moka.common.data.GitHubIncidentStatus.*
import io.tonnyl.moka.common.data.GitHubIncidentUpdate
import io.tonnyl.moka.common.data.GitHubStatus
import io.tonnyl.moka.common.util.GitHubStatusProvider
import kotlinx.serialization.ExperimentalSerializationApi

@ExperimentalMaterialApi
@ExperimentalSerializationApi
@Composable
fun IncidentScreen(incidentId: String) {
    val currentAccount = LocalAccountInstance.current ?: return

    Box {
        var topAppBarSize by remember { mutableStateOf(0) }

        val contentPaddings = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars,
            applyTop = false,
            additionalTop = with(LocalDensity.current) { topAppBarSize.toDp() }
        )

        val viewModel = viewModel(
            initializer = {
                GitHubStatusViewModel(accountInstance = currentAccount)
            }
        )

        val status by viewModel.gitHubStatus.observeAsState()
        val incident = status?.incidents?.firstOrNull { it.id == incidentId }

        if (incident != null) {
            IncidentScreenContent(
                paddingValues = contentPaddings,
                incident = incident
            )
        }

        InsetAwareTopAppBar(
            title = {
                Text(text = stringResource(id = R.string.github_incident))
            },
            navigationIcon = {
                AppBarNavigationIcon()
            },
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { topAppBarSize = it.height }
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun IncidentScreenContent(
    paddingValues: PaddingValues,
    incident: GitHubIncident
) {
    LazyColumn(
        contentPadding = paddingValues,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            IncidentInfoItem(incident)
        }

        items(count = incident.incidentUpdates.size) {
            IncidentUpdateItem(item = incident.incidentUpdates[it])
        }
    }
}

@ExperimentalMaterialApi
@Composable
private fun IncidentInfoItem(
    incident: GitHubIncident
) {
    ListItem {
        Text(
            text = incident.name,
            color = incident.impact.color
        )
    }
}

@ExperimentalMaterialApi
@Composable
private fun IncidentUpdateItem(item: GitHubIncidentUpdate) {
    ListItem(
        icon = {
            val icon = item.status.icon()
            val modifier = Modifier
                .size(size = IconSize)
                .padding(all = ContentPaddingMediumSize)
            if (icon is Painter) {
                Icon(
                    painter = icon,
                    contentDescription = item.status.name,
                    modifier = modifier
                )
            } else {
                Icon(
                    imageVector = icon as ImageVector,
                    contentDescription = item.status.name,
                    modifier = modifier
                )
            }
        },
        secondaryText = {
            Text(text = item.updatedAt.formatDateWithDefaultLocale())
        }
    ) {
        Text(text = stringResource(id = R.string.github_incident_status_and_body, item.status.name, item.body))
    }
}

@ExperimentalSerializationApi
@ExperimentalMaterialApi
@Preview(
    name = "IncidentScreenContentPreview",
    showBackground = true,
    backgroundColor = 0xFFFFFF
)
@Composable
private fun IncidentScreenContentPreview(
    @PreviewParameter(
        provider = GitHubStatusProvider::class,
        limit = 1
    )
    status: GitHubStatus
) {
    IncidentScreenContent(
        paddingValues = PaddingValues(),
        incident = status.incidents.first()
    )
}

@Composable
private fun GitHubIncidentStatus.icon(): Any {
    return when (this) {
        Investigating -> {
            painterResource(id = R.drawable.ic_location_searching)
        }
        Identified -> {
            painterResource(id = R.drawable.ic_my_location)
        }
        Monitoring -> {
            painterResource(id = R.drawable.ic_monitor)
        }
        Resolved -> {
            Icons.Outlined.Check
        }
        Postmortem -> {
            painterResource(id = R.drawable.ic_checklist)
        }
    }
}