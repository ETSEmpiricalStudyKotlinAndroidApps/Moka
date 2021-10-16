package io.github.tonnyl.moka.data.item

import io.github.tonnyl.moka.data.UserItem
import io.tonnyl.moka.graphql.fragment.OrganizationListItemFragment

data class SearchedUserOrOrgItem(

    val user: UserItem? = null,

    val org: SearchedOrganizationItem? = null

)

data class SearchedOrganizationItem(

    /**
     * A URL pointing to the organization's public avatar.
     */
    val avatarUrl: String,

    /**
     * The organization's public profile description.
     */
    val description: String?,

    /**
     * The organization's public profile description rendered to HTML.
     */
    val descriptionHTML: String?,

    val id: String,

    /**
     * Whether the organization has verified its profile email and website.
     */
    val isVerified: Boolean,

    /**
     * The organization's login name.
     */
    val login: String,

    /**
     * The organization's public profile name.
     */
    val name: String?,

    /**
     * The HTTP URL for this organization.
     */
    val url: String,

    /**
     * Viewer is an active member of this organization.
     */
    val viewerIsAMember: Boolean,

    /**
     * The organization's public profile URL.
     */
    val websiteUrl: String?

)

fun OrganizationListItemFragment.toNonNullSearchedOrganizationItem(): SearchedOrganizationItem {
    return SearchedOrganizationItem(
        avatarUrl,
        description,
        descriptionHTML,
        id,
        isVerified,
        login,
        name,
        url,
        viewerIsAMember,
        websiteUrl
    )
}