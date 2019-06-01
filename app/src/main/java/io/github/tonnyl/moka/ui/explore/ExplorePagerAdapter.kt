package io.github.tonnyl.moka.ui.explore

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.explore.developers.TrendingDevelopersFragment
import io.github.tonnyl.moka.ui.explore.repositories.TrendingRepositoriesFragment

class ExplorePagerAdapter(
        private val context: Context,
        fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val titles = arrayOf(R.string.explore_trending_repositories, R.string.explore_trending_developers)

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> TrendingRepositoriesFragment.newInstance()
        else -> TrendingDevelopersFragment.newInstance()
    }

    override fun getCount(): Int = titles.size

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> context.getString(titles[0])
        else -> context.getString(titles[1])
    }

}