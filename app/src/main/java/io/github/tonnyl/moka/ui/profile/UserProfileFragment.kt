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
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.net.Status
import io.github.tonnyl.moka.ui.profile.edit.EditProfileFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragment
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersFragment
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.fragment_user_profile.*

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

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        val factory = ViewModelFactory(login)
        viewModel = ViewModelProviders.of(this, factory).get(UserProfileViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner, Observer { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    val user = resource.data?.user() ?: return@Observer

                    GlideLoader.loadAvatar(user.avatarUrl().toString(), profile_avatar)
                    profile_username.text = user.name()
                    profile_login_name.text = user.login()
                    profile_bio.text = user.bio()
                    profile_company_content.text = user.company()
                    profile_location_content.text = user.location()
                    profile_email_content.text = user.email()
                    profile_website_content.text = user.websiteUrl().toString()
                    username = user.name()
                    profile_repositories_count_text.text = formatNumberWithSuffix(user.repositories().totalCount())
                    profile_stars_count_text.text = formatNumberWithSuffix(user.starredRepositories().totalCount())
                    profile_followers_count_text.text = formatNumberWithSuffix(user.followers().totalCount())
                    profile_following_count_text.text = formatNumberWithSuffix(user.following().totalCount())

                    user.organizations().nodes()?.let {
                        profile_organizations.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
                        profile_organizations.setHasFixedSize(true)

                        val adapter = ProfileOrganizationAdapter()
                        profile_organizations.adapter = adapter
                        adapter.submitList(it)
                    }

                    val flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_YEAR or DateUtils.FORMAT_ABBREV_MONTH
                    profile_joined_on_content.text = DateUtils.formatDateTime(requireContext(), user.createdAt().time, flags)
                    profile_updated_on_content.text = DateUtils.formatDateTime(requireContext(), user.updatedAt().time, flags)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

        profile_repositories_text_layout.setOnClickListener(this)
        profile_stars_text_layout.setOnClickListener(this)
        profile_followers_text_layout.setOnClickListener(this)
        profile_following_text_layout.setOnClickListener(this)
        profile_company_layout.setOnClickListener(this)
        profile_email_layout.setOnClickListener(this)
        profile_location_layout.setOnClickListener(this)
        profile_website_layout.setOnClickListener(this)
        profile_organizations_category_see_all.setOnClickListener(this)
        toolbar_edit.setOnClickListener(this)
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