package io.github.tonnyl.moka.ui.auth

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ContextAmbient
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.ui.tooling.preview.Preview
import io.github.tonnyl.moka.BuildConfig
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.AuthenticatedUser
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.RetrofitClient
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.util.safeStartActivity

@Preview(name = "AuthScreen", showBackground = true)
@Composable
private fun AuthScreenContentPreview() {
    AuthScreenContent(authTokenAndUserResult = MutableLiveData(Resource.error(null, null)))
}

@Composable
fun AuthScreenContent(
    authTokenAndUserResult: LiveData<Resource<Pair<String, AuthenticatedUser>>>
) {
    val context = ContextAmbient.current
    val authTokenAndUser = authTokenAndUserResult.observeAsState().value

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val guideline = createGuidelineFromTop(.5f)
        val (logoImageRef, appNameTextRef, getStartedButtonRef, progressBarRef) = createRefs()

        Image(
            asset = imageResource(id = R.drawable.splash_screen_logo),
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

        if (authTokenAndUser?.status == Status.LOADING
            || authTokenAndUser?.status == Status.SUCCESS
        ) {
            AndroidView(
                viewBlock = { context ->
                    LayoutInflater.from(context)
                        .inflate(R.layout.view_lottie_loading, FrameLayout(context), false)
                },
                modifier = Modifier.constrainAs(progressBarRef) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(guideline)
                }
            )
        } else {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(
                            """
                                |${RetrofitClient.GITHUB_AUTHORIZE_URL}
                                |?client_id=${BuildConfig.CLIENT_ID}
                                |&redirect_uri=${RetrofitClient.GITHUB_AUTHORIZE_CALLBACK_URI}
                                |&scope=${RetrofitClient.SCOPE}
                                |&state=${System.currentTimeMillis()}
                            """.trimMargin()
                        )
                    }
                    context.safeStartActivity(intent, null, null)
                },
                modifier = Modifier.constrainAs(getStartedButtonRef) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                    top.linkTo(guideline)
                }
            ) {
                Text(text = stringResource(id = R.string.auth_get_started))
            }
        }
    }
}