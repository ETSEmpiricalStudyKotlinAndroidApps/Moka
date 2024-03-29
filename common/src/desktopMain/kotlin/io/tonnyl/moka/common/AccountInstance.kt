package io.tonnyl.moka.common

import io.tonnyl.moka.common.network.ApolloGraphQLClient
import io.tonnyl.moka.common.network.KtorClient
import io.tonnyl.moka.common.network.api.*

actual class AccountInstance(private val accessToken: String) {

    private val authenticatedKtorClient = KtorClient(
        requireAuth = true,
        accessToken = accessToken
    ).httpClient

    actual val eventApi = EventApi(ktorClient = authenticatedKtorClient)
    actual val trendingApi = TrendingApi(ktorClient = KtorClient.unauthenticatedKtorClient)
    actual val userApi = UserApi(ktorClient = authenticatedKtorClient)
    actual val notificationApi = NotificationApi(ktorClient = authenticatedKtorClient)
    actual val commitApi = CommitApi(ktorClient = authenticatedKtorClient)
    actual val repositoryContentApi = RepositoryContentApi(ktorClient = authenticatedKtorClient)
    actual val repositoryApi = RepositoryApi(authenticatedKtorClient = authenticatedKtorClient)
    actual val gitHubStatusApi = GitHubStatusApi(ktorClient = KtorClient.unauthenticatedKtorClient)

    actual val apolloGraphQLClient = ApolloGraphQLClient(accessToken = accessToken)

}