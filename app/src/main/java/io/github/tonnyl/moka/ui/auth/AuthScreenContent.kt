package io.github.tonnyl.moka.ui.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.chrisbanes.accompanist.insets.navigationBarsPadding
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.widget.LottieLoadingComponent
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import kotlinx.datetime.Clock

@ExperimentalMaterialApi
@Composable
fun AuthScreen(
    authTokenAndUserResource: Resource<Pair<String, AuthenticatedUser>>?,
    scaffoldState: ScaffoldState
) {
    val context = AmbientContext.current

    ConstraintLayout(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
        val guideline = createGuidelineFromTop(.5f)
        val (logoImageRef, appNameTextRef, getStartedButtonRef, progressBarRef) = createRefs()

        Image(
            bitmap = imageResource(id = R.drawable.splash_screen_logo),
            modifier = Modifier.constrainAs(logoImageRef) {
                centerHorizontallyTo(parent)
                top.linkTo(parent.top)
                bottom.linkTo(appNameTextRef.top)
            }
        )
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier.constrainAs(appNameTextRef) {
                centerHorizontallyTo(parent)
                top.linkTo(logoImageRef.bottom)
                bottom.linkTo(guideline)
            },
            style = MaterialTheme.typography.h6
        )

        val getStarted = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    """
                        |${RetrofitClient.GITHUB_AUTHORIZE_URL}
                        |?client_id=${BuildConfig.CLIENT_ID}
                        |&redirect_uri=${RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI}
                        |&scope=${RetrofitClient.SCOPE}
                        |&state=${Clock.System.now().toEpochMilliseconds()}
                        """.trimMargin()
                )
            }
            context.safeStartActivity(intent, null, null)
        }

        Button(
            onClick = getStarted,
            modifier = Modifier.constrainAs(getStartedButtonRef) {
                centerHorizontallyTo(parent)
                bottom.linkTo(parent.bottom)
                top.linkTo(guideline)
            }.alpha(
                0f.takeIf {
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
                modifier = Modifier.constrainAs(progressBarRef) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(guideline)
                }
            )
        } else if (authTokenAndUserResource?.status == Status.ERROR) {
            SnackBarErrorMessage(
                scaffoldState = scaffoldState,
                action = getStarted,
                duration = SnackbarDuration.Indefinite
            )
        }
    }
}

@ExperimentalMaterialApi
@Preview(name = "AuthScreen", showBackground = true)
@Composable
private fun AuthScreenContentPreview() {
    AuthScreen(
        authTokenAndUserResource = Resource.error(null, null),
        scaffoldState = rememberScaffoldState()
    )
}