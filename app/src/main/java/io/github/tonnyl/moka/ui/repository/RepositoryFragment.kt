package io.github.tonnyl.moka.ui.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.issues.IssuesFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsType
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryEvent.*
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.widget.SnackBarErrorMessage
import io.github.tonnyl.moka.widget.TopAppBarElevation

class RepositoryFragment : Fragment() {

    private val args by navArgs<RepositoryFragmentArgs>()

    private val viewModel by viewModels<RepositoryViewModel> {
        ViewModelFactory(args)
    }

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

            val usersRepository by viewModel.usersRepository.observeAsState()
            val organizationsRepository by viewModel.organizationsRepository.observeAsState()

            val repo = usersRepository?.data ?: organizationsRepository?.data

            val starredState by viewModel.starState.observeAsState()
            val followState by viewModel.followState.observeAsState()

            MokaTheme(darkTheme = resources.isDarkModeOn) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.background,
                            title = { Text(text = stringResource(id = R.string.repository)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { findNavController().navigateUp() },
                                    content = { Icon(imageVector = vectorResource(R.drawable.ic_arrow_back_24)) }
                                )
                            },
                            elevation = TopAppBarElevation(lifted = scrollState.value != .0f),
                            actions = {
                                if (repo?.viewerCanAdminister == true) {
                                    IconButton(onClick = {}) {
                                        Icon(vectorResource(id = R.drawable.ic_edit_24))
                                    }
                                }
                            }
                        )
                    },
                    bodyContent = {
                        RepositoryScreen(scrollState = scrollState)
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = it) { data: SnackbarData ->
                            Snackbar(snackbarData = data)
                        }
                    },
                    bottomBar = {
                        if (repo != null) {
                            BottomAppBar(
                                backgroundColor = MaterialTheme.colors.background,
                                cutoutShape = CircleShape
                            ) {
                                IconButton(onClick = {}) {
                                    Icon(imageVector = vectorResource(id = R.drawable.ic_code_24))
                                }
                                IconButton(onClick = {}) {
                                    Icon(imageVector = vectorResource(id = R.drawable.ic_eye_24))
                                }
                                IconButton(onClick = {}) {
                                    Icon(imageVector = vectorResource(id = R.drawable.ic_code_fork_24))
                                }
                            }
                        }
                    },
                    floatingActionButton = {
                        if (repo != null) {
                            FloatingActionButton(
                                onClick = { viewModel.toggleStar() },
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = vectorResource(
                                        id = if (starredState?.data == true) {
                                            R.drawable.ic_star_24
                                        } else {
                                            R.drawable.ic_star_border_24
                                        }
                                    ),
                                    tint = MaterialTheme.colors.onPrimary
                                )
                            }
                        }
                    },
                    isFloatingActionButtonDocked = true,
                    floatingActionButtonPosition = FabPosition.End,
                    scaffoldState = scaffoldState
                )
            }

            if (followState?.status == Status.ERROR) {
                SnackBarErrorMessage(
                    scaffoldState = scaffoldState,
                    action = viewModel::toggleFollow
                )
            } else if (starredState?.status == Status.ERROR) {
                SnackBarErrorMessage(
                    scaffoldState = scaffoldState,
                    action = viewModel::toggleStar
                )
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is ViewOwnersProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(args.login, it.type).toBundle()
                    )
                }
                is ViewWatchers -> {

                }
                is ViewStargazers -> {

                }
                is ViewForks -> {

                }
                is ViewIssues -> {
                    findNavController().navigate(
                        R.id.issues_fragment,
                        IssuesFragmentArgs(args.login, args.name).toBundle()
                    )
                }
                is ViewPullRequests -> {
                    findNavController().navigate(
                        R.id.prs_fragment,
                        PullRequestsFragmentArgs(args.login, args.name).toBundle()
                    )
                }
                is ViewProjects -> {
                    findNavController().navigate(
                        R.id.nav_projects,
                        ProjectsFragmentArgs(
                            args.login,
                            args.name,
                            ProjectsType.RepositoriesProjects
                        ).toBundle()
                    )
                }
                is ViewLicense -> {

                }
                is ViewBranches -> {

                }
                is ViewAllTopics -> {

                }
                is ViewReleases -> {

                }
                is ViewLanguages -> {

                }
                is ViewFiles -> {

                }
            }
        })

    }

}