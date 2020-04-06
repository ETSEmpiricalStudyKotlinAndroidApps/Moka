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
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsFragmentArgs
import io.github.tonnyl.moka.ui.projects.ProjectsType
import io.github.tonnyl.moka.ui.prs.PullRequestsFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryEvent.*

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
                is ViewOwnersProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(args.login, it.type).toBundle()
                    )
                }
                is ViewWatchers -> {

                }
                is ViewStargazers -> {

                }
                is ViewForks -> {

                }
                is ViewIssues -> {
                    findNavController().navigate(
                        R.id.issues_fragment,
                        IssuesFragmentArgs(args.login, args.name).toBundle()
                    )
                }
                is ViewPullRequests -> {
                    findNavController().navigate(
                        R.id.prs_fragment,
                        PullRequestsFragmentArgs(args.login, args.name).toBundle()
                    )
                }
                is ViewProjects -> {
                    findNavController().navigate(
                        R.id.nav_projects,
                        ProjectsFragmentArgs(
                            args.login,
                            args.name,
                            ProjectsType.RepositoriesProjects
                        ).toBundle()
                    )
                }
                is ViewLicense -> {

                }
                is ViewBranches -> {

                }
                is ViewAllTopics -> {

                }
                is ViewReleases -> {

                }
                is ViewLanguages -> {

                }
                is ViewFiles -> {

                }
            }
        })

    }

}