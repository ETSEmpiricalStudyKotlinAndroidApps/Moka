package io.github.tonnyl.moka.ui.users

import android.os.Bundle
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
import io.github.tonnyl.moka.databinding.FragmentUsersBinding
import io.github.tonnyl.moka.ui.profile.UserProfileFragmentArgs
import kotlinx.android.synthetic.main.appbar_layout.*
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment(), ItemUserActions {

    private lateinit var viewModel: UsersViewModel

    private val args: UsersFragmentArgs by navArgs()

    private val adapter by lazy {
        UserAdapter().apply {
            actions = this@UsersFragment
        }
    }

    companion object {
        const val USER_TYPE_FOLLOWING = "following"
        const val USER_TYPE_FOLLOWERS = "followers"
    }

    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = args.login
        val userTypeArg = args.usersType
        val usernameArg = args.username

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        toolbar.title = context?.getString(if (userTypeArg == USER_TYPE_FOLLOWERS) R.string.users_followers_title else R.string.users_following_title, usernameArg)

        val userType = when (userTypeArg) {
            USER_TYPE_FOLLOWING -> UserType.FOLLOWING
            else -> UserType.FOLLOWER
        }
        val factory = ViewModelFactory(loginArg, userType)
        viewModel = ViewModelProviders.of(this, factory).get(UsersViewModel::class.java)

        recycler_view.adapter = adapter
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager

        viewModel.usersResults.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

    }

    override fun openProfile(login: String) {
        val builder = UserProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, builder.toBundle())
    }

    override fun followUserClicked(login: String, follow: Boolean) {

    }

}