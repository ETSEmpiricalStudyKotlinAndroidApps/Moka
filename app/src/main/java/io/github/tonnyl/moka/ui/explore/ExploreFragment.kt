package io.github.tonnyl.moka.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.databinding.FragmentExploreBinding
import io.github.tonnyl.moka.db.MokaDataBase
import io.github.tonnyl.moka.ui.MainViewModel
import io.github.tonnyl.moka.ui.explore.filters.TrendingFilterFragment
import io.github.tonnyl.moka.ui.profile.ProfileFragmentArgs
import io.github.tonnyl.moka.ui.repository.RepositoryFragmentArgs
import io.github.tonnyl.moka.ui.ViewModelFactory as MainViewModelFactory

class ExploreFragment : Fragment(), ExploreRepositoryActions, View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private lateinit var viewModel: ExploreViewModel
    private lateinit var mainViewModel: MainViewModel

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

        mainViewModel = ViewModelProviders.of(requireActivity(), MainViewModelFactory())
            .get(MainViewModel::class.java)
        MokaDataBase.getInstance(requireContext(), mainViewModel.userId.value ?: return).let {
            viewModel = ViewModelProviders.of(
                this,
                ViewModelFactory(it.trendingDevelopersDao(), it.trendingRepositoriesDao())
            ).get(ExploreViewModel::class.java)
        }

        with(binding) {
            mainViewModel = this@ExploreFragment.mainViewModel
            lifecycleOwner = this@ExploreFragment.viewLifecycleOwner
        }

        val adapter = ExplorePagerAdapter(requireContext(), childFragmentManager)
        binding.viewPager.adapter = adapter

        binding.tabLayout.setupWithViewPager(binding.viewPager)

        mainViewModel.loginUserProfile.observe(this, Observer { data ->
            if (data != null) {
                binding.mainSearchBar.mainSearchBarAvatar.setOnClickListener(this@ExploreFragment)
            } else {

            }
        })

        binding.fab.setOnClickListener(this@ExploreFragment)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab -> {
                val sheet = TrendingFilterFragment.newInstance()
                sheet.show(childFragmentManager, TrendingFilterFragment::class.java.simpleName)
            }
            R.id.empty_content_action_text -> {

            }
            R.id.empty_content_retry_button -> {

            }
        }
    }

    override fun onRefresh() {
        viewModel.refreshTrendingDevelopers()
        viewModel.refreshTrendingRepositories()
    }

    override fun openProfile(login: String) {
        val builder = ProfileFragmentArgs(login)
        findNavController().navigate(R.id.action_to_profile, builder.toBundle())
    }

    override fun openRepository(login: String, repositoryName: String) {
        val builder = RepositoryFragmentArgs(login, repositoryName)
        findNavController().navigate(R.id.action_to_repository, builder.toBundle())
    }

}