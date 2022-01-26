package io.github.tonnyl.moka.widget

import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.placeholder.material.placeholder
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.issuePrGreen

@Composable
fun CommitVerification(
    verified: Boolean,
    enablePlaceholder: Boolean,
    modifier: Modifier = Modifier
) {
    Image(
        imageVector = if (verified) {
            Icons.Outlined.Check
        } else {
            Icons.Outlined.Close
        },
        contentDescription = stringResource(
            id = if (verified) {
                R.string.commit_status_success
            } else {
                R.string.commit_status_failure
            }
        ),
        colorFilter = ColorFilter.tint(
            color = if (verified) {
                issuePrGreen
            } else {
                MaterialTheme.colors.error
            }
        ),
        modifier = Modifier
            .placeholder(visible = enablePlaceholder)
            .then(modifier)
    )
}

@Preview(showBackground = true, name = "CommitVerificationPreview")
@Composable
private fun CommitVerificationPreview() {
    CommitVerification(
        verified = true,
        enablePlaceholder = false
    )
}