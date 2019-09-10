package io.github.tonnyl.moka.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentExploreBinding
import io.github.tonnyl.moka.ui.EmptyViewActions
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.SearchBarActions
import io.github.tonnyl.moka.ui.explore.filters.TrendingFilterFragment
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs

class ExploreFragment : Fragment(), SearchBarActions,
    EmptyViewActions, ExploreActions {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private val mainViewModel by activityViewModels<MainViewModel>()

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

        binding.apply {
            exploreActions = this@ExploreFragment
            mainViewModel = this@ExploreFragment.mainViewModel
            lifecycleOwner = this@ExploreFragment.viewLifecycleOwner
        }

        val adapter = ExplorePagerAdapter(requireContext(), childFragmentManager)
        binding.viewPager.adapter = adapter

        binding.tabLayout.setupWithViewPager(binding.viewPager)
    }

    override fun onResume() {
        super.onResume()
        if (!::drawer.isInitialized) {
            drawer = parentFragment?.parentFragment?.view?.findViewById(R.id.drawer_layout)
                ?: return
        }
        toggle = ActionBarDrawerToggle(
            parentFragment?.activity,
            drawer,
            binding.mainSearchBar.mainSearchBarToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onPause() {
        super.onPause()

        drawer.removeDrawerListener(toggle)
    }

    override fun openSearch() {
        findNavController().navigate(R.id.search_fragment)
    }

    override fun openAccountDialog() {
        mainViewModel.currentUser.value?.let {
            val args = ProfileFragmentArgs(it.login).toBundle()
            findNavController().navigate(R.id.profile_fragment, args)
        }
    }

    override fun retryInitial() {

    }

    override fun doAction() {

    }

    override fun openFilters() {
        val sheet = TrendingFilterFragment.newInstance()
        sheet.show(childFragmentManager, TrendingFilterFragment::class.java.simpleName)
    }

}