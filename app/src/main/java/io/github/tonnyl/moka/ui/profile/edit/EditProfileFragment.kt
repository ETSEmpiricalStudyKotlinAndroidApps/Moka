package io.github.tonnyl.moka.ui.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedTask
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.widget.LottieLoadingComponent
import io.github.tonnyl.moka.widget.TopAppBarElevation

class EditProfileFragment : Fragment() {

    private val args by navArgs<EditProfileFragmentArgs>()

    private val viewModel by viewModels<EditProfileViewModel>(
        factoryProducer = {
            ViewModelFactory(args)
        }
    )

    @ExperimentalMaterialApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_container, container, false)

        (view as ViewGroup).setContent(Recomposer.current()) {
            val scrollState = rememberScrollState()
            val scaffoldState = rememberScaffoldState()

            val name by viewModel.name.observeAsState()
            val bio by viewModel.bio.observeAsState()
            val url by viewModel.url.observeAsState()
            val company by viewModel.company.observeAsState()
            val location by viewModel.location.observeAsState()
            val twitterUsername by viewModel.twitterUsername.observeAsState()

            val updateState by viewModel.loadingStatus.observeAsState()

            MokaTheme(darkTheme = resources.isDarkModeOn) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.background,
                            title = {
                                Text(text = stringResource(id = R.string.profile_title))
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = { findNavController().navigateUp() },
                                    icon = { Icon(vectorResource(R.drawable.ic_close_24)) }
                                )
                            },
                            elevation = TopAppBarElevation(lifted = scrollState.value != .0f),
                            actions = {
                                val enabled = name != args.name
                                        || bio != args.bio
                                        || url != args.url
                                        || company != args.company
                                        || location != args.location
                                        || twitterUsername != args.twitter
                                IconButton(
                                    onClick = {
                                        if (enabled) {
                                            viewModel.updateUserInformation()
                                        } else {
                                            findNavController().navigateUp()
                                        }
                                    },
                                    // ☹️ actions of TopAppBar have set emphasis internally...
                                    // So in theory, setting enabled won't change the appearance at all.
                                    enabled = enabled
                                ) {
                                    if (updateState?.status == Status.LOADING) {
                                        LottieLoadingComponent()
                                    } else {
                                        Icon(asset = vectorResource(id = R.drawable.ic_check_24))
                                    }
                                }
                            }
                        )
                    },
                    bodyContent = {
                        EditProfileScreen(scrollState = scrollState)
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = it) { data: SnackbarData ->
                            Snackbar(snackbarData = data)
                        }
                    },
                    scaffoldState = scaffoldState
                )

                when (updateState?.status) {
                    Status.ERROR -> {
                        val message = stringResource(id = R.string.common_error_requesting_data)
                        val action = stringResource(id = R.string.common_retry)

                        LaunchedTask {
                            val result = scaffoldState.snackbarHostState.showSnackbar(
                                message = message,
                                actionLabel = action,
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.updateUserInformation()
                            }
                        }
                    }
                    Status.SUCCESS -> {
                        findNavController().navigateUp()
                    }
                    else -> {

                    }
                }
            }
        }

        return view
    }

}