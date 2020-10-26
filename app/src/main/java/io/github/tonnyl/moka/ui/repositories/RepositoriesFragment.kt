package io.github.tonnyl.moka.ui.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentRepositoriesBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.repositories.RepositoryItemEvent.*
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs

class RepositoriesFragment : Fragment(), EmptyViewActions {

    private val args by navArgs<RepositoriesFragmentArgs>()

    private val repositoriesViewModel by viewModels<RepositoriesViewModel> {
        ViewModelFactory(args)
    }

    private lateinit var binding: FragmentRepositoriesBinding

    private val repositoryAdapter by lazy {
        val adapter = RepositoryAdapter(viewLifecycleOwner, repositoriesViewModel)
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
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

        with(binding) {
            appbarLayout.toolbar.setNavigationOnClickListener {
                parentFragment?.findNavController()?.navigateUp()
            }

            appbarLayout.toolbar.title = context?.getString(
                when (args.repositoriesType) {
                    RepositoryType.STARRED -> {
                        R.string.repositories_stars
                    }
                    RepositoryType.OWNED -> {
                        R.string.repositories_owned
                    }
                },
                args.login
            )

            emptyViewActions = this@RepositoriesFragment
            viewModel = this@RepositoriesFragment.repositoriesViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        repositoriesViewModel.repositoriesResult.observe(viewLifecycleOwner) {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = repositoryAdapter
                }
            }

            repositoryAdapter.submitData(lifecycle, it)
        }

        repositoriesViewModel.event.observe(viewLifecycleOwner) {
            when (val event = it.getContentIfNotHandled()) {
                is ViewRepository -> {
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(
                            event.login,
                            event.repoName,
                            args.profileType
                        ).toBundle()
                    )
                }
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(event.login).toBundle()
                    )
                }
                is StarRepository -> {

                }
            }
        }

    }

    override fun retryInitial() {
        repositoryAdapter.refresh()
    }

    override fun doAction() {

    }

}