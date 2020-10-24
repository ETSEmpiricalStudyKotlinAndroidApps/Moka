package io.github.tonnyl.moka.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentExploreBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.EventObserver
import io.github.tonnyl.moka.ui.MainNavigationFragment
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.explore.filters.FilterEvent
import io.github.tonnyl.moka.ui.explore.filters.TrendingFilterFragment

class ExploreFragment : MainNavigationFragment(), EmptyViewActions {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val viewModel by viewModels<ExploreViewModel> {
        MokaDataBase.getInstance(
            requireContext(),
            mainViewModel.currentUser.value?.id ?: 0L
        ).let {
            ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
        }
    }

    private lateinit var binding: FragmentExploreBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            mainViewModel = mainViewModel
            exploreViewModel = viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        binding.viewPager.adapter = ExplorePagerAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(
                if (position == 0) {
                    R.string.explore_trending_repositories
                } else {
                    R.string.explore_trending_developers
                }
            )
        }.attach()

        viewModel.filterEvent.observe(viewLifecycleOwner, EventObserver {
            if (it is FilterEvent.ShowFilters) {
                val sheet = TrendingFilterFragment.newInstance()
                sheet.show(childFragmentManager, TrendingFilterFragment::class.java.simpleName)
            }
        })
    }

    override fun retryInitial() {

    }

    override fun doAction() {

    }

}