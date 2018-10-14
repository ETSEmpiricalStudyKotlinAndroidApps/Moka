package io.github.tonnyl.moka.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.net.GlideLoader
import io.github.tonnyl.moka.ui.RepositoryAdapter
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragment
import io.github.tonnyl.moka.ui.repositories.RepositoriesFragmentArgs
import io.github.tonnyl.moka.ui.users.UsersFragment
import io.github.tonnyl.moka.util.dp2px
import io.github.tonnyl.moka.util.formatNumberWithSuffix
import kotlinx.android.synthetic.main.fragment_user_profile.*

class UserProfileFragment : Fragment(), AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private lateinit var viewModel: UserProfileViewModel

    private var titleTextToTopHeight = 0
    private var username: String? = ""

    private lateinit var login: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_user_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login = UserProfileFragmentArgs.fromBundle(arguments).login

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        val factory = ViewModelFactory(login)
        viewModel = ViewModelProviders.of(this, factory).get(UserProfileViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.hasErrors().not()) {
                val user = response.data()?.user() ?: return@Observer

                username = user.name()

                GlideLoader.loadAvatar(user.avatarUrl().toString(), profile_avatar)
                profile_username.text = user.name()
                profile_login_name.text = user.login()
                profile_bio.text = user.bio()
                profile_company_image.setOnClickListener {
                    snackbar.show(messageText = user.company(), actionId = R.string.user_profile_company_action, actionClick = {
                        snackbar.dismiss()
                    })
                }
                profile_location_image.setOnClickListener {
                    snackbar.show(messageText = user.location(), actionId = R.string.user_profile_location_action, actionClick = {
                        snackbar.dismiss()
                    })
                }
                profile_email_image.setOnClickListener {
                    snackbar.show(messageText = user.email(), actionId = R.string.user_profile_email_action, actionClick = {
                        snackbar.dismiss()
                    })
                }
                profile_link_image.setOnClickListener {
                    snackbar.show(messageText = user.websiteUrl().toString(), actionId = R.string.user_profile_link_action, actionClick = {
                        snackbar.dismiss()
                    })
                }
                username = user.name()
                profile_repositories_text.text = getString(R.string.user_profile_repositories, formatNumberWithSuffix(user.repositories().totalCount()), resources.getQuantityText(R.plurals.user_profile_repositories_plurals, user.repositories().totalCount()))
                profile_stars_text.text = getString(R.string.user_profile_stars, formatNumberWithSuffix(user.starredRepositories().totalCount()), resources.getQuantityText(R.plurals.user_profile_stars_plurals, user.starredRepositories().totalCount()))
                profile_followers_text.text = getString(R.string.user_profile_followers, formatNumberWithSuffix(user.followers().totalCount()), resources.getQuantityText(R.plurals.user_profile_followers_plurals, user.followers().totalCount()))
                profile_following_text.text = resources.getString(R.string.user_profile_following, formatNumberWithSuffix(user.following().totalCount()))
            }
        })

        viewModel.pinnedRepositories.observe(viewLifecycleOwner, Observer { response ->
            if (response != null && response.hasErrors().not()) {
                val data = response.data() ?: return@Observer
                profile_pinned_repositories.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                profile_pinned_repositories.adapter = RepositoryAdapter(data)
            }
        })

        profile_username.post {
            titleTextToTopHeight = dp2px(32f + 80f, resources) + profile_username.height
        }

        appbar.addOnOffsetChangedListener(this)

        profile_followers_text.setOnClickListener(this)
        profile_following_text.setOnClickListener(this)
        profile_repositories_text.setOnClickListener(this)
        profile_stars_text.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        appbar.removeOnOffsetChangedListener(this)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        if (verticalOffset == 0) {
            // Fully expanded
            if (toolbar.elevation != 0f) {
                ViewCompat.setElevation(toolbar, 0f)
            }
        } else {
            // Not fully expanded or collapsed
            val fourDpElevation = dp2px(4f, resources).toFloat()
            if (toolbar.elevation != fourDpElevation) {
                ViewCompat.setElevation(toolbar, fourDpElevation)
            }

            if (verticalOffset < -titleTextToTopHeight) {
                if (profile_toolbar_title.text != username) {
                    profile_toolbar_title.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_bottom))
                    profile_toolbar_title.text = username
                }
            } else {
                if (profile_toolbar_title.text != "") {
                    profile_toolbar_title.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_top))
                    profile_toolbar_title.text = ""
                }
            }
        }
    }

    override fun onClick(v: View?) {
        v ?: return
        when (v.id) {
            R.id.profile_followers_text -> {
                val bundle = Bundle().apply {
                    putString("login", login)
                    putString("users_type", UsersFragment.USER_TYPE_FOLLOWERS)
                    putString("username", username)
                }
                parentFragment?.findNavController()?.navigate(R.id.action_user_profile_to_users, bundle)
            }
            R.id.profile_following_text -> {
                val bundle = Bundle().apply {
                    putString("login", login)
                    putString("users_type", UsersFragment.USER_TYPE_FOLLOWING)
                    putString("username", username)
                }
                parentFragment?.findNavController()?.navigate(R.id.action_user_profile_to_users, bundle)
            }
            R.id.profile_repositories_text -> {
                val builder = RepositoriesFragmentArgs.Builder(login, RepositoriesFragment.REPOSITORY_TYPE_OWNED, username
                        ?: return)
                parentFragment?.findNavController()?.navigate(R.id.action_to_repositories, builder.build().toBundle())
            }
            R.id.profile_stars_text -> {
                val builder = RepositoriesFragmentArgs.Builder(login, RepositoriesFragment.REPOSITORY_TYPE_STARS, username
                        ?: return)
                parentFragment?.findNavController()?.navigate(R.id.action_to_repositories, builder.build().toBundle())
            }
        }
    }

}