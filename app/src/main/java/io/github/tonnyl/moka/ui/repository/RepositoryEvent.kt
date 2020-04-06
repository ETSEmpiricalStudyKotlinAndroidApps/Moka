package io.github.tonnyl.moka.ui.repository

import io.github.tonnyl.moka.ui.profile.ProfileType

sealed class RepositoryEvent {

    data class ViewOwnersProfile(
        val type: ProfileType
    ) : RepositoryEvent()

    object ViewWatchers : RepositoryEvent()

    object ViewStargazers : RepositoryEvent()

    object ViewForks : RepositoryEvent()

    object ViewIssues : RepositoryEvent()

    object ViewPullRequests : RepositoryEvent()

    object ViewProjects : RepositoryEvent()

    object ViewLicense : RepositoryEvent()

    object ViewBranches : RepositoryEvent()

    object ViewAllTopics : RepositoryEvent()

    object ViewReleases : RepositoryEvent()

    object ViewLanguages : RepositoryEvent()

    object ViewFiles : RepositoryEvent()

}