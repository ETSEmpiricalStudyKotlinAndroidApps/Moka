package io.github.tonnyl.moka.ui.search

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.tonnyl.moka.R
import io.github.tonnyl.moka.ui.search.page.SearchedRepositoriesFragment
import io.github.tonnyl.moka.ui.search.users.SearchedUsersFragment

class SearchPagerAdapter(
        private val context: Context,
        fm: FragmentManager
) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> SearchedRepositoriesFragment.newInstance()
        else -> SearchedUsersFragment.newInstance()
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> context.getString(R.string.search_tab_repositories)
        else -> context.getString(R.string.search_tab_users)
    }

}