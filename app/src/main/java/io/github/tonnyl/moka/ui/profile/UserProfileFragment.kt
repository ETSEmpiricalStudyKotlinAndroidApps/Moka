package io.github.tonnyl.moka.ui.profile

import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentUserProfileBinding
import io.github.tonnyl.moka.network.GlideLoader
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.profile.edit.EditProfileFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragment
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersFragment
import io.github.tonnyl.moka.util.formatNumberWithSuffix

class UserProfileFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: UserProfileViewModel
    private val args: UserProfileFragmentArgs by navArgs()

    private var username: String? = ""

    private lateinit var login: String

    private lateinit var binding: FragmentUserProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login = args.login

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        val factory = ViewModelFactory(login)
        viewModel = ViewModelProviders.of(this, factory).get(UserProfileViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val user = resource.data?.user() ?: return@Observer

                    GlideLoader.loadAvatar(user.avatarUrl().toString(), binding.profileAvatar)
                    binding.profileUsername.text = user.name()
                    binding.profileLoginName.text = user.login()
                    binding.profileBio.text = user.bio()
                    binding.profileCompanyContent.text = user.company()
                    binding.profileLocationContent.text = user.location()
                    binding.profileEmailContent.text = user.email()
                    binding.profileWebsiteContent.text = user.websiteUrl().toString()
                    username = user.name()
                    binding.profileRepositoriesCountText.text = formatNumberWithSuffix(user.repositories().totalCount())
                    binding.profileStarsCountText.text = formatNumberWithSuffix(user.starredRepositories().totalCount())
                    binding.profileFollowersCountText.text = formatNumberWithSuffix(user.followers().totalCount())
                    binding.profileFollowingCountText.text = formatNumberWithSuffix(user.following().totalCount())

                    user.organizations().nodes()?.let {
                        with(binding.profileOrganizations) {
                            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                            setHasFixedSize(true)
                            val adapter = ProfileOrganizationAdapter()
                            this.adapter = adapter
                            adapter.submitList(it)
                        }

                    }

                    val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
                    binding.profileJoinedOnContent.text = DateUtils.formatDateTime(requireContext(), user.createdAt().time, flags)
                    binding.profileUpdatedOnContent.text = DateUtils.formatDateTime(requireContext(), user.updatedAt().time, flags)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

        binding.profileRepositoriesTextLayout.setOnClickListener(this)
        binding.profileStarsTextLayout.setOnClickListener(this)
        binding.profileFollowersTextLayout.setOnClickListener(this)
        binding.profileFollowingTextLayout.setOnClickListener(this)
        binding.profileCompanyLayout.setOnClickListener(this)
        binding.profileEmailLayout.setOnClickListener(this)
        binding.profileLocationLayout.setOnClickListener(this)
        binding.profileWebsiteLayout.setOnClickListener(this)
        binding.profileOrganizationsCategorySeeAll.setOnClickListener(this)
        binding.toolbarEdit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.profile_followers_text_layout -> {
                val bundle = Bundle().apply {
                    putString("login", login)
                    putString("users_type", UsersFragment.USER_TYPE_FOLLOWERS)
                    putString("username", username)
                }
                parentFragment?.findNavController()?.navigate(R.id.action_user_profile_to_users, bundle)
            }
            R.id.profile_following_text_layout -> {
                val bundle = Bundle().apply {
                    putString("login", login)
                    putString("users_type", UsersFragment.USER_TYPE_FOLLOWING)
                    putString("username", username)
                }
                parentFragment?.findNavController()?.navigate(R.id.action_user_profile_to_users, bundle)
            }
            R.id.profile_repositories_text_layout -> {
                val builder = RepositoriesFragmentArgs(login, RepositoriesFragment.REPOSITORY_TYPE_OWNED, username
                        ?: return)
                parentFragment?.findNavController()?.navigate(R.id.action_to_repositories, builder.toBundle())
            }
            R.id.profile_stars_text_layout -> {
                val builder = RepositoriesFragmentArgs(login, RepositoriesFragment.REPOSITORY_TYPE_STARS, username
                        ?: return)
                parentFragment?.findNavController()?.navigate(R.id.action_to_repositories, builder.toBundle())
            }
            R.id.toolbar_edit -> {
                viewModel.user.value?.data?.user()?.let {
                    val bundle = EditProfileFragmentArgs(it.login(), it.name(), it.email(), it.bio(), it.websiteUrl()?.toString(), it.company(), it.location())
                    parentFragment?.findNavController()?.navigate(R.id.action_to_edit_profile, bundle.toBundle())
                }
            }
        }
    }

}