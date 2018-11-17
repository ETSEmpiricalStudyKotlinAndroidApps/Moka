package io.github.tonnyl.moka.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.R
import kotlinx.android.synthetic.main.fragment_users.*

class UsersFragment : Fragment() {

    private lateinit var viewModel: UsersViewModel

    private val adapter by lazy {
        UserAdapter()
    }

    companion object {
        const val USER_TYPE_FOLLOWING = "following"
        const val USER_TYPE_FOLLOWERS = "followers"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = UsersFragmentArgs.fromBundle(arguments).login
        val userTypeArg = UsersFragmentArgs.fromBundle(arguments).usersType
        val usernameArg = UsersFragmentArgs.fromBundle(arguments).username

        toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        toolbar_title.text = context?.getString(if (userTypeArg == USER_TYPE_FOLLOWERS) R.string.users_followers_title else R.string.users_following_title, usernameArg)

        swipe_refresh.setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.indigo, null),
                ResourcesCompat.getColor(resources, R.color.teal, null),
                ResourcesCompat.getColor(resources, R.color.lightBlue, null),
                ResourcesCompat.getColor(resources, R.color.yellow, null),
                ResourcesCompat.getColor(resources, R.color.orange, null)
        )

        val userType = when (userTypeArg) {
            USER_TYPE_FOLLOWING -> UserType.FOLLOWING
            else -> UserType.FOLLOWER
        }
        val factory = ViewModelFactory(loginArg, userType)
        viewModel = ViewModelProviders.of(this, factory).get(UsersViewModel::class.java)

        recycler_view.adapter = adapter
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = layoutManager

        recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && layoutManager.findFirstCompletelyVisibleItemPosition() != 0 && appbar?.elevation == 0f) {
                    ViewCompat.setElevation(appbar, resources.getDimension(R.dimen.toolbar_elevation))
                } else if (dy < 0 && layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && appbar != null && appbar.elevation != 0f) {
                    ViewCompat.setElevation(appbar, 0f)
                }
            }

        })

        viewModel.usersResults.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })

    }

}