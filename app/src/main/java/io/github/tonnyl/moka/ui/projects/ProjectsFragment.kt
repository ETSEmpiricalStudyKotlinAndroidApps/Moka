package io.github.tonnyl.moka.ui.projects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import io.github.tonnyl.moka.MokaApp
import io.github.tonnyl.moka.databinding.FragmentProjectsBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.LoadStateAdapter
import io.github.tonnyl.moka.ui.MainViewModel

class ProjectsFragment : Fragment(), EmptyViewActions {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<ProjectsViewModel> {
        ViewModelFactory(
            login == mainViewModel.currentUser.value?.login,
            args,
            requireContext().applicationContext as MokaApp
        )
    }

    private lateinit var binding: FragmentProjectsBinding

    private val args: ProjectsFragmentArgs by navArgs()

    private val projectAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val adapter = ProjectAdapter()
        adapter.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter(adapter::retry),
            footer = LoadStateAdapter(adapter::retry)
        )
        adapter
    }

    private val login: String
        get() {
            val loginOfMySelf = mainViewModel.currentUser.value?.login
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

        binding.apply {
            emptyViewActions = this@ProjectsFragment
            viewModel = this@ProjectsFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        mainViewModel.currentUser.observe(viewLifecycleOwner, Observer {
            viewModel.login = it.login
            viewModel.userId = it.id

            viewModel.projectsResult.observe(viewLifecycleOwner, Observer {
                with(binding.recyclerView) {
                    if (adapter == null) {
                        adapter = projectAdapter
                    }
                }

                projectAdapter.submitData(lifecycle, it)
            })
        })

        binding.swipeRefresh.setOnRefreshListener {
            retryInitial()
        }
    }

    override fun retryInitial() {
        projectAdapter.refresh()
    }

    override fun doAction() {

    }

}