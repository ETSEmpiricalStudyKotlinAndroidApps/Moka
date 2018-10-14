package io.github.tonnyl.moka.data

import android.net.Uri
import android.os.Parcelable
import io.github.tonnyl.moka.OwnedRepositoriesQuery
import io.github.tonnyl.moka.StarredRepositoriesQuery
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class RepositoryAbstract(
        val createdAt: Date,
        val databaseId: Int?,
        val description: String?,
        val descriptionHTML: String?,
        val homepageUrl: Uri?,
        val id: String,
        val name: String,
        val nameWithOwner: String,
        val pushedAt: Date?,
        val resourcePath: Uri,
        val shortDescriptionHTML: String,
        val updatedAt: Date,
        val url: Uri,
        val issuesCount: Int,
        val pullRequestsCount: Int,
        val watchersCount: Int,
        val forksCount: Int,
        val stargazersCount: Int,
        val primaryLanguageColor: String?,
        val primaryLanguageName: String?,
        val viewerHasStarred: Boolean,
        val owner: RepositoryAbstractOwner
) : Parcelable {

    companion object {

        fun createFromOwnedRepositoryDataNode(
                node: OwnedRepositoriesQuery.Node
        ): RepositoryAbstract {
            val abstractRepository = node.fragments().abstractRepository()
            return RepositoryAbstract(
                    abstractRepository.createdAt(),
                    abstractRepository.databaseId(),
                    abstractRepository.description(),
                    abstractRepository.descriptionHTML(),
                    abstractRepository.homepageUrl(),
                    abstractRepository.id(),
                    abstractRepository.name(),
                    abstractRepository.nameWithOwner(),
                    abstractRepository.pushedAt(),
                    abstractRepository.resourcePath(),
                    abstractRepository.shortDescriptionHTML(),
                    abstractRepository.updatedAt(),
                    abstractRepository.url(),
                    abstractRepository.issues().totalCount(),
                    abstractRepository.pullRequests().totalCount(),
                    abstractRepository.watchers().totalCount(),
                    abstractRepository.forks().totalCount(),
                    abstractRepository.stargazers().totalCount(),
                    abstractRepository.primaryLanguage()?.color(),
                    abstractRepository.primaryLanguage()?.name(),
                    abstractRepository.viewerHasStarred(),
                    RepositoryAbstractOwner(
                            abstractRepository.owner().avatarUrl(),
                            abstractRepository.owner().id(),
                            abstractRepository.owner().login(),
                            abstractRepository.owner().resourcePath(),
                            abstractRepository.owner().url()
                    )
            )
        }

        fun createFromStarredRepositoryDataNode(
                node: StarredRepositoriesQuery.Node
        ): RepositoryAbstract {
            val abstractRepository = node.fragments().abstractRepository()
            return RepositoryAbstract(
                    abstractRepository.createdAt(),
                    abstractRepository.databaseId(),
                    abstractRepository.description(),
                    abstractRepository.descriptionHTML(),
                    abstractRepository.homepageUrl(),
                    abstractRepository.id(),
                    abstractRepository.name(),
                    abstractRepository.nameWithOwner(),
                    abstractRepository.pushedAt(),
                    abstractRepository.resourcePath(),
                    abstractRepository.shortDescriptionHTML(),
                    abstractRepository.updatedAt(),
                    abstractRepository.url(),
                    abstractRepository.issues().totalCount(),
                    abstractRepository.pullRequests().totalCount(),
                    abstractRepository.watchers().totalCount(),
                    abstractRepository.forks().totalCount(),
                    abstractRepository.stargazers().totalCount(),
                    abstractRepository.primaryLanguage()?.color(),
                    abstractRepository.primaryLanguage()?.name(),
                    abstractRepository.viewerHasStarred(),
                    RepositoryAbstractOwner(
                            abstractRepository.owner().avatarUrl(),
                            abstractRepository.owner().id(),
                            abstractRepository.owner().login(),
                            abstractRepository.owner().resourcePath(),
                            abstractRepository.owner().url()
                    )
            )
        }

    }

}