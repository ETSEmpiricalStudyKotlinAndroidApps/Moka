package io.github.tonnyl.moka.ui.explore.developers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.data.TrendingDeveloper
import io.github.tonnyl.moka.databinding.FragmentExploreDevelopersBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.explore.ExploreTimeSpanType
import io.github.tonnyl.moka.ui.explore.ExploreViewModel
import io.github.tonnyl.moka.ui.explore.ViewModelFactory
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperItemEvent.ViewProfile
import io.github.tonnyl.moka.ui.explore.developers.TrendingDeveloperItemEvent.ViewRepository
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.widget.ListCategoryDecoration

class TrendingDevelopersFragment : Fragment(), EmptyViewActions {

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

    private lateinit var binding: FragmentExploreDevelopersBinding

    private val developerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TrendingDeveloperAdapter(viewLifecycleOwner, viewModel)
    }

    companion object {

        fun newInstance(): TrendingDevelopersFragment = TrendingDevelopersFragment()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreDevelopersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = this@TrendingDevelopersFragment.viewModel
            emptyViewActions = this@TrendingDevelopersFragment
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.developersLocalData.observe(viewLifecycleOwner) {
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

                    adapter = developerAdapter
                }

                developerAdapter.submitList(it)
            }
        }

        viewModel.developerEvent.observe(viewLifecycleOwner) {
            when (val event = it.getContentIfNotHandled()) {
                is ViewProfile -> {
                    findNavController().navigate(
                        R.id.profile_fragment,
                        ProfileFragmentArgs(
                            event.developer.username,
                            getProfileTypeByDeveloper(event.developer)
                        ).toBundle()
                    )
                }
                is ViewRepository -> {
                    findNavController().navigate(
                        R.id.repository_fragment,
                        RepositoryFragmentArgs(
                            event.developer.username,
                            event.developer.repository.name,
                            getProfileTypeByDeveloper(event.developer)
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
        viewModel.refreshTrendingDevelopers()
    }

    override fun doAction() {

    }

    private fun getProfileTypeByDeveloper(developer: TrendingDeveloper): ProfileType {
        return when (developer.type) {
            "user" -> ProfileType.USER
            "organization" -> ProfileType.ORGANIZATION
            else -> ProfileType.NOT_SPECIFIED
        }
    }

}