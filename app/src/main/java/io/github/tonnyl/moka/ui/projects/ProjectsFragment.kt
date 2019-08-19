package io.github.tonnyl.moka.ui.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.databinding.FragmentProjectsBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.PagingNetworkStateActions
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class ProjectsFragment : Fragment(), PagingNetworkStateActions,
    EmptyViewActions {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: ProjectsViewModel

    private lateinit var binding: FragmentProjectsBinding

    private val args: ProjectsFragmentArgs by navArgs()

    private val projectAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProjectAdapter(this@ProjectsFragment)
    }

    private val login: String
        get() {
            val loginOfMySelf = mainViewModel.login.value
            return args.login ?: loginOfMySelf ?: ""
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProjectsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                login == mainViewModel.login.value,
                MokaDataBase.getInstance(
                    requireContext(),
                    mainViewModel.userId.value ?: return
                ).projectsDao(),
                args.repositoryName
            )
        ).get(ProjectsViewModel::class.java)

        binding.apply {
            emptyViewActions = this@ProjectsFragment
            viewModel = this@ProjectsFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        mainViewModel.login.observe(viewLifecycleOwner, Observer {
            viewModel.refreshData(login, true)
        })

        viewModel.previousNextLoadStatusLiveData.observe(this, Observer {
            when (it.resource?.status) {
                Status.SUCCESS -> {
                    projectAdapter.setNetworkState(
                        Pair(
                            it.direction,
                            NetworkState.LOADED
                        )
                    )
                }
                Status.ERROR -> {
                    projectAdapter.setNetworkState(
                        Pair(
                            it.direction,
                            NetworkState.error(it.resource.message)
                        )
                    )
                }
                Status.LOADING -> {
                    projectAdapter.setNetworkState(
                        Pair(
                            it.direction,
                            NetworkState.LOADING
                        )
                    )
                }
                null -> {

                }
            }
        })

        viewModel.data.observe(this, Observer {
            with(binding.recyclerView) {
                if (adapter == null) {
                    adapter = projectAdapter
                }
            }

            projectAdapter.submitList(it)
        })

        binding.swipeRefresh.setOnRefreshListener {
            retryInitial()
        }
    }

    override fun retryLoadPreviousNext() {
        viewModel.retryLoadPreviousNext()
    }

    override fun retryInitial() {
        viewModel.refreshData(login, true)
    }

    override fun doAction() {

    }

}