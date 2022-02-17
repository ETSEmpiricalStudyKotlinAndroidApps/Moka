package io.github.tonnyl.moka.widget

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LocalNavController

@Composable
fun AppBarNavigationIcon(
    onClick: (() -> Unit)? = null,
    imageVector: ImageVector = Icons.Outlined.ArrowBack,
    contentDescription: String? = stringResource(id = R.string.navigate_up)
) {
    val navController = if (onClick == null) {
        LocalNavController.current
    } else {
        null
    }

    IconButton(
        onClick = {
            onClick?.invoke() ?: navController?.navigateUp()
        },
        content = {
            Icon(
                contentDescription = contentDescription,
                imageVector = imageVector
            )
        }
    )
}