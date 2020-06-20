package io.github.tonnyl.moka.ui.profile

import android.content.Intent
import android.os.Bundle
import android.provider.Browser.EXTRA_APPLICATION_ID
import android.provider.Browser.EXTRA_CREATE_NEW_TAB
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.PinnableItem
import io.github.tonnyl.moka.data.UserStatus
import io.github.tonnyl.moka.databinding.FragmentProfileBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.profile.ProfileEvent.*
import io.github.tonnyl.moka.ui.profile.edit.EditProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.status.EditStatusFragment
import io.github.tonnyl.moka.ui.profile.status.EditStatusFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsType
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoryType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersType
import io.github.tonnyl.moka.util.safeStartActivity

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

    private val pinnedItemAdapter by lazy(LazyThreadSafetyMode.NONE) {
        PinnedItemAdapter(viewLifecycleOwner, viewModel)
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

        val handle = findNavController().currentBackStackEntry?.savedStateHandle
        handle?.getLiveData<UserStatus?>(EditStatusFragment.RESULT_UPDATE_STATUS)
            ?.observe(viewLifecycleOwner, Observer { userStatus ->
                userStatus?.let {
                    viewModel.updateUserStatusIfNeeded(it)
                }
            })

        with(binding) {
            lifecycleOwner = viewLifecycleOwner

            appbarLayout.toolbar.setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }

            emptyViewActions = this@ProfileFragment
            viewModel = this@ProfileFragment.viewModel
            mainViewModel = this@ProfileFragment.mainViewModel
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
                    findNavController().navigate(
                        R.id.edit_status_fragment,
                        EditStatusFragmentArgs(
                            viewModel.userProfile.value?.data?.status
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
                is PinnedItemUpdate -> {
                    pinnedItemAdapter.notifyItemChanged(event.index)
                }
            }
        })

        viewModel.userProfile.observe(viewLifecycleOwner, Observer {
            if (it.data?.isViewer == true) {
                inflateMenu()
            }

            handlePinnedItems(it.data?.pinnedItems)
        })

        viewModel.organizationProfile.observe(viewLifecycleOwner, Observer {
            handlePinnedItems(it.data?.pinnedItems)
        })
    }

    override fun doAction() {

    }

    override fun retryInitial() {
        viewModel.refreshData()
    }

    private fun inflateMenu() {
        with(binding.appbarLayout.toolbar) {
            inflateMenu(R.menu.fragment_profile_option_menu)
            setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_edit_profile) {
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

                    return@setOnMenuItemClickListener true
                }

                false
            }
        }
    }

    private fun handlePinnedItems(items: List<PinnableItem>?) {
        if (items.isNullOrEmpty()) {
            return
        }

        with(binding.profilePinnedItemsList) {
            if (adapter == null) {
                layoutManager = object : LinearLayoutManager(
                    requireContext(),
                    RecyclerView.HORIZONTAL,
                    false
                ) {

                    override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                        lp?.width = (width * .8f).toInt()
                        return true
                    }

                }
                adapter = pinnedItemAdapter
                addItemDecoration(
                    PinnedItemDecoration(
                        resources.getDimensionPixelSize(R.dimen.fragment_content_padding)
                    )
                )
            }

            pinnedItemAdapter.submitList(items)
        }
    }

}