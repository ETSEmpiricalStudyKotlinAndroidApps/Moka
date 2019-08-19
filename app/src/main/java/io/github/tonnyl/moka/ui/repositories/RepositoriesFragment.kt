package io.github.tonnyl.moka.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentRepositoriesBinding
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs

class RepositoriesFragment : Fragment(), ItemRepositoryActions, EmptyViewActions,
    PagingNetworkStateActions {

    private lateinit var viewModel: RepositoriesViewModel

    private val args: RepositoriesFragmentArgs by navArgs()

    private lateinit var binding: FragmentRepositoriesBinding

    private val repositoryAdapter by lazy {
        RepositoryAdapter(this@RepositoriesFragment).apply {
            repositoryActions = this@RepositoriesFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoriesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loginArg = args.login
        val repositoriesTypeArg = args.repositoriesType

        viewModel = ViewModelProviders.of(this, ViewModelFactory(loginArg, repositoriesTypeArg))
            .get(RepositoriesViewModel::class.java)

        with(binding) {
            appbarLayout.toolbar.setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }

            appbarLayout.toolbar.title = context?.getString(
                when (repositoriesTypeArg) {
                    RepositoryType.STARRED -> {
                        R.string.repositories_stars
                    }
                    RepositoryType.OWNED -> {
                        R.string.repositories_owned
                    }
                },
                loginArg
            )

            emptyViewActions = this@RepositoriesFragment
            viewModel = this@RepositoriesFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.data.observe(this, Observer { list ->
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = repositoryAdapter
                }
            }
            repositoryAdapter.submitList(list)
        })

        viewModel.pagedLoadStatus.observe(this, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    repositoryAdapter.setNetworkState(Pair(it.direction, NetworkState.LOADED))
                }
                Status.ERROR -> {
                    repositoryAdapter.setNetworkState(
                        Pair(it.direction, NetworkState.error(it.resource.message))
                    )
                }
                Status.LOADING -> {
                    repositoryAdapter.setNetworkState(Pair(it.direction, NetworkState.LOADING))
                }
                null -> {

                }
            }
        })

    }

    override fun openRepository(login: String, repositoryName: String) {
        val repositoryArgs = RepositoryFragmentArgs(login, repositoryName).toBundle()
        findNavController().navigate(R.id.action_to_repository, repositoryArgs)
    }

    override fun openProfile(login: String) {
        val profileArgs = ProfileFragmentArgs(login).toBundle()
        findNavController().navigate(R.id.action_to_profile, profileArgs)
    }

    override fun starRepositoryClicked(repositoryNameWithOwner: String, star: Boolean) {

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