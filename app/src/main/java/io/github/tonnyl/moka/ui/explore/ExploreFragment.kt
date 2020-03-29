package io.github.tonnyl.moka.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.github.tonnyl.moka.databinding.FragmentExploreBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.EmptyViewActions
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

        val adapter = ExplorePagerAdapter(requireContext(), childFragmentManager)
        binding.viewPager.adapter = adapter

        binding.tabLayout.setupWithViewPager(binding.viewPager)

        viewModel.filterEvent.observe(viewLifecycleOwner, Observer {
            when (it.getContentIfNotHandled()) {
                is FilterEvent.ShowFilters -> {
                    val sheet = TrendingFilterFragment.newInstance()
                    sheet.show(childFragmentManager, TrendingFilterFragment::class.java.simpleName)
                }
            }
        })
    }

    override fun retryInitial() {

    }

    override fun doAction() {

    }

}