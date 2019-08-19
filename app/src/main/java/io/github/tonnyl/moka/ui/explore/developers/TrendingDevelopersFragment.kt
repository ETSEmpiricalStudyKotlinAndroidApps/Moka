package io.github.tonnyl.moka.ui.explore.developers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
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
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.profile.ProfileType
import io.github.tonnyl.moka.widget.ListCategoryDecoration
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class TrendingDevelopersFragment : Fragment(), TrendingDeveloperAction,
    EmptyViewActions {

    private val mainViewModel: MainViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
    }
    private lateinit var viewModel: ExploreViewModel

    private lateinit var binding: FragmentExploreDevelopersBinding

    private val developerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        TrendingDeveloperAdapter().apply {
            actions = this@TrendingDevelopersFragment
        }
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

        MokaDataBase.getInstance(requireContext(), mainViewModel.userId.value ?: return).let {
            viewModel = ViewModelProviders.of(
                requireParentFragment(),
                ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
            ).get(ExploreViewModel::class.java)
        }

        binding.apply {
            viewModel = this@TrendingDevelopersFragment.viewModel
            emptyViewActions = this@TrendingDevelopersFragment
            lifecycleOwner = viewLifecycleOwner
        }

        viewModel.developersLocalData.observe(this, Observer {
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
        })

        binding.swipeRefresh.setOnRefreshListener {
            retryInitial()
        }
    }

    override fun openProfile(developer: TrendingDeveloper) {
        val args = ProfileFragmentArgs(developer.username, ProfileType.USER).toBundle()
        findNavController().navigate(R.id.action_timeline_to_user_profile, args)
    }

    override fun openRepository(developer: TrendingDeveloper) {

    }

    override fun retryInitial() {
        viewModel.refreshTrendingDevelopers()
    }

    override fun doAction() {

    }

}