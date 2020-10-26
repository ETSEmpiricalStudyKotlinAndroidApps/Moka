package io.github.tonnyl.moka.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentUsersBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.users.ItemUserEvent.FollowUser
import io.github.tonnyl.moka.ui.users.ItemUserEvent.ViewProfile

class UsersFragment : Fragment(), EmptyViewActions {

    private val args by navArgs<UsersFragmentArgs>()

    private val viewModel by viewModels<UsersViewModel> {
        ViewModelFactory(args)
    }

    private val usersAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = UserAdapter(viewLifecycleOwner, viewModel)
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
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

        viewModel.usersResult.observe(viewLifecycleOwner) {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = usersAdapter
                }
            }
            usersAdapter.submitData(lifecycle, it)
        }

        viewModel.event.observe(viewLifecycleOwner) {
            when (val event = it.getContentIfNotHandled()) {
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(event.login, event.type).toBundle()
                    )
                }
                is FollowUser -> {

                }
            }
        }

    }

    override fun retryInitial() {
        usersAdapter.refresh()
    }

    override fun doAction() {

    }

}