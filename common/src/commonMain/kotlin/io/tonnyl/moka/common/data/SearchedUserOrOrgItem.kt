package io.tonnyl.moka.common.data

import io.tonnyl.moka.graphql.fragment.OrganizationListItemFragment
import io.tonnyl.moka.graphql.fragment.UserListItemFragment

data class SearchedUserOrOrgItem(

    val user: UserListItemFragment? = null,

    val org: OrganizationListItemFragment? = null

)