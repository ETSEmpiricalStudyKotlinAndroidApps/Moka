package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import io.github.tonnyl.moka.OrganizationQuery.Organization as RawOrganization

@Parcelize
data class Organization(

    /**
     * A URL pointing to the organization's public avatar.
     */
    val avatarUrl: Uri,

    /**
     * Identifies the primary key from the database.
     */
    val databaseId: Int?,

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
    val newTeamResourcePath: Uri,

    /**
     * The HTTP URL creating a new team
     */
    val newTeamUrl: Uri,

    /**
     * Returns how many more items this profile owner can pin to their profile.
     */
    val pinnedItemsRemaining: Int,

    /**
     * The HTTP path listing organization's projects
     */
    val projectsResourcePath: Uri,

    /**
     * The HTTP URL listing organization's projects
     */
    val projectsUrl: Uri,

    /**
     * The HTTP path for this organization.
     */
    val resourcePath: Uri,

    /**
     * The HTTP URL for this organization.
     */
    val url: Uri,

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
    val websiteUrl: Uri?,

    val repositoriesTotalCount: Int,

    val projectsTotalCount: Int

) : Parcelable {

    companion object {

        fun createFromRaw(data: RawOrganization?): Organization? = if (data == null) {
            null
        } else {
            Organization(
                data.avatarUrl(),
                data.databaseId(),
                data.description(),
                data.email(),
                data.id(),
                data.isVerified,
                data.location(),
                data.login(),
                data.name(),
                data.newTeamResourcePath(),
                data.newTeamUrl(),
                data.pinnedItemsRemaining(),
                data.projectsResourcePath(),
                data.projectsUrl(),
                data.resourcePath(),
                data.url(),
                data.viewerCanAdminister(),
                data.viewerCanChangePinnedItems(),
                data.viewerCanCreateProjects(),
                data.viewerCanCreateRepositories(),
                data.viewerCanCreateTeams(),
                data.viewerIsAMember(),
                data.websiteUrl(),
                data.repositories().totalCount(),
                data.projects().totalCount()
            )
        }

    }

}