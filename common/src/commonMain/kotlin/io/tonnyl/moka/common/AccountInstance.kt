package io.tonnyl.moka.common

import io.tonnyl.moka.common.network.ApolloGraphQLClient
import io.tonnyl.moka.common.network.api.*

expect class AccountInstance {

    val eventApi: EventApi
    val trendingApi: TrendingApi
    val userApi: UserApi
    val notificationApi: NotificationApi
    val commitApi: CommitApi
    val repositoryContentApi: RepositoryContentApi
    val repositoryApi: RepositoryApi
    val gitHubStatusApi: GitHubStatusApi

    val apolloGraphQLClient: ApolloGraphQLClient

}