package io.github.tonnyl.moka.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Browser.EXTRA_APPLICATION_ID
import android.provider.Browser.EXTRA_CREATE_NEW_TAB
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.profile.ProfileEvent.*
import io.github.tonnyl.moka.ui.profile.edit.EditProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.status.EditStatusArgs
import io.github.tonnyl.moka.ui.profile.status.EditStatusFragment
import io.github.tonnyl.moka.ui.profile.status.EditStatusFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsType
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.ui.theme.MokaTheme
import io.github.tonnyl.moka.ui.users.UsersFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersType
import io.github.tonnyl.moka.util.isDarkModeOn
import io.github.tonnyl.moka.util.safeStartActivity
import io.github.tonnyl.moka.widget.TopAppBarElevation

class ProfileFragment : Fragment(), EmptyViewActions {

    private val args by navArgs<ProfileFragmentArgs>()
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory(args)
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private val specifiedProfileType: ProfileType
        get() {
            return if (args.profileType == ProfileType.NOT_SPECIFIED) {
                if (viewModel.userProfile.value != null) {
                    ProfileType.USER
                } else {
                    ProfileType.ORGANIZATION
                }
            } else {
                args.profileType
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_container, container, false)

        (view as ViewGroup).setContent(Recomposer.current()) {
            val scrollState = rememberScrollState()
            MokaTheme(darkTheme = resources.isDarkModeOn) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            backgroundColor = MaterialTheme.colors.background,
                            title = { Text(text = stringResource(id = R.string.profile_title)) },
                            navigationIcon = {
                                IconButton(
                                    onClick = { findNavController().navigateUp() },
                                    icon = { Icon(vectorResource(R.drawable.ic_arrow_back_24)) }
                                )
                            },
                            elevation = TopAppBarElevation(lifted = scrollState.value != .0f),
                            actions = {
                                val userResource = viewModel.userProfile.observeAsState()
                                if (userResource.value?.data?.isViewer == true) {
                                    IconButton(onClick = {
                                        val user = userResource.value?.data ?: return@IconButton

                                        findNavController().navigate(
                                            R.id.edit_profile_fragment,
                                            EditProfileFragmentArgs(
                                                user.login,
                                                user.name,
                                                user.bio,
                                                user.websiteUrl?.toString(),
                                                user.company,
                                                user.location,
                                                user.twitterUsername
                                            ).toBundle()
                                        )
                                    }) {
                                        Icon(asset = vectorResource(id = R.drawable.ic_edit_24))
                                    }
                                }
                            }
                        )
                    },
                    bodyContent = {
                        ProfileScreen(
                            scrollState,
                            mainViewModel.currentUser.value?.login
                        ) {
                            mainViewModel.getEmojiByName(it)
                        }
                    }
                )
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handle = findNavController().currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<UserStatus?>(EditStatusFragment.RESULT_UPDATE_STATUS)
            ?.observe(viewLifecycleOwner) { userStatus ->
                userStatus?.let {
                    viewModel.updateUserStatusIfNeeded(it)
                }
            }

        viewModel.userEvent.observe(viewLifecycleOwner, EventObserver { event ->
            when (event) {
                is ViewRepositories -> {
                    findNavController().navigate(
                        R.id.repositories_fragment,
                        RepositoriesFragmentArgs(
                            args.login,
                            RepositoryType.OWNED,
                            specifiedProfileType
                        ).toBundle()
                    )
                }
                is ViewStars -> {
                    findNavController().navigate(
                        R.id.repositories_fragment,
                        RepositoriesFragmentArgs(
                            args.login,
                            RepositoryType.STARRED,
                            specifiedProfileType
                        ).toBundle()
                    )
                }
                is ViewFollowers -> {
                    findNavController().navigate(
                        R.id.action_user_profile_to_users,
                        UsersFragmentArgs(
                            args.login,
                            UsersType.FOLLOWER
                        ).toBundle()
                    )
                }
                is ViewFollowings -> {
                    findNavController().navigate(
                        R.id.action_user_profile_to_users,
                        UsersFragmentArgs(
                            args.login,
                            UsersType.FOLLOWING
                        ).toBundle()
                    )
                }
                is ViewProjects -> {
                    findNavController().navigate(
                        R.id.nav_projects,
                        ProjectsFragmentArgs(
                            args.login,
                            "",
                            if (specifiedProfileType == ProfileType.USER) {
                                ProjectsType.UsersProjects
                            } else {
                                ProjectsType.OrganizationsProjects
                            }
                        ).toBundle()
                    )
                }
                is EditStatus -> {
                    val status = viewModel.userProfile.value?.data?.status
                    findNavController().navigate(
                        R.id.edit_status_fragment,
                        EditStatusFragmentArgs(
                            EditStatusArgs(
                                status?.emoji,
                                status?.message,
                                status?.indicatesLimitedAvailability
                            )
                        ).toBundle()
                    )
                }
                is ViewRepository -> {
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(
                            event.repository.owner.login,
                            event.repository.name
                        ).toBundle()
                    )
                }
                is ViewGist -> {
                    requireContext().safeStartActivity(
                        Intent(Intent.ACTION_VIEW, event.gist.url).apply {
                            putExtra(EXTRA_CREATE_NEW_TAB, true)
                            putExtra(EXTRA_APPLICATION_ID, requireContext().packageName)
                        }
                    )
                }
            }
        })
    }

    override fun doAction() {

    }

    override fun retryInitial() {
        viewModel.refreshData()
    }

}