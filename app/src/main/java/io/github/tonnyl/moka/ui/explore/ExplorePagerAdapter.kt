package io.github.tonnyl.moka.ui.explore

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.tonnyl.moka.ui.explore.developers.TrendingDevelopersFragment
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoriesFragment

class ExplorePagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = ITEM_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TrendingRepositoriesFragment.newInstance()
            else -> TrendingDevelopersFragment.newInstance()
        }
    }

    companion object {

        private const val ITEM_COUNT = 2

    }

}