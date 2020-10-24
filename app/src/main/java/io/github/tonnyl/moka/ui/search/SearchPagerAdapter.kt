package io.github.tonnyl.moka.ui.search

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.github.tonnyl.moka.ui.search.repositories.SearchedRepositoriesFragment
import io.github.tonnyl.moka.ui.search.users.SearchedUsersFragment

class SearchPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = ITEM_COUNT

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SearchedRepositoriesFragment.newInstance()
            else -> SearchedUsersFragment.newInstance()
        }
    }

    companion object {

        private const val ITEM_COUNT = 2

    }

}