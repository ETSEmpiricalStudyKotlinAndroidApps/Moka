package io.github.tonnyl.moka.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.accompanist.insets.navigationBarsPadding
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.theme.LottieLoadingAnimationSize
import io.github.tonnyl.moka.widget.LottieLoadingComponent
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import io.tonnyl.moka.common.build.CommonBuildConfig
import io.tonnyl.moka.common.data.Account
import io.tonnyl.moka.common.network.KtorClient
import io.tonnyl.moka.common.network.Resource
import io.tonnyl.moka.common.network.Status
import kotlinx.datetime.Clock

@Composable
fun AuthScreen(
    authTokenAndUserResource: Resource<Pair<String, Account>>?,
    scaffoldState: ScaffoldState,
    getAuthToken: (AuthParameter) -> Unit
) {
    val context = LocalContext.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        val guideline = createGuidelineFromTop(fraction = .5f)
        val (logoImageRef, appNameTextRef, getStartedButtonRef, progressBarRef) = createRefs()

        Image(
            contentDescription = stringResource(id = R.string.auth_logo_image_content_description),
            painter = painterResource(id = R.drawable.ic_app_icon_24),
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(size = 128.dp)
                .constrainAs(ref = logoImageRef) {
                    centerHorizontallyTo(other = parent)
                    top.linkTo(anchor = parent.top)
                    bottom.linkTo(anchor = appNameTextRef.top)
                }
        )
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.constrainAs(ref = appNameTextRef) {
                centerHorizontallyTo(other = parent)
                top.linkTo(anchor = logoImageRef.bottom)
                bottom.linkTo(anchor = guideline)
            },
            style = MaterialTheme.typography.h6
        )

        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val authResult =
                        result.data?.getParcelableExtra<AuthParameter>(AuthBrowserActivity.RESULT_AUTH_RESULT)
                    if (authResult != null) {
                        getAuthToken.invoke(authResult)
                    }
                }
            }

        val getStarted = {
            launcher.launch(Intent(context, AuthBrowserActivity::class.java).apply {
                putExtra(
                    AuthBrowserActivity.ARG_URL,
                    "${KtorClient.GITHUB_AUTHORIZE_URL}?client_id=${CommonBuildConfig.CLIENT_ID}&redirect_uri=${KtorClient.GITHUB_AUTHORIZE_CALLBACK_URI}&scope=${KtorClient.SCOPE}&state=${
                        Clock.System.now().toEpochMilliseconds()
                    }"
                )
            })
        }

        Button(
            onClick = getStarted,
            modifier = Modifier
                .constrainAs(ref = getStartedButtonRef) {
                    centerHorizontallyTo(other = parent)
                    bottom.linkTo(anchor = parent.bottom)
                    top.linkTo(anchor = guideline)
                }
                .alpha(
                    alpha = 0f.takeIf {
                        authTokenAndUserResource?.status == Status.LOADING
                                || authTokenAndUserResource?.status == Status.SUCCESS
                    } ?: 1f
                )
        ) {
            Text(text = stringResource(id = R.string.auth_get_started))
        }

        if (authTokenAndUserResource?.status == Status.LOADING
            || authTokenAndUserResource?.status == Status.SUCCESS
        ) {
            LottieLoadingComponent(
                modifier = Modifier
                    .size(size = LottieLoadingAnimationSize)
                    .constrainAs(ref = progressBarRef) {
                        centerHorizontallyTo(other = parent)
                        bottom.linkTo(anchor = parent.bottom)
                        top.linkTo(anchor = guideline)
                    }
            )
        } else if (authTokenAndUserResource?.status == Status.ERROR) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                action = getStarted,
                actionId = R.string.common_retry,
                duration = SnackbarDuration.Indefinite
            )
        }
    }
}

@Preview(name = "AuthScreen", showBackground = true)
@Composable
private fun AuthScreenContentPreview() {
    AuthScreen(
        authTokenAndUserResource = Resource.error(null, null),
        scaffoldState = rememberScaffoldState(),
        getAuthToken = {}
    )
}