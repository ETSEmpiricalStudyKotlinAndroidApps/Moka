package io.github.tonnyl.moka.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentProfileBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.UserEvent
import io.github.tonnyl.moka.ui.profile.edit.EditProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.status.EditStatusFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsType
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.users.UsersFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersType

class ProfileFragment : Fragment(), EmptyViewActions {

    private val args by navArgs<ProfileFragmentArgs>()
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory(args)
    }
    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var binding: FragmentProfileBinding

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
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.apply {
            emptyViewActions = this@ProfileFragment
            viewModel = this@ProfileFragment.viewModel
            mainViewModel = this@ProfileFragment.mainViewModel
            lifecycleOwner = this@ProfileFragment
        }

        viewModel.userEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                ProfileEvent.EDIT_PROFILE -> {
                    editProfile()
                }
                ProfileEvent.VIEW_REPOSITORIES -> {
                    openRepositories()
                }
                ProfileEvent.VIEW_STARS -> {
                    openStars()
                }
                ProfileEvent.VIEW_FOLLOWERS -> {
                    openFollowers()
                }
                ProfileEvent.VIEW_FOLLOWINGS -> {
                    openFollowings()
                }
                ProfileEvent.VIEW_PROJECTS -> {
                    openProjects()
                }
                ProfileEvent.VIEW_AVATAR -> {
                    openAvatar()
                }
                ProfileEvent.CLICK_EMAIL -> {
                    openEmail()
                }
                ProfileEvent.CLICK_WEBSITE -> {
                    openWebsite()
                }
                ProfileEvent.CLICK_LOCATION -> {
                    openLocation()
                }
                ProfileEvent.CLICK_COMPANY -> {
                    openCompany()
                }
                ProfileEvent.EDIT_STATUS -> {
                    findNavController().navigate(
                        R.id.edit_status_fragment,
                        EditStatusFragmentArgs(
                            viewModel.userProfile.value?.data?.status
                        ).toBundle()
                    )
                }
            }
        })

        mainViewModel.fragmentScopedEvent.observe(viewLifecycleOwner, EventObserver {
            if (it is UserEvent.UpdateUserState) {
                viewModel.updateUserStatus(it.userStatus)
            }
        })
    }

    private fun editProfile() {
        viewModel.userProfile.value?.data?.let {
            findNavController().navigate(
                R.id.edit_profile_fragment,
                EditProfileFragmentArgs(
                    it.login,
                    it.name,
                    it.email,
                    it.bio,
                    it.websiteUrl?.toString(),
                    it.company,
                    it.location
                ).toBundle()
            )
        }
    }

    private fun openRepositories() {
        findNavController().navigate(
            R.id.repositories_fragment,
            RepositoriesFragmentArgs(
                args.login,
                RepositoryType.OWNED,
                specifiedProfileType
            ).toBundle()
        )
    }

    private fun openStars() {
        findNavController().navigate(
            R.id.repositories_fragment,
            RepositoriesFragmentArgs(
                args.login,
                RepositoryType.STARRED,
                specifiedProfileType
            ).toBundle()
        )
    }

    private fun openFollowers() {
        findNavController().navigate(
            R.id.action_user_profile_to_users,
            UsersFragmentArgs(
                args.login,
                UsersType.FOLLOWER
            ).toBundle()
        )
    }

    private fun openFollowings() {
        findNavController().navigate(
            R.id.action_user_profile_to_users,
            UsersFragmentArgs(
                args.login,
                UsersType.FOLLOWING
            ).toBundle()
        )
    }

    private fun openProjects() {
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

    private fun openEmail() {

    }

    private fun openWebsite() {

    }

    private fun openLocation() {

    }

    private fun openCompany() {

    }

    private fun openAvatar() {

    }

    override fun doAction() {

    }

    override fun retryInitial() {
        viewModel.refreshData()
    }

}