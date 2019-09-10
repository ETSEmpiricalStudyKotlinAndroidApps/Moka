package io.github.tonnyl.moka.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentProfileBinding
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.profile.edit.EditProfileFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.users.UsersFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersType

class ProfileFragment : Fragment(), ProfileActions, EmptyViewActions {

    private val args by navArgs<ProfileFragmentArgs>()
    private val viewModel by viewModels<ProfileViewModel> {
        ViewModelFactory(args.login, args.profileType)
    }

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
            actions = this@ProfileFragment
            lifecycleOwner = this@ProfileFragment
        }

        viewModel.loadStatus.observe(this, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false

                    binding.snackbar.show(
                        messageId = R.string.common_error_requesting_data,
                        actionId = R.string.common_retry
                    )
                }
                Status.LOADING -> {

                }
            }
        })

    }

    override fun toggleFollow() {

    }

    override fun editProfile() {
        viewModel.userProfile.value?.let {
            val bundle = EditProfileFragmentArgs(
                it.login,
                it.name,
                it.email,
                it.bio,
                it.websiteUrl?.toString(),
                it.company,
                it.location
            )
            parentFragment?.findNavController()
                ?.navigate(R.id.edit_profile_fragment, bundle.toBundle())
        }
    }

    override fun openRepositories() {
        val builder = RepositoriesFragmentArgs(
            args.login,
            RepositoryType.OWNED,
            specifiedProfileType
        )
        parentFragment?.findNavController()
            ?.navigate(R.id.repositories_fragment, builder.toBundle())
    }

    override fun openStars() {
        val builder = RepositoriesFragmentArgs(
            args.login,
            RepositoryType.STARRED,
            specifiedProfileType
        )
        parentFragment?.findNavController()
            ?.navigate(R.id.repositories_fragment, builder.toBundle())
    }

    override fun openFollowers() {
        val args = UsersFragmentArgs(
            args.login,
            UsersType.FOLLOWER
        ).toBundle()
        parentFragment?.findNavController()
            ?.navigate(R.id.action_user_profile_to_users, args)
    }

    override fun openFollowings() {
        val args = UsersFragmentArgs(
            args.login,
            UsersType.FOLLOWING
        ).toBundle()
        parentFragment?.findNavController()
            ?.navigate(R.id.action_user_profile_to_users, args)
    }

    override fun openProjects() {

    }

    override fun openEmail() {

    }

    override fun openWebsite() {

    }

    override fun openLocation() {

    }

    override fun openCompany() {

    }

    override fun openAvatar() {

    }

    override fun doAction() {

    }

    override fun retryInitial() {
        viewModel.refreshData()
    }

}