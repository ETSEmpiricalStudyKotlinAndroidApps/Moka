package io.github.tonnyl.moka.ui.explore.repositories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentExploreRepositoriesBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.explore.ExploreTimeSpanType
import io.github.tonnyl.moka.ui.explore.ExploreViewModel
import io.github.tonnyl.moka.ui.explore.ViewModelFactory
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoryItemEvent.ViewProfile
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoryItemEvent.ViewRepository
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class TrendingRepositoriesFragment : Fragment(), EmptyViewActions {

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val viewModel by viewModels<ExploreViewModel>(
        ownerProducer = {
            requireParentFragment()
        },
        factoryProducer = {
            MokaDataBase.getInstance(
                requireContext(),
                mainViewModel.currentUser.value?.id ?: 0L
            ).let {
                ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
            }
        }
    )

    private lateinit var binding: FragmentExploreRepositoriesBinding

    private val repositoryAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TrendingRepositoryAdapter(viewLifecycleOwner, viewModel)
    }

    companion object {

        fun newInstance(): TrendingRepositoriesFragment = TrendingRepositoriesFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreRepositoriesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = this@TrendingRepositoriesFragment.viewModel
            emptyViewActions = this@TrendingRepositoriesFragment
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.repositoriesLocalData.observe(viewLifecycleOwner) {
            with(binding.recyclerView) {
                if (adapter == null) {
                    addItemDecoration(
                        ListCategoryDecoration(
                            this,
                            getString(
                                R.string.explore_filter_info,
                                viewModel.queryData.value?.second?.name
                                    ?: getString(R.string.explore_trending_filter_all_languages),
                                getString(
                                    when (viewModel.queryData.value?.first) {
                                        ExploreTimeSpanType.WEEKLY -> {
                                            R.string.explore_trending_filter_time_span_weekly
                                        }
                                        ExploreTimeSpanType.MONTHLY -> {
                                            R.string.explore_trending_filter_time_span_monthly
                                        }
                                        // including ExploreTimeSpanType.DAILY
                                        else -> {
                                            R.string.explore_trending_filter_time_span_daily
                                        }
                                    }
                                )
                            )
                        )
                    )

                    adapter = repositoryAdapter
                }

                repositoryAdapter.submitList(it)
            }
        }

        viewModel.repositoryEvent.observe(viewLifecycleOwner) {
            when (val event = it.getContentIfNotHandled()) {
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(
                            event.repository.author,
                            ProfileType.NOT_SPECIFIED
                        ).toBundle()
                    )
                }
                is ViewRepository -> {
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(
                            event.repository.author,
                            event.repository.name,
                            ProfileType.NOT_SPECIFIED
                        ).toBundle()
                    )
                }
            }
        }

        binding.swipeRefresh.setOnRefreshListener {
            retryInitial()
        }
    }

    override fun retryInitial() {
        viewModel.refreshTrendingRepositories()
    }

    override fun doAction() {

    }

}