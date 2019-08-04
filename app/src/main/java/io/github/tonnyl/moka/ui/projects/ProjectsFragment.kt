package io.github.tonnyl.moka.ui.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.tonnyl.moka.databinding.FragmentProjectsBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.network.NetworkState
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class ProjectsFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: ProjectsViewModel

    private lateinit var binding: FragmentProjectsBinding

    private val args: ProjectsFragmentArgs by navArgs()

    private val projectAdapter: ProjectAdapter by lazy(LazyThreadSafetyMode.NONE) {
        ProjectAdapter()
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

        val loginOfMySelf = mainViewModel.login.value
        val login = args.login ?: loginOfMySelf ?: ""

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                login == loginOfMySelf,
                MokaDataBase.getInstance(
                    requireContext(),
                    mainViewModel.userId.value ?: return
                ).projectsDao(),
                args.repositoryName
            )
        ).get(ProjectsViewModel::class.java)

        mainViewModel.login.observe(viewLifecycleOwner, Observer {
            viewModel.refreshData(login, true)
        })

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = projectAdapter
        }

        viewModel.loadStatusLiveData.observe(this, Observer {
            when (it.initial?.status) {
                Status.SUCCESS -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                Status.ERROR -> {
                    binding.swipeRefresh.isRefreshing = false
                }
                Status.LOADING -> {
                    binding.swipeRefresh.isRefreshing = true
                }
                null -> {

                }
            }

            when (it.after?.status) {
                Status.SUCCESS -> {
                    projectAdapter.setAfterNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    projectAdapter.setAfterNetworkState(NetworkState.error(it.after.message))
                }
                Status.LOADING -> {
                    projectAdapter.setAfterNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }

            when (it.before?.status) {
                Status.SUCCESS -> {
                    projectAdapter.setBeforeNetworkState(NetworkState.LOADED)
                }
                Status.ERROR -> {
                    projectAdapter.setBeforeNetworkState(NetworkState.error(it.before.message))
                }
                Status.LOADING -> {
                    projectAdapter.setBeforeNetworkState(NetworkState.LOADING)
                }
                null -> {

                }
            }
        })

        viewModel.data.observe(this, Observer {
            projectAdapter.submitList(it)

            showHideEmptyView(it.isEmpty())
        })

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData(login, true)
        }
    }

    private fun showHideEmptyView(show: Boolean) {
        if (show) {
            binding.emptyContent.root.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyContent.root.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

}