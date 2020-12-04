package io.github.tonnyl.moka.ui.profile.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.emojis.EmojisFragment
import io.github.tonnyl.moka.ui.profile.status.EditStatusEvent.ShowEmojis
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.widget.TopAppBarElevation

class EditStatusFragment : Fragment() {

    private val args by navArgs<EditStatusFragmentArgs>()

    private val editStatusViewModel by viewModels<EditStatusViewModel>(
        factoryProducer = {
            ViewModelFactory(args)
        }
    )
    private val mainViewModel by activityViewModels<MainViewModel>()

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

            MokaTheme(darkTheme = resources.isDarkModeOn) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.background,
                            title = { Text(text = stringResource(id = R.string.edit_status)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { findNavController().navigateUp() },
                                    content = { Icon(imageVector = vectorResource(R.drawable.ic_close_24)) }
                                )
                            },
                            elevation = TopAppBarElevation(lifted = scrollState.value != .0f)
                        )
                    },
                    bodyContent = {
                        EditStatusScreen(
                            scaffoldState = scaffoldState,
                            scrollState = scrollState,
                            mainViewModel = mainViewModel
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = it) { data: SnackbarData ->
                            Snackbar(snackbarData = data)
                        }
                    },
                    scaffoldState = scaffoldState
                )
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handle = findNavController().currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<String>(EmojisFragment.RESULT_EMOJI)
            ?.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    editStatusViewModel.updateEmoji(it)
                }
            }

        editStatusViewModel.event.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                is ShowEmojis -> {
                    findNavController().navigate(R.id.emojis_fragment)
                }
            }
        })

        val updateUserStatusObserver = Observer<Resource<UserStatus?>> {
            if (it.status == Status.SUCCESS) {
                findNavController().previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(RESULT_UPDATE_STATUS, it.data)

                findNavController().navigateUp()
            }
        }

        editStatusViewModel.updateStatusState.observe(viewLifecycleOwner, updateUserStatusObserver)
        editStatusViewModel.clearStatusState.observe(viewLifecycleOwner, updateUserStatusObserver)
    }

    companion object {

        const val RESULT_UPDATE_STATUS = "result_update_status"

    }

}