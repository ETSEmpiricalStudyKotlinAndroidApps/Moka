package io.github.tonnyl.moka.data

import io.github.tonnyl.moka.fragment.Organization.PinnedItems.Node.Companion.pinnableItem
import io.github.tonnyl.moka.fragment.PinnableItem.Companion.asGist
import io.github.tonnyl.moka.fragment.PinnableItem.Companion.repositoryListItemFragment
import io.github.tonnyl.moka.queries.OrganizationQuery.Data.Organization.Companion.organization
import kotlinx.datetime.Instant
import io.github.tonnyl.moka.queries.OrganizationQuery.Data.Organization as RawOrganization

data class Organization(

    /**
     * A URL pointing to the organization's public avatar.
     */
    val avatarUrl: String,

    /**
     * The organization's public profile description.
     */
    val description: String?,

    /**
     * The organization's public email.
     */
    val email: String?,

    val id: String,

    /**
     * Whether the organization has verified its profile email and website.
     */
    val isVerified: Boolean,

    /**
     * The organization's public profile location.
     */
    val location: String?,

    /**
     * The organization's login name.
     */
    val login: String,

    /**
     * The organization's public profile name.
     */
    val name: String?,

    /**
     * The HTTP path creating a new team
     */
    val newTeamResourcePath: String,

    /**
     * The HTTP URL creating a new team
     */
    val newTeamUrl: String,

    /**
     * Returns how many more items this profile owner can pin to their profile.
     */
    val pinnedItemsRemaining: Int,

    /**
     * The HTTP path listing organization's projects
     */
    val projectsResourcePath: String,

    /**
     * The HTTP URL listing organization's projects
     */
    val projectsUrl: String,

    /**
     * The HTTP path for this organization.
     */
    val resourcePath: String,

    /**
     * The HTTP URL for this organization.
     */
    val url: String,

    /**
     * Organization is adminable by the viewer.
     */
    val viewerCanAdminister: Boolean,

    /**
     * Can the viewer pin repositories and gists to the profile?
     */
    val viewerCanChangePinnedItems: Boolean,

    /**
     * Can the current viewer create new projects on this owner.
     */
    val viewerCanCreateProjects: Boolean,

    /**
     * Viewer can create repositories on this organization
     */
    val viewerCanCreateRepositories: Boolean,

    /**
     * Viewer can create teams on this organization.
     */
    val viewerCanCreateTeams: Boolean,

    /**
     * Viewer is an active member of this organization.
     */
    val viewerIsAMember: Boolean,

    /**
     * The organization's public profile URL.
     */
    val websiteUrl: String?,

    val repositoriesTotalCount: Int,

    val projectsTotalCount: Int,

    val pinnedItems: MutableList<PinnableItem>?,

    val createdAt: Instant,

    val updatedAt: Instant

)

fun RawOrganization?.toNullableOrganization(): Organization? {
    this ?: return null

    val org = organization() ?: return null

    val pinnableItems = mutableListOf<PinnableItem>()
    org.pinnedItems.nodes?.map { node ->
        node?.pinnableItem()?.let { fragment ->
            fragment.asGist()?.let {
                pinnableItems.add(it.toGist())
            } ?: fragment.repositoryListItemFragment()?.let {
                pinnableItems.add(it.toNonNullRepositoryItem())
            }
        }
    }

    return Organization(
        org.avatarUrl,
        org.description,
        org.email,
        org.id,
        org.isVerified,
        org.location,
        org.login,
        org.name,
        org.newTeamResourcePath,
        org.newTeamUrl,
        org.pinnedItemsRemaining,
        "",
        "",
        org.resourcePath,
        org.url,
        org.viewerCanAdminister,
        org.viewerCanChangePinnedItems,
        org.viewerCanCreateProjects,
        org.viewerCanCreateRepositories,
        org.viewerCanCreateTeams,
        org.viewerIsAMember,
        org.websiteUrl,
        0,
        0,
        pinnableItems,
        org.createdAt,
        org.updatedAt
    )
}