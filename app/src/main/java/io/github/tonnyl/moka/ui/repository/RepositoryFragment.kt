package io.github.tonnyl.moka.ui.repository

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.Repository
import io.github.tonnyl.moka.databinding.FragmentRepositoryBinding
import io.github.tonnyl.moka.network.Resource
import io.github.tonnyl.moka.network.Status
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.issues.IssuesFragmentArgs
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs

class RepositoryFragment : Fragment() {

    private val args by navArgs<RepositoryFragmentArgs>()

    private val viewModel by viewModels<RepositoryViewModel> {
        ViewModelFactory(args)
    }

    private lateinit var binding: FragmentRepositoryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRepositoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragment?.findNavController()?.navigateUp()
        }

        binding.apply {
            viewModel = this@RepositoryFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        binding.repositoryBottomAppBar.inflateMenu(R.menu.fragment_repository_menu)

        val repoObserver: Observer<Resource<Repository>> = Observer { repository ->
            repository.data?.topics?.let { topicList ->
                binding.repositoryTopics.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    val adapter = RepositoryTopicAdapter()
                    this.adapter = adapter
                    adapter.submitList(topicList)
                }
            }
        }

        viewModel.userRepository.observe(viewLifecycleOwner, repoObserver)
        viewModel.organizationsRepository.observe(viewLifecycleOwner, repoObserver)

        viewModel.readmeHtml.observe(viewLifecycleOwner, Observer { resources ->
            when (resources.status) {
                Status.SUCCESS -> {
                    binding.repositoryReadmeContent.loadData(resources.data ?: return@Observer)
                }
                Status.ERROR -> {

                }
                Status.LOADING -> {

                }
            }
        })

        viewModel.userEvent.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                RepositoryEvent.VIEW_OWNERS_PROFILE -> {

                }
                RepositoryEvent.VIEW_WATCHERS -> {

                }
                RepositoryEvent.VIEW_STARGAZERS -> {

                }
                RepositoryEvent.VIEW_FORKS -> {

                }
                RepositoryEvent.VIEW_ISSUES -> {
                    val args = IssuesFragmentArgs(args.login, args.name)
                    parentFragment?.findNavController()
                        ?.navigate(R.id.issues_fragment, args.toBundle())
                }
                RepositoryEvent.VIEW_PULL_REQUESTS -> {
                    val args = PullRequestsFragmentArgs(args.login, args.name)
                    parentFragment?.findNavController()
                        ?.navigate(R.id.prs_fragment, args.toBundle())
                }
                RepositoryEvent.VIEW_PROJECTS -> {

                }
                RepositoryEvent.VIEW_LICENSE -> {

                }
            }
        })

    }

}