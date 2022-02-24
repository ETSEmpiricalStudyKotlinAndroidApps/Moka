package io.tonnyl.moka.common.data

import io.tonnyl.moka.graphql.type.RepositoryOrder
import io.tonnyl.moka.graphql.type.RepositoryPrivacy
import io.tonnyl.moka.graphql.type.StarOrder

sealed interface RepositoriesQueryOption {

    data class Starred(
        val order: StarOrder
    ) : RepositoriesQueryOption

    data class Owned(
        val isAffiliationCollaborator: Boolean,
        val isAffiliationOwner: Boolean,
        val order: RepositoryOrder,
        val privacy: RepositoryPrivacy?
    ) : RepositoriesQueryOption

    data class Forks(
        val order: RepositoryOrder
    ) : RepositoriesQueryOption

}