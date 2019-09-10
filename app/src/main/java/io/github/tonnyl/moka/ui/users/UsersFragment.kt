package io.github.tonnyl.moka.ui.users

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
import io.github.tonnyl.moka.databinding.FragmentUsersBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs

class UsersFragment : Fragment(), ItemUserActions, EmptyViewActions, PagingNetworkStateActions {

    private val args by navArgs<UsersFragmentArgs>()

    private val viewModel by viewModels<UsersViewModel> {
        ViewModelFactory(args.login, args.usersType)
    }

    private val userAdapter: UserAdapter by lazy {
        UserAdapter(this@UsersFragment).apply {
            actions = this@UsersFragment
        }
    }

    private lateinit var binding: FragmentUsersBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUsersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            appbarLayout.toolbar.setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }

            appbarLayout.toolbar.title = context?.getString(
                when (args.usersType) {
                    UsersType.FOLLOWER -> {
                        R.string.users_followers_title

                    }
                    UsersType.FOLLOWING -> {
                        R.string.users_following_title
                    }
                },
                args.login
            )

            viewModel = this@UsersFragment.viewModel
            emptyViewActions = this@UsersFragment
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.pagedLoadStatus.observe(this, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    userAdapter.setNetworkState(Pair(it.direction, NetworkState.LOADED))
                }
                Status.ERROR -> {
                    userAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.error(it.resource.message))
                    )
                }
                Status.LOADING -> {
                    userAdapter.setNetworkState(Pair(it.direction, NetworkState.LOADING))
                }
                null -> {

                }
            }
        })

        viewModel.data.observe(this, Observer { list ->
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = userAdapter
                }
            }
            userAdapter.submitList(list)
        })

    }

    override fun openProfile(login: String) {
        val builder = ProfileFragmentArgs(login)
        findNavController().navigate(R.id.profile_fragment, builder.toBundle())
    }

    override fun followUserClicked(login: String, follow: Boolean) {

    }

    override fun retryInitial() {
        viewModel.refresh()
    }

    override fun doAction() {

    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

}