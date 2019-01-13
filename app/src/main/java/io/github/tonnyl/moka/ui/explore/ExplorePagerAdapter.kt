package io.github.tonnyl.moka.ui.explore

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.tonnyl.moka.R

class ExplorePagerAdapter(
        private val context: Context,
        fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    private val fragments = listOf(
            TrendingRepositoriesFragment.newInstance(),
            TrendingDevelopersFragment.newInstance()
    )

    private val titles = listOf(
            context.getString(R.string.explore_trending_repositories),
            context.getString(R.string.explore_trending_developers)
    )

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? = titles[position]

}